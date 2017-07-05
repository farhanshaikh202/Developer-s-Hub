package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {

    private EditText crntpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        crntpass = (EditText)findViewById(R.id.change_pass_crntpass_et);
        final EditText newpass = (EditText)findViewById(R.id.change_pass_newpass_et);
        final EditText renewpass = (EditText)findViewById(R.id.change_pass_renewpass_et);

        Button submit = (Button)findViewById(R.id.change_pass_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                  if (!validatePswd(crntpass.getText().toString())) {
                    crntpass.setError("Password length must be between 6-10 characters long");
                    crntpass.requestFocus();
                }
               else if (!validatePswd(newpass.getText().toString())) {
                    newpass.setError("Password length must be between 6-10 characters long");
                    newpass.requestFocus();
                }
                  else if(!renewpass.getText().toString().equals(newpass.getText().toString()))
                {
                    renewpass.setError("Password doesn't match");
                    renewpass.requestFocus();
                } else {
                    changePassword(crntpass.getText().toString(),newpass.getText().toString());
                }
            }
        });

    }

    private void changePassword(final String crntpass, final String newpass) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String em=pref.getString(Server.PARAM_EMAIL,"");
        if (Internet.isAvail(getApplicationContext())) {
            final Loading loading = new Loading();
            loading.show(ChangePassword.this, "Verifying...");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("changepwd", "")
                                .add(Server.PARAM_EMAIL, em)
                                .add("oldpwd",crntpass)
                                .add("newpwd",newpass)
                                .build();

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.CHANGE_PWD_URL)
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ChangePassword.this, "Server Error :\n" + e.getMessage());
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
            Alert.show(ChangePassword.this, "Please connect to Internet");
        }
    }
    private void loginResponse(String res) {

        Log.e("json",res);
        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                Toast.show(getApplicationContext(),"Password changed");
                finish();
            } else {

                if(object.getString("error").equals("1")) {
                    crntpass.setError("Incorrect password");
                    crntpass.requestFocus();
                }else {
                    Toast.show(getApplicationContext(),"Unable to change password, Try Again");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean validatePswd(String pswd) {
        if (pswd != null && pswd.length()>6 && pswd.length() < 16) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)finish();
        return super.onOptionsItemSelected(item);

    }
}
