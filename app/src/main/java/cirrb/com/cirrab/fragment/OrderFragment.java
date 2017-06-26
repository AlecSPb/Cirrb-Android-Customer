package cirrb.com.cirrab.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.MenuItemActivity;
import cirrb.com.cirrab.OrderDetailsActivity;
import cirrb.com.cirrab.OrderRestaurentActivity;
import cirrb.com.cirrab.R;
import cirrb.com.cirrab.adapter.OrderHistoryAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.PreferenceClass;


/**
 * Created by yuva on 13/6/17.
 */

public class OrderFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    ArrayList<JSONObject> lstOrder=new ArrayList<JSONObject>();
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        mContext = this.getActivity();
        init();
        return rootView;

    }

    private void init(){
        getOrder();
    }

    private void getOrder() {
        AQuery aquery = new AQuery(mContext);
        JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.GET_ALL_ORDER;
        System.out.println("getOrder url is: "+url);
        try {
            jsonObject.putOpt("user_id", PreferenceClass.getIntegerPreferences(mContext,Constant.LOGINEMPID));
        }catch (Exception e){
            e.printStackTrace();
        }
        aquery.post(url,jsonObject,JSONObject.class,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try{

                    if(object.getString("status").equals("success")){
                        JSONArray jsonArray = object.getJSONArray("orders");
                        if(jsonArray!=null && jsonArray.length()>0){
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jObject=jsonArray.getJSONObject(i);
                                lstOrder.add(jObject);
                            }

                            RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerViewOrderLst);
                            recyclerView.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                            recyclerView.setLayoutManager(layoutManager);

                            OrderHistoryAdapter rvAdapter = new OrderHistoryAdapter(mContext, lstOrder,OrderFragment.this);
                            recyclerView.setAdapter(rvAdapter);
                            rvAdapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.method(AQuery.METHOD_POST).header("Authorization",PreferenceClass.getStringPreferences(mContext,Constant.LOGINTOKEN)).header("Content-Type","application/json"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtOrderId:
            case R.id.txtOrderStatus:
            case R.id.txtOrderAmt:
                int posi = Integer.parseInt(view.getTag().toString());
                JSONObject object = lstOrder.get(posi);
                startActivity(new Intent(mContext, OrderDetailsActivity.class).putExtra("OrderDetails",object.toString()));
                break;
        }
    }
}
