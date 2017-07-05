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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Farhan on 03-02-2017.
 */

public class FollowAdapter extends BaseAdapter {
    Context context;
    JSONArray array;

    public FollowAdapter(Context context, JSONArray array) {
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


    public String getUserId(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_USER_ID);
    }

    public String getName(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_USER_NAME);
    }
    public String getImg(int i) throws JSONException {
        JSONObject object=array.getJSONObject(i);
        return object.getString(Server.RETURN_USER_PIC);
    }
    CircleImageView im;
    TextView name,detail ;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.follow_user_item,null);
        }
        im=(CircleImageView) convertView.findViewById(R.id.follow_usr_pic);
        name=(TextView)convertView.findViewById(R.id.follow_usr_name);
        detail=(TextView)convertView.findViewById(R.id.follow_usr_detail);
        try {
            JSONObject object=array.getJSONObject(position);
            name.setText(object.getString(Server.RETURN_USER_NAME));
            detail.setText(object.getString("posts")+" posts | "+object.getString("followers")+" followers | "+object.getString("following")+" following");
            Picasso.with(context).load(object.getString(Server.RETURN_USER_PIC)).into(im);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
