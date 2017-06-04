package com.vns.webstore.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.webstore.webstore.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by root on 14/05/2017.
 */

public class TranslateDialog extends DialogFragment{
    TextView resultText;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.translate, null);
        resultText = (TextView)view.findViewById(R.id.translatedText);
        resultText.setSelected(true);
        final EditText wordText = (EditText)view.findViewById(R.id.word);
        Button translateBtn = (Button)view.findViewById(R.id.translateBtn);
        Button closeBtn = (Button)view.findViewById(R.id.closetranslateBtn);
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordText.getText().toString();
                if(!word.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText("");
                        }
                    });
                    HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/translate?word=" + word, new HttpRequestListener() {
                        @Override
                        public void onRecievedData(Object data, ErrorCode errorCode) {
                            try {
                                final JSONObject result = new JSONObject(data.toString());
                                callBack(result.getString("meaning"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },null);
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);

        return builder.create();
    }

    private  void callBack(final String text){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.setText(text);
            }
        });

    }
}
