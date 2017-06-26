package cirrb.com.cirrab.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.R;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.PreferenceClass;


public class AllRestaurentListAdapter extends RecyclerView.Adapter<AllRestaurentListAdapter.RestaurentHolder> {

    ArrayList<JSONObject> arrayList;
    View.OnClickListener clickListener;
    Context context;


    public AllRestaurentListAdapter(Context context, ArrayList<JSONObject> arrayList, View.OnClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    @Override
    public RestaurentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_new_order, parent, false);
        RestaurentHolder imh = new RestaurentHolder(v);
        return imh;
    }

    @Override
    public void onBindViewHolder(RestaurentHolder viewHolder, int position) {

        JSONObject jsnRest = null;
        try {
            jsnRest = arrayList.get(position).getJSONObject("restaurant");
            String id = jsnRest.getString("id");
            String name = jsnRest.getString("name");
            String imge = jsnRest.getString("image");

            JSONObject jsnBranch = arrayList.get(position).getJSONObject("branch");
            String branchAdd = jsnBranch.getString("name");
            String branchDistnc = jsnBranch.getString("distance");
            int resId = jsnBranch.getInt("restaurant_id");
          //  PreferenceClass.setIntegerPreference(context, Constant.RESTAURENTID,resId);
            viewHolder.tvrestName.setText(name);
            viewHolder.tvrestAdd.setText(branchAdd);
            viewHolder.tvrestDistnace.setText(branchDistnc);

             Picasso.with(context)
                            .load(imge)
                            .into(viewHolder.restImage);

            viewHolder.rootViewLayout.setOnClickListener(clickListener);
            viewHolder.rootViewLayout.setTag(position);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public static class RestaurentHolder extends RecyclerView.ViewHolder {

        ImageView restImage;
        TextView tvrestName, tvrestAdd, tvrestDistnace;
        RelativeLayout rootViewLayout;


        RestaurentHolder(View itemView) {
            super(itemView);

            restImage = (ImageView) itemView.findViewById(R.id.restaurent_imv);
            tvrestName = (TextView) itemView.findViewById(R.id.tv_rstrnt_name);
            tvrestAdd = (TextView) itemView.findViewById(R.id.tv_rstrnt_add);
            tvrestDistnace = (TextView) itemView.findViewById(R.id.tv_rstrnt_distance);
            rootViewLayout=(RelativeLayout)itemView.findViewById(R.id.rootViewLayout);

        }
    }
}



   /* public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(String.valueOf(encodedString), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }*/



















/*
package com.example.yuva.instagramdemonew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public abstract class HomeFeedAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<String> arrayList;
    private Context context;


    public HomeFeedAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
    }

    public HomeFeedAdapter() {
        setHasStableIds(true);
    }


    public void clear() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        arrayList.remove(object);
        notifyDataSetChanged();
    }

    public String getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
*/
