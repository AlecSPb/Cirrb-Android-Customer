package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cirrb.com.cirrab.adapter.MenuItemListAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 19/6/17.
 */

public class MenuItemActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    Context context;
    ProgressHUD progressHUD;
    ArrayList<JSONObject> menuArrayList = new ArrayList<>();
    ArrayList<Float> totalAmountLst = new ArrayList<>();
    Toolbar mToolbar;
    ActionBar actionBar;
    MenuItemListAdapter rvAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_menu);
        context = this;
        init();
    }

    private void init() {
        ((Button) findViewById(R.id.btn_contin)).setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_menu_selection);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        int resId = PreferenceClass.getIntegerPreferences(context, Constant.RESTAURENTID);
        int id = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
        ConnectionDetector cd = new ConnectionDetector(context);
        if (cd.isConnectingToInternet()) {
            getMenus(resId, id);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
        }
        setActionBar();
    }

    private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_menu);
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


    private void getMenus(int resId, int userId) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });

        String url = Constant.BASE_URL + "menus?action=api&restaurant_id=" + resId + "&user_id=" + userId;
        AQuery aq = new AQuery(context);
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        if (status.getCode() == 200) {
                            menuArrayList.clear();
                            try {
                                JSONArray jsonArray = object.getJSONArray("details");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    menuArrayList.add(jsonObject);
                                    totalAmountLst.add(0.0f);
                                    rvAdapter = new MenuItemListAdapter(context, menuArrayList, MenuItemActivity.this);
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
            case R.id.btn_contin:
                float total_bill_amt = Float.parseFloat(((TextView)findViewById(R.id.total_price)).getText().toString().replace("SR: ",""));
                if(total_bill_amt>0){
                    startActivity(new Intent(MenuItemActivity.this, ReviewActivity.class).putExtra("MENU_SELECTESLIST",menuArrayList.toString()).putExtra("SUB_TOTAL",""+total_bill_amt));
                }else{
                    Toast.makeText(context,R.string.select_menu_item,Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.foodPlus:
                try{
                    int pos = Integer.parseInt(view.getTag().toString());
                    JSONObject menuObject = menuArrayList.get(pos);
                    int item_count = menuObject.getInt("quantity");
                    item_count++;
                    menuObject.putOpt("quantity",item_count);
                    rvAdapter.notifyDataSetChanged();
                    float amount= item_count * Float.parseFloat(menuArrayList.get(pos).getString("price"));
                    totalAmountLst.set(pos,amount);
                    System.out.println("totalAmountLst: "+totalAmountLst);
                    float total_amt=0.0f;
                    for (int i=0;i<totalAmountLst.size();i++){
                        total_amt=total_amt+totalAmountLst.get(i);
                    }
                    ((TextView)findViewById(R.id.total_price)).setText("SR: "+new DecimalFormat("##.##").format(total_amt));
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case R.id.foodMinus:
                try{
                    int pos = Integer.parseInt(view.getTag().toString());
                    JSONObject menuObject = menuArrayList.get(pos);
                    int item_count = menuObject.getInt("quantity");
                    if(item_count>0){
                        item_count--;
                        menuObject.putOpt("quantity",item_count);
                        rvAdapter.notifyDataSetChanged();
                    }
                    float amount= item_count * Float.parseFloat(menuArrayList.get(pos).getString("price"));
                    totalAmountLst.set(pos,amount);
                    System.out.println("totalAmountLst: "+totalAmountLst);
                    float total_amt=0.0f;
                    for (int i=0;i<totalAmountLst.size();i++){
                        total_amt=total_amt+totalAmountLst.get(i);
                    }
                    ((TextView)findViewById(R.id.total_price)).setText("SR: "+new DecimalFormat("##.##").format(total_amt));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
