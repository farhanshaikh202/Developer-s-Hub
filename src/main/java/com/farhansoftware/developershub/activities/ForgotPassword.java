package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.utils.Internet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassword extends AppCompatActivity {

    private EditText frgtpswrd_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frgtpswrd_email = (EditText) findViewById(R.id.frgt_pswrd_email_et);
        Button sbmt = (Button) findViewById(R.id.frgt_pswrd_submit_btn);

        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail(frgtpswrd_email.getText().toString())) {
                    frgtpswrd_email.setError("Invalid Email");
                    frgtpswrd_email.requestFocus();
                } else {
                    requestPassword(frgtpswrd_email.getText().toString());
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)finish();
        return super.onOptionsItemSelected(item);

    }

    private void requestPassword(final String s) {
        if (Internet.isAvail(getApplicationContext())) {
            final Loading loading = new Loading();
            loading.show(ForgotPassword.this, "Verifying...");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("forgotpwd", "")
                                .add(Server.PARAM_EMAIL, s)
                                .build();

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.FORGOT_PWD_URL)
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ForgotPassword.this, "Server Error :\n" + e.getMessage());
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
                                        loginResponse(res);
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
            Alert.show(ForgotPassword.this, "Please connect to Internet");
        }
    }

    private boolean validateEmail(String s) {
        String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Pattern pattern = Pattern.compile(EmailPattern);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }

    private void loginResponse(String res) {

        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                Toast.show(getApplicationContext(),"Check Your Inbox");
                finish();
            } else {
                if(object.getString("error").equals("1")) {
                    frgtpswrd_email.setError("Email not registered");
                    frgtpswrd_email.requestFocus();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}