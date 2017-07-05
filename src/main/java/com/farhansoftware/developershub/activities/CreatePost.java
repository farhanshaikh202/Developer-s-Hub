package com.farhansoftware.developershub.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.CategSpinnerAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.Toast;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class CreatePost extends AppCompatActivity {


    private RTManager rtManager;
    private RTEditText rtEditText;
    private Spinner categoryspin;
    private EditText ptitle,downlodet;
    private LinearLayout ssroot;
    private ImageButton addss;
    private SharedPreferences pref;
    private CategSpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set theme before calling setContentView!
        setTheme(R.style.RTE_ThemeLight);

        setContentView(R.layout.activity_create_post);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));

        categoryspin = (Spinner) findViewById(R.id.post_up_categor);
        ptitle = (EditText) findViewById(R.id.post_up_title);
        ssroot = (LinearLayout) findViewById(R.id.post_up_ss_root);
        addss = (ImageButton) findViewById(R.id.post_up_ss_add);
        downlodet=(EditText)findViewById(R.id.create_post_dnld_url);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = pref.getString("categoryjson", "");
        if (json.isEmpty()) finish();
        try {
            JSONArray jsarr = new JSONArray(json);
            adapter=new CategSpinnerAdapter(CreatePost.this, jsarr);
            categoryspin.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create RTManager
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

// register toolbar
        final ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);
        RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar_paragraph);
        RTToolbar rtToolbar1 = (RTToolbar) findViewById(R.id.rte_toolbar_character);

        if (rtToolbar != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar);
        }
        if (rtToolbar1 != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar1);
        }


// register editor & set text
        rtEditText = (RTEditText) findViewById(R.id.rtEditText);
        rtManager.registerEditor(rtEditText, true);
        rtEditText.setRichTextEditing(true, "<i>Write description here...</i>");


        rtEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) toolbarContainer.setVisibility(View.VISIBLE);
                else toolbarContainer.setVisibility(View.GONE);
            }
        });

        addss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageList.size() <= 10) {
                    EasyImage.openGallery(CreatePost.this, EasyImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
                } else Toast.show(getApplicationContext(), "Maximum 10 images");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                Toast.show(getApplicationContext(), "Error");
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
                addPhotos(imagesFiles);

            }


        });
    }

    ArrayList<File> imageList = new ArrayList<>();

    private void addPhotos(List<File> imagesFiles) {

        for (int i = 0; i < imagesFiles.size(); i++) {

            imageList.add(imagesFiles.get(i));

            ImageView iv = new ImageView(getApplicationContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            Picasso.with(getApplicationContext()).load(imagesFiles.get(i)).noFade().into(iv);
            iv.setMaxWidth(200);
            iv.setMaxHeight(200);
            iv.setPadding(3, 3, 3, 3);
            ssroot.addView(iv);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        rtManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Post").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 0:
                savePost();
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePost() {

        try {
            final Loading load = new Loading();
            MultipartUploadRequest request=new MultipartUploadRequest(getApplicationContext(), Server.SERVER_URL+Server.CREATE_POST_URL);
            request.setUtf8Charset()
                    .addParameter("createpost","")
                    .addParameter(Server.PARAM_USER_ID,pref.getString(Server.PARAM_USER_ID,""))
                    .addParameter(Server.RETURN_CATEGORY_ID,adapter.getId(categoryspin.getSelectedItemPosition()))
                    .addParameter(Server.RETURN_POST_TITLE,ptitle.getText().toString())
                    .addParameter(Server.RETURN_POST_DECRIPTION,rtEditText.getText(RTFormat.HTML))
                    .addParameter(Server.RETURN_POST_DOWNLOAD_URL,downlodet.getText().toString())
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            load.updatemsg("Posting..."+uploadInfo.getProgressPercent()+"%");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                            Toast.show(context,exception.getMessage());
                            load.hide();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            load.hide();
                            try {
                                JSONObject object=new JSONObject(serverResponse.getBodyAsString());
                                if(object.getString("success").equals("yes")){
                                    if(imageList.size()>0)uploadImages(object.getInt("id"));
                                }else Toast.show(context,object.getString("error"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            load.hide();
                        }
                    });
            load.show(CreatePost.this,"Uploading post");
            request.startUpload();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    int i=0;
    private void uploadImages(final int postid) {
        try {
            final Loading load = new Loading();
            MultipartUploadRequest request=new MultipartUploadRequest(getApplicationContext(), Server.SERVER_URL+Server.POST_IMAGE_UPLOAD_URL);
            request.setUtf8Charset()
                    .addParameter("uploadimage","")
                    .addParameter(Server.PARAM_USER_ID,pref.getString(Server.PARAM_USER_ID,""))
                    .addParameter(Server.RETURN_POST_ID,String.valueOf(postid))
                    .addFileToUpload(imageList.get(i).getAbsolutePath(),"image")
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            load.updatemsg("Image "+(i+1)+"..."+uploadInfo.getProgressPercent()+"%");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                            Toast.show(context,exception.getMessage());
                            load.hide();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            load.hide();

                            try {
                                JSONObject object=new JSONObject(serverResponse.getBodyAsString());
                                if (object.getString("success").equals("yes")) {
                                    if(i<imageList.size()-1)
                                    {
                                        ++i;
                                        uploadImages(postid);
                                    }
                                    else{
                                        finish();
                                        Toast.show(context,"Post uploaded");
                                    }
                                } else {
                                    Toast.show(getApplicationContext(),object.getString("error"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            load.hide();
                        }
                    });
            load.show(CreatePost.this,"Uploading post");
            request.startUpload();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
