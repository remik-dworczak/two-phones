package com.vedaco.twophones;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.view.View.OnClickListener;
import android.view.View;
import android.net.Uri;
import java.io.DataOutputStream;
import java.io.IOException;


public class LandingPageActivity extends Activity  {
    private Button callBtn;
    private Button dialBtn;
    private EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            execCommand("touch testFile");
        }catch(IOException e){
            // just swallow :)
        }

        number = findViewById(R.id.phoneNumber);
        callBtn = findViewById(R.id.call);
        dialBtn =  findViewById(R.id.dial);
        MyPhoneListener phoneListener = new MyPhoneListener();

        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
        callBtn.setOnClickListener(new OnClickListener() {
           @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                try {
                    String uri = "tel:"+number.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                    startActivity(callIntent);
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"Your call has failed...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        dialBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                try {
                    String uri = "tel:"+number.getText().toString();
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));

                    startActivity(dialIntent);
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"Your call has failed...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
    private class MyPhoneListener extends PhoneStateListener {
        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(LandingPageActivity.this, incomingNumber + " calls you",
                            Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(LandingPageActivity.this, "on call...",
                            Toast.LENGTH_LONG).show();
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (onCall == true) {
                        Toast.makeText(LandingPageActivity.this, "restart app after call",
                                Toast.LENGTH_LONG).show();
                        // restart our application
                        Intent restart = getBaseContext().getPackageManager().
                                getLaunchIntentForPackage(getBaseContext().getPackageName());
                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(restart);
                        onCall = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // Just an example
    private Boolean execCommand(String command) throws IOException {
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}