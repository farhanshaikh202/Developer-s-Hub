package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.TAG;
import com.farhansoftware.developershub.utils.Internet;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

import static android.R.attr.password;

public class Register extends AppCompatActivity {

    private TextView GoToLogin;
    private Button registerbtn;
    private EditText nametext;
    private EditText email;
    private EditText pswrd;
    private EditText repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nametext = (EditText)findViewById(R.id.register_name_edittext);
        email = (EditText)findViewById(R.id.register_emailid_edittext);
        pswrd = (EditText)findViewById(R.id.register_password_edittext);
        repassword = (EditText)findViewById(R.id.register_repswd_edittext);

        registerbtn = (Button)findViewById(R.id.register_register_button);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isvalidateName(nametext.getText().toString()))
                {
                    nametext.setError("Enter your Name");
                    nametext.requestFocus();
                }
               else if (!validateEmail(email.getText().toString())) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                } else if (!validatePswd(pswrd.getText().toString())) {
                    pswrd.setError("Password length must be between 6-10 characters long");
                    pswrd.requestFocus();
                } else if(!pswrd.getText().toString().equals(repassword.getText().toString()))
                {
                    repassword.setError("Password doesn't match");
                    repassword.requestFocus();
                } else {
                    attemptRegister();
                }
            }
        });

        GoToLogin = (TextView) findViewById(R.id.register_login_textview);
        GoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent GoToLoginPage = new Intent(Register.this,Login.class);
                startActivity(GoToLoginPage);
            }
        });
    }

    private void attemptRegister() {
        final String name=nametext.getText().toString();
        final String emailid=email.getText().toString();
        final String pass=pswrd.getText().toString();

        if (Internet.isAvail(getApplicationContext())) {
            final Loading loading = new Loading();
            loading.show(Register.this, "Please wait...");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_VERIFY, "x")
                                .add(Server.PARAM_EMAIL, emailid)
                                .add(Server.PARAM_USERNAME,name)
                                .build();
                        //Log.d(com.farhansoftware.developershub.custom.TAG.APP_NAME, "POST PARAMS :: " + formBody.toString());

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.VERIFY_OTP_URL)
                                .post(formBody)
                                .build();

                        //Log.d(TAG.APP_NAME, "URL :: " + request.toString());
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(Register.this, "Server Error :\n" + e.getMessage());
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
                                        otpResponse(res,name,emailid,pass);
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
            Alert.show(Register.this, "Please connect to Internet");
        }

    }

    private void otpResponse(String res,String name,String emailid,String pass) {
        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                String otp = object.getString(Server.PARAM_OTP);
                Intent intent = new Intent(getApplicationContext(), OTPverify.class);
                intent.putExtra("name",name);
                intent.putExtra("pass",pass);
                intent.putExtra("email",emailid);
                intent.putExtra("otp",otp);
                finish();
                startActivity(intent);
            } else if((object.getString("error")).equals("1")){
                //email exists
                email.setError("Email id already exists");
                email.requestFocus();
            }else {//mail not sent
                Alert.show(Register.this, "Can't send Verification email");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isvalidateName(String nametxt) {
        if (!nametxt.isEmpty() && nametxt.length() > 4 ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateEmail(String s) {
        String EmailPattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Pattern pattern =Pattern.compile(EmailPattern);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }

    private boolean validatePswd(String pswd) {
        if (pswd != null && pswd.length()>5 && pswd.length() < 11) {
            return true;
        } else {
            return false;
        }
    }
}