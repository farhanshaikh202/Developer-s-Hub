package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.TAG;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.utils.Internet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTPverify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);


        TextView tv= (TextView) findViewById(R.id.otp_verify_text_info);
        tv.setText(Html.fromHtml("OTP is sent to your email id <b>"+getIntent().getExtras().getString("email")+"</b>.\nComplete registration process by entering OTP below"));
        final EditText et=(EditText)findViewById(R.id.otp_verify_number_box);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et.getText().length()==6){
                    String otp=getIntent().getExtras().getString("otp");
                    if(et.getText().toString().equals(otp)){
                        attemptRegister();
                    }else {
                        Toast.show(getApplicationContext(),"OTP does not match");
                    }
                }
            }
        });
    }

    private void attemptRegister() {
        if (Internet.isAvail(getApplicationContext())) {
            final Loading loading = new Loading();
            loading.show(OTPverify.this, "Completing Registration");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {
                        String name=getIntent().getExtras().getString("name");
                        String email=getIntent().getExtras().getString("email");
                        String pwd=getIntent().getExtras().getString("pass");

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_REGISTER, "")
                                .add(Server.PARAM_USERNAME,name)
                                .add(Server.PARAM_EMAIL, email)
                                .add(Server.PARAM_PASSWORD, pwd)
                                .build();

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.REGISTER_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(OTPverify.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        loading.hide();
                                        registerResponse(res);
                                    }
                                };
                                runOnUiThread(runnable1);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread th = new Thread(runnable1);
            th.start();
        } else {
            Alert.show(OTPverify.this, "Please connect to Internet");
        }
    }

    private void registerResponse(String res) {
        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref.edit().putBoolean("loginskipped", false).apply();
                String name=getIntent().getExtras().getString("name");
                String email=getIntent().getExtras().getString("email");
                pref.edit().putString(Server.PARAM_USER_PHOTO, object.getString(Server.PARAM_USER_PHOTO)).commit();
                pref.edit().putString(Server.PARAM_USER_ID, object.getString(Server.PARAM_RETURN_ID)).commit();
                pref.edit().putString(Server.PARAM_EMAIL,email).commit();
                pref.edit().putString(Server.PARAM_USERNAME, name).commit();
                pref.edit().putBoolean("islogin", true).apply();
                finish();
                Intent in=new Intent(getApplicationContext(), MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            } else {
                Alert.show(OTPverify.this,"Oops, Registration failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
