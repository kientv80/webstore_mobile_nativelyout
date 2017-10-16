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

import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.service.SettingsService;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    String serviceUrl = null;
    JSONObject jsettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                }
                if ("text".equals(settingItem.getString("type"))) {
                    value = new EditText(this);
                    value.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                    value.setId(settingItem.getInt("id"));
                }
                settingContainer.addView(label);
                settingContainer.addView(value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            saveSettings();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    settings.put(item);
                } else if (v instanceof EditText) {
                    JSONObject item = new JSONObject();
                    item.put("id", v.getId());
                    item.put("value", ((EditText) v).getText());
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
            SettingsService.getInstance().loadSettings();
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
