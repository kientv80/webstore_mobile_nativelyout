package com.vns.webstore.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.webstore.webstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DynamicDialog extends DialogFragment {
    private String data = null;
    Map<Integer,String> idNameMapping = new HashMap<>();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.content_settings, null);
        GridLayout settingContainer = (GridLayout) view.findViewById(R.id.content_settings);
        settingContainer.removeAllViews();


        try {
            JSONObject jsettings = new JSONObject(data);
            //TextView title = (TextView) view.findViewById(R.id.setting_title);
            //title.setText(jsettings.getString("title"));
            String serviceUrl = jsettings.getString("serviceUrl");
            JSONArray settingsParams = jsettings.getJSONArray("settings");
            for (int i = 0; i < settingsParams.length(); i++) {
                JSONObject settingItem = (JSONObject) settingsParams.get(i);
                TextView label = new TextView(view.getContext());
                label.setText(settingItem.get("label").toString());
                label.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                View value = null;
                if ("checkbox".equals(settingItem.getString("type"))) {
                    value = new CheckBox(view.getContext());
                    value.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                    value.setId(settingItem.getInt("id"));
                    ((CheckBox) value).setChecked(settingItem.getBoolean("value"));
                    idNameMapping.put(settingItem.getInt("id"),settingItem.getString("name"));
                }
                if ("text".equals(settingItem.getString("type"))) {
                    value = new EditText(view.getContext());
                    value.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                    value.setId(settingItem.getInt("id"));
                    idNameMapping.put(settingItem.getInt("id"),settingItem.getString("name"));
                }
                settingContainer.addView(label);
                settingContainer.addView(value);
                System.out.println("count================" + i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setView(view);
        return builder.create();
    }
    public void setData(String data){
        this.data = data;
    }
}
