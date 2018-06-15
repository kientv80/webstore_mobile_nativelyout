package com.vns.webstore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.SettingsService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.ui.dialog.FBLoginDialog;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    String serviceUrl = null;
    JSONObject jsettings = null;
    Map<Integer, String> idNameMapping = null;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    boolean loggedIn = false;
    FBLoginDialog fbLoginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        idNameMapping = new HashMap<>();
        initUI();

    }

    private void initUI() {
        GridLayout settingContainer = (GridLayout) findViewById(R.id.content_settings);
        settingContainer.removeAllViews();
        String settings = getIntent().getStringExtra("settings");
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_setting_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveSettings();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            jsettings = new JSONObject(settings);
            TextView title = (TextView) findViewById(R.id.setting_title);
            title.setText(jsettings.getString("title"));
            serviceUrl = jsettings.getString("serviceUrl");
            JSONArray settingsParams = jsettings.getJSONArray("settings");
            for (int i = 0; i < settingsParams.length(); i++) {
                JSONObject settingItem = (JSONObject) settingsParams.get(i);
                TextView label = new TextView(this);
                label.setText(settingItem.get("label").toString());
                label.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                View value = null;
                if ("checkbox".equals(settingItem.getString("type"))) {
                    value = new CheckBox(this);
                    value.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                    value.setId(settingItem.getInt("id"));
                    ((CheckBox) value).setChecked(settingItem.getBoolean("value"));
                    idNameMapping.put(settingItem.getInt("id"),settingItem.getString("name"));
                }
                if ("text".equals(settingItem.getString("type"))) {
                    value = new EditText(this);
                    value.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                    value.setId(settingItem.getInt("id"));
                    idNameMapping.put(settingItem.getInt("id"),settingItem.getString("name"));
                }
                settingContainer.addView(label);
                settingContainer.addView(value);
            }
            Button btnSave = new Button(this);

            btnSave.setText("Save Settings");
            btnSave.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>click save");
                    if(!loggedIn) {
                        fbLoginDialog = new FBLoginDialog();
                        fbLoginDialog.show(getFragmentManager(), null);
                    }else{
                        try {
                            saveFBProfile(null);
                            saveSettings();
                            if(fbLoginDialog != null)
                                fbLoginDialog.dismiss();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            settingContainer.addView(btnSave);
            callbackManager = CallbackManager.Factory.create();
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(
                        AccessToken oldAccessToken,
                        AccessToken currentAccessToken) {
                    System.out.println(".........................ok");
                    // Set the access token using
                    // currentAccessToken when it's loaded or set.
                }
            };

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(
                        Profile oldProfile,
                        Profile currentProfile) {
                    try {
                        if(currentProfile != null) {
                            saveFBProfile(currentProfile);
                            saveSettings();
                        }
                        if(fbLoginDialog != null)
                            fbLoginDialog.dismiss();
                        finish();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            };

            if(AccessToken.getCurrentAccessToken() != null)
                loggedIn = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveFBProfile(Profile currentProfile) throws JSONException {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(currentProfile == null)
            currentProfile = Profile.getCurrentProfile();
        JSONObject profile = new JSONObject();
        if(accessToken != null && currentProfile != null) {
            String userId = accessToken.getUserId();
            String acToken = accessToken.getToken();
            Set<String> permissions = accessToken.getPermissions();
            Set<String> declinedPermissions = accessToken.getDeclinedPermissions();
            profile.put("permissions",permissions);
            profile.put("declinedPermissions", declinedPermissions);
            String name = currentProfile.getName();
            String firstName = currentProfile.getFirstName();
            String lastName = currentProfile.getLastName();
            String avatar = currentProfile.getLinkUri().getPath();
            profile.put("id", userId);
            profile.put("name", name);
            profile.put("avatar", avatar);
            profile.put("token", acToken);
            profile.put("firstName", firstName);
            profile.put("lastName", lastName);
            List<Pair<String,String>> params = new ArrayList<>();
            params.add(new Pair<String, String>("profile",profile.toString()));
            HttpClientHelper.executeHttpPostRequest(AppConfigService.DOMAIN + "/saveprofile",null,params);
            LocalStorageHelper.saveToFile("profileinfo",profile.toString());
        } else if(currentProfile == null){
            LocalStorageHelper.saveToFile("profileinfo","");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void onBackPressed() {
        /*
        try {
            saveSettings();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        super.onBackPressed();
    }

    private void saveSettings() throws JSONException {
        JSONArray settings = new JSONArray();
        try {
            GridLayout settingContainer = (GridLayout) findViewById(R.id.content_settings);
            for (int i = 0; i < settingContainer.getChildCount(); i++) {
                View v = settingContainer.getChildAt(i);
                if (v instanceof CheckBox) {
                    JSONObject item = new JSONObject();
                    item.put("id", v.getId());
                    item.put("value", ((CheckBox) v).isChecked());
                    item.put("name",idNameMapping.get(v.getId()));
                    settings.put(item);
                } else if (v instanceof EditText) {
                    JSONObject item = new JSONObject();
                    item.put("id", v.getId());
                    item.put("value", ((EditText) v).getText());
                    item.put("name",idNameMapping.get(v.getId()));
                    settings.put(item);
                }
            }
            jsettings.put("settings",settings);
            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<String, String>("settings", jsettings.toString()));
            HttpClientHelper.executeHttpPostRequest(serviceUrl, new HttpRequestListener() {
                @Override
                public void onRecievedData(Object data, final ErrorCode errorCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (errorCode.getErrorCode().equals(ErrorCode.ERROR_CODE.SUCCESSED))
                                Toast.makeText(getApplicationContext(), R.string.save_settings_successfull, Toast.LENGTH_LONG);
                            else
                                Toast.makeText(getApplicationContext(), R.string.save_settings_failed, Toast.LENGTH_LONG);
                        }

                        ;
                    });

                }
            }, params);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
            ActivityLogService.getInstance().logUserActivity(new UserActivity("SAVE_CATE_SETTING", "SETTING", settings.toString()));
        }
    }

    private JSONObject getJSONById(int id, JSONArray items) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            if (((JSONObject) items.get(i)).getInt("id") == id) {
                return (JSONObject) items.get(i);
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }
}
