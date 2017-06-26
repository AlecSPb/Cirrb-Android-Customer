package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.adapter.AllRestaurentListAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 12/6/17.
 */

public class OrderRestaurentActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    Context context;
    ProgressHUD progressHUD;
    ArrayList<JSONObject> restrntArrayList = new ArrayList<>();
    Toolbar mToolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_restaurent);
        context = this;
        init();
    }

    private void init() {
        setActionBar();

        recyclerView = (RecyclerView) findViewById(R.id.rv_restaurent);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        String currentLat = PreferenceClass.getStringPreferences(context, Constant.USER_CURRENT_LATITUDE);
        String currentLong = PreferenceClass.getStringPreferences(context, Constant.USER_CURRENT_LONGITUDE);
        String token = PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN);
        int id = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
        ConnectionDetector cd = new ConnectionDetector(context);

        if (cd.isConnectingToInternet()) {
            getRestaurent(currentLat, currentLong, 100, token, id);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
        }
    }


    private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_res);
        setSupportActionBar(mToolbar);
        ((ImageView) mToolbar.findViewById(R.id.imgBack)).setVisibility(View.VISIBLE);
        ((ImageView) mToolbar.findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

    }

    private void getRestaurent(String lat, String longi, int distance, String token, int id) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        String url = Constant.BASE_URL + "restaurants?action=api&lat=" + lat + "&long=" + longi + "&distance=" + distance + "&Authorization=" + token + "&user_id=" + id;
        System.out.println("url is: "+url);
        AQuery aq = new AQuery(context);
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        if (status.getCode() == 200) {
                            System.out.println("object is: "+object);
                            restrntArrayList.clear();
                            try {
                                JSONArray jsonArray = object.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    restrntArrayList.add(jsonObject);
                                    AllRestaurentListAdapter rvAdapter = new AllRestaurentListAdapter(context, restrntArrayList, OrderRestaurentActivity.this);
                                    recyclerView.setAdapter(rvAdapter);
                                    rvAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }.method(AQuery.METHOD_GET).header("Authorization", PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN))
        );
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.rootViewLayout:
                try {
                    int posi = Integer.parseInt(view.getTag().toString());
                    JSONObject jsnBranch = restrntArrayList.get(posi).getJSONObject("branch");
                    int resId = jsnBranch.getInt("restaurant_id");
                    int branchId = jsnBranch.getInt("id");
                    int branchDistance = jsnBranch.getInt("distance");
                    PreferenceClass.setIntegerPreference(context, Constant.RESTAURENTID, resId);
                    PreferenceClass.setIntegerPreference(context, Constant.BRANCH_ID, branchId);
                    PreferenceClass.setStringPreference(context, Constant.BRANCH_DISTANCE, String.valueOf(branchDistance));
                    startActivity(new Intent(OrderRestaurentActivity.this, MenuItemActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
