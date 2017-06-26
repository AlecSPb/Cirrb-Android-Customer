package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cirrb.com.cirrab.adapter.MenuItemListAdapter;
import cirrb.com.cirrab.adapter.MenuItemReviewAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 19/6/17.
 */

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ArrayList<JSONObject> menuArrayList;
    private RecyclerView recyclerView;
    Toolbar mToolbar;
    ActionBar actionBar;
    float subTotalAmt=0.0f;
    float delTotalAmt=0.0f;
    float grandTotal=0.0f;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);
        mContext = this;
        setActionBar();
        init();
    }


    private void init() {
        try{
            menuArrayList = new ArrayList<JSONObject>();
            ((Button) findViewById(R.id.btn_confrm)).setOnClickListener(this);
            String sub_total = getIntent().getStringExtra("SUB_TOTAL");
            String menuLstReview = getIntent().getStringExtra("MENU_SELECTESLIST");
            JSONArray jsonArray = new JSONArray(menuLstReview);

            if(jsonArray!=null && jsonArray.length()>0){
                menuArrayList.clear();
                for (int i=0;i<jsonArray.length();i++){
                    if(jsonArray.getJSONObject(i).getInt("quantity")>0){
                        menuArrayList.add(jsonArray.getJSONObject(i));
                    }
                }
            }
            recyclerView = (RecyclerView) findViewById(R.id.recOrderReview);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            MenuItemReviewAdapter rvAdapter = new MenuItemReviewAdapter(mContext, menuArrayList);
            recyclerView.setAdapter(rvAdapter);
            rvAdapter.notifyDataSetChanged();

            ((TextView)findViewById(R.id.txtSetSubOrder)).setText("SR "+sub_total);

            float deliveryCharges=Float.parseFloat(PreferenceClass.getStringPreferences(mContext, Constant.BRANCH_DISTANCE)) * Float.parseFloat(PreferenceClass.getStringPreferences(mContext,Constant.DELIVERY_CHARGE_PER_KM));

            if(deliveryCharges > Float.parseFloat(PreferenceClass.getStringPreferences(mContext,Constant.MINIMUM_DELIVERY_CHARGE))){
                ((TextView)findViewById(R.id.txtSetDeliveryCharges)).setText("SR "+deliveryCharges);
            }else{
                ((TextView)findViewById(R.id.txtSetDeliveryCharges)).setText("SR "+PreferenceClass.getStringPreferences(mContext,Constant.MINIMUM_DELIVERY_CHARGE));
            }

            subTotalAmt = Float.parseFloat(((TextView)findViewById(R.id.txtSetSubOrder)).getText().toString().replace("SR ",""));
            delTotalAmt  = Float.parseFloat(((TextView)findViewById(R.id.txtSetDeliveryCharges)).getText().toString().replace("SR ",""));

            grandTotal =subTotalAmt+delTotalAmt;

            ((TextView)findViewById(R.id.txtSetGrandTotal)).setText("SR "+new DecimalFormat("##.##").format(grandTotal));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_review);
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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_confrm:

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.txt_confirm_dailog)
                        .setCancelable(false)
                        .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try{
                                    String strQuantity="";
                                    String strBranch="";
                                    String strRestaurent="";
                                    String strId="";
                                    for (int i=0;i<menuArrayList.size();i++){
                                        int idMenu=menuArrayList.get(i).getInt("id");
                                        int quantityMenu=menuArrayList.get(i).getInt("quantity");
                                        if(strId.trim().length()==0){
                                            strId=""+idMenu;
                                        }else{
                                            strId=strId+","+idMenu;
                                        }

                                        if(strQuantity.trim().length()==0){
                                            strQuantity=""+quantityMenu;
                                        }else{
                                            strQuantity=strQuantity+","+quantityMenu;
                                        }

                                        if(strBranch.trim().length()==0){
                                            strBranch=""+PreferenceClass.getIntegerPreferences(mContext,Constant.BRANCH_ID);
                                        }else{
                                            strBranch=strBranch+","+PreferenceClass.getIntegerPreferences(mContext,Constant.BRANCH_ID);
                                        }

                                        if(strRestaurent.trim().length()==0){
                                            strRestaurent=""+PreferenceClass.getIntegerPreferences(mContext,Constant.RESTAURENTID);
                                        }else{
                                            strRestaurent=strRestaurent+","+PreferenceClass.getIntegerPreferences(mContext,Constant.RESTAURENTID);
                                        }


                                    }
                                    setNewOrder(strId,strBranch,strRestaurent,strQuantity);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(R.string.txt_no, null);
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
    }

    private void setNewOrder(String idMenu,String branchId, String restaurentId,String quantity) {
        AQuery aquery = new AQuery(mContext);
        JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.NEW_ORDER;

        try {

            jsonObject.putOpt("sub_total",subTotalAmt );
            jsonObject.putOpt("delivery_fees", delTotalAmt);
            jsonObject.putOpt("total", grandTotal);
            jsonObject.putOpt("id", idMenu);
            jsonObject.putOpt("resturent_id", restaurentId);
            jsonObject.putOpt("branch_id", branchId);
            jsonObject.putOpt("quantity", quantity);
            jsonObject.putOpt("user_id", PreferenceClass.getIntegerPreferences(mContext,Constant.LOGINEMPID));
        }catch (Exception e){
            e.printStackTrace();
        }
        aquery.post(url,jsonObject,JSONObject.class,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try{
                    System.out.println("object is: "+object);
                    if(object.getString("status").equals("success")){
                        startActivity(new Intent(ReviewActivity.this, OrderCompleteActivity.class));
                        finish();
                    }else{
                        Toast.makeText(mContext,""+object.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.method(AQuery.METHOD_POST).header("Authorization",PreferenceClass.getStringPreferences(mContext,Constant.LOGINTOKEN)).header("Content-Type","application/json"));
    }
}
