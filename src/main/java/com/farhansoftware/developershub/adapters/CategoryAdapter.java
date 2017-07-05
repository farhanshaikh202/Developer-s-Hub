package com.farhansoftware.developershub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Farhan on 03-02-2017.
 */

public class CategoryAdapter extends BaseAdapter {
    Context context;
    JSONArray array;

    public CategoryAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return array.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getColor(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_CATEGORY_COLOR);
    }

    public String getId(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_CATEGORY_ID);
    }

    public String getName(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_CATEGORY_NAME);
    }
    public String getIcon(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_CATEGORY_ICON);
    }
    ImageView im;
    TextView tv ;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        if(convertView==null){
            v= LayoutInflater.from(context).inflate(R.layout.main_category_grid_item,null);

        }else v=convertView;
        im=(ImageView)v.findViewById(R.id.category_icon_iv);
        tv=(TextView)v.findViewById(R.id.category_name_tv);
        try {
            JSONObject object=array.getJSONObject(position);
            tv.setText(object.getString(Server.RETURN_CATEGORY_NAME));
            tv.setTextColor(Color.parseColor(object.getString(Server.RETURN_CATEGORY_COLOR)));
            Picasso.with(context).load(object.getString(Server.RETURN_CATEGORY_ICON)).into(im);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
}
