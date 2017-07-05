package com.farhansoftware.developershub.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategorySelection extends AppCompatActivity {

    private LinearLayout root;
    private SharedPreferences pref;
    private String json;
    private String selectedcat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        root=(LinearLayout)findViewById(R.id.category_root);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        json=pref.getString("categoryjson","");
        if(json.isEmpty())finish();

        selectedcat=pref.getString("selectedcat","0");

        try {
            Log.e("select before",selectedcat);
            selectedcat=selectedcat.substring(0,selectedcat.length()-1);
            Log.e("select after",selectedcat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray array=new JSONArray(json);
            for(int i=0;i<array.length();i++){
                final JSONObject object=array.getJSONObject(i);
                View view = LayoutInflater.from(CategorySelection.this).inflate(R.layout.select_cate_item, null);
                TextView tv=(TextView)view.findViewById(R.id.category_name);
                tv.setText(object.getString(Server.RETURN_CATEGORY_NAME));
                SwitchCompat switchCompat=(SwitchCompat)view.findViewById(R.id.switchonoff);

                final String id=object.getString(Server.RETURN_CATEGORY_ID);
                if(selectedcat.contains(" "+id+",")){
                    switchCompat.setChecked(true);
                }
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            addCategory(id);
                        }else {
                            removeCategory(id);
                        }
                        saveSelection();
                    }
                });

                root.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeCategory(String id) {
        selectedcat=selectedcat.replace(" "+id+",","");
    }

    private void addCategory(String id) {
        selectedcat+=" "+id+",";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveSelection() {
        Log.e("cate",selectedcat);
        pref.edit().putString("selectedcat",selectedcat+"0").commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }
}
