package com.farhansoftware.developershub.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.utils.Internet;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.MultipartUploadTask;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.aprilapps.easyphotopicker.EasyImage;

public class EditProfile extends AppCompatActivity implements CropHandler {

    EditText nameet;
    TextView pwdet;
    Button savebtn;
    CircleImageView imageView;
    CropParams mCropParams;
    private SharedPreferences pref;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = pref.getString(Server.PARAM_EMAIL, "");

        imageView = (CircleImageView) findViewById(R.id.edit_profile_user_pic);
        nameet = (EditText) findViewById(R.id.edit_profile_name_edittext);
        pwdet = (TextView) findViewById(R.id.edit_profil_pwd_edittext);
        savebtn = (Button) findViewById(R.id.edit_profile_save_button);

        pwdet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EditProfile.this, ChangePassword.class);
                startActivity(in);
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename(nameet.getText().toString());
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EasyImage.openGallery(EditProfile.this, EasyImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
                setNewImage();
            }
        });

        nameet.setText(pref.getString(Server.PARAM_USERNAME,""));
        Picasso.with(this).load(pref.getString(Server.PARAM_USER_PHOTO,"")).into(imageView);

        this.getCropParams();

        mCropParams.outputFormat = Bitmap.CompressFormat.PNG.toString();
        mCropParams.outputX = 500;
        mCropParams.outputY = 500;

        isStoragePermissionGranted();

    }

    String TAG="FileManager";
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    final Loading loading = new Loading();
    private void rename(final String s) {



        if (Internet.isAvail(getApplicationContext())) {
            if(newimage!=null){
                upload_flag=true;
                uploadMultipart(newimage.getPath(),email);
            }

            loading.show(EditProfile.this, "Updating...");

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("rename", "")
                                .add(Server.PARAM_EMAIL, email)
                                .add("name",s)
                                .build();

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.RENAME_PROFILE_URL)
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(EditProfile.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {

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
            Alert.show(EditProfile.this, "Please connect to Internet");
        }
    }

    boolean upload_flag=false;
    private void loginResponse(String res) {

        try {
            JSONObject object = new JSONObject(res);
            if (((String) object.getString(Server.PARAM_SUCCESS)).equals("yes")) {
                pref.edit().putString(Server.PARAM_USERNAME,nameet.getText().toString()).commit();
                if(!upload_flag) {
                    loading.hide();
                    Toast.show(getApplicationContext(), "Updated");
                    finish();
                }
            } else {
                if(object.getString("error").equals("1")) {
                    nameet.setError("Error");
                    nameet.requestFocus();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void setNewImage() {

        try {
            // MUST!! Clear Last Cached Image
            CropHelper.clearCachedCropFile(mCropParams.uri);
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
            builder.setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        Intent intent = CropHelper.buildCaptureIntent(mCropParams.uri);
                        startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                    } else {
                        Intent intent = CropHelper.buildCropFromGalleryIntent(mCropParams);
                        startActivityForResult(intent, CropHelper.REQUEST_CROP);
                    }
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Toast.show(getContext(),e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (this.getCropParams() != null)
            CropHelper.clearCachedCropFile(mCropParams.uri);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(this, requestCode, resultCode, data);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);

    }

    Uri newimage=null;

    @Override
    public void onPhotoCropped(Uri uri) {

        newimage=uri;
        Picasso.with(getContext()).load(uri).into(imageView);

    }


    @Override
    public void onCropCancel() {

    }

    @Override
    public void onCropFailed(String message) {

    }

    @Override
    public CropParams getCropParams() {
        mCropParams = new CropParams();
        return mCropParams;
    }

    @Override
    public Activity getContext() {
        return this;
    }

    public void uploadMultipart(String path,String email) {



        //Uploading code
        try {
            final String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            MultipartUploadRequest uploader = new MultipartUploadRequest(this, uploadId, Server.SERVER_URL+Server.POFILE_IMAGE_UPLOAD_URL)
                    .addFileToUpload(path, "image","dh.png") //Adding file
                    .addParameter("email", email)//Adding text parameter to the request
                    .addParameter("image","nullx")
                    .addParameter("uid",uploadId)
                    .setMaxRetries(2);
            uploader.setDelegate(new UploadStatusDelegate() {
                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {

                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                    Toast.show(context,exception.getMessage());
                    loading.hide();
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                    Log.e(TAG,serverResponse.getBodyAsString());
                    Log.e(TAG,serverResponse.toString());
                    try {

                        JSONObject object = new JSONObject(serverResponse.getBodyAsString());
                        if (!((Boolean) object.getBoolean("error"))) {
                            pref.edit().putString(Server.PARAM_USER_PHOTO,object.getString("url")).commit();
                            Toast.show(getApplicationContext(), "Updated");
                            finish();
                        } else {
                            Toast.show(getApplicationContext(), "Error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    upload_flag=false;
                    loading.hide();
                }

                @Override
                public void onCancelled(Context context, UploadInfo uploadInfo) {
                    loading.hide();
                }
            });
            uploader.startUpload();



        } catch (Exception exc) {
            Toast.show(this, exc.getMessage());
        }
    }
}
