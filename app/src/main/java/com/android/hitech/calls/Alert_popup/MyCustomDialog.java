package com.android.hitech.calls.Alert_popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.hitech.calls.R;

public class MyCustomDialog extends Activity {
    TextView tv_client;
    Button dialog_ok,close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        try {
            tv_client   = (TextView) findViewById(R.id.tv_client);
            dialog_ok   = (Button) findViewById(R.id.dialog_ok);
            close=(Button)findViewById(R.id.button);
            dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddCall1.class);
                    startActivity(i);
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
