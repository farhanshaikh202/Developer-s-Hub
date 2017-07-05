package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.TAG;
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

public class Login extends AppCompatActivity {


    private Button loginbtn;
    private TextView forgotpswd;
    private Button registerbtn;
    private TextView registelink;
    private EditText email;
    private EditText pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.login_email_edittext);
        pswd = (EditText) findViewById(R.id.login_pswd_edittext);

        loginbtn = (Button) findViewById(R.id.login_login_button);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail(email.getText().toString())) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                } else if (!validatePswd(pswd.getText().toString())) {
                    pswd.setError("Password length must be between 6-15 characters long");
                    pswd.requestFocus();
                } else {
                    attemptLogin(email.getText().toString(), pswd.getText().toString());
                }
            }
        });

        forgotpswd = (TextView) findViewById(R.id.login_forgotpswd_textview);
        forgotpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });

        registelink = (TextView) findViewById(R.id.login_register_textview);
        registelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent GoToRegisterPage = new Intent(Login.this, Register.class);
                startActivity(GoToRegisterPage);

            }
        });
    }

    private void attemptLogin(final String email, final String pswd) {

        if (Internet.isAvail(getApplicationContext())) {
            final Loading loading = new Loading();
            loading.show(Login.this, "Please wait...");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_LOGIN, "")
                                .add(Server.PARAM_EMAIL, email)
                                .addEncoded(Server.PARAM_PASSWORD, pswd)
                                .build();

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.LOGIN_URL)
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(Login.this, "Server Error :\n" + e.getMessage());
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
            Alert.show(Login.this, "Please connect to Internet");
        }

    }

    private void loginResponse(String res) {

        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref.edit().putBoolean("loginskipped", false).apply();
                pref.edit().putString(Server.PARAM_USER_ID, object.getString(Server.PARAM_RETURN_ID)).commit();
                pref.edit().putString(Server.PARAM_USER_PHOTO, object.getString(Server.PARAM_USER_PHOTO)).commit();
                pref.edit().putString(Server.PARAM_EMAIL,object.getString(Server.PARAM_EMAIL)).commit();
                pref.edit().putString(Server.PARAM_USERNAME, object.getString(Server.PARAM_USERNAME)).commit();
                pref.edit().putBoolean("islogin", true).apply();
                finish();
                Intent in=new Intent(getApplicationContext(), MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            } else {
                email.setError("Incorrect Email or Password");
                email.requestFocus();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean validateEmail(String s) {
        String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Pattern pattern = Pattern.compile(EmailPattern);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }


    private boolean validatePswd(String pswd) {
        if (pswd != null && pswd.length() > 5 && pswd.length() < 16) {
            return true;
        } else {
            return false;
        }
    }
}