package cirrb.com.cirrab.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.OrderCompleteActivity;
import cirrb.com.cirrab.OrderDetailsActivity;
import cirrb.com.cirrab.OrderRestaurentActivity;
import cirrb.com.cirrab.R;
import cirrb.com.cirrab.ReviewActivity;
import cirrb.com.cirrab.adapter.AllRestaurentListAdapter;
import cirrb.com.cirrab.adapter.MenuItemReviewAdapter;
import cirrb.com.cirrab.adapter.OrderHistoryAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

import static android.R.attr.password;

/**
 * Created by yuva on 13/6/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap googlemap;
    View rootView;
    Context context;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ArrayList<JSONObject> lstOrder=new ArrayList<JSONObject>();

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init();
        return rootView;

    }

    private void init() {

        ((Button) rootView.findViewById(R.id.btn_new_order)).setOnClickListener(this);

        ConnectionDetector cd = new ConnectionDetector(context);
        if (cd.isConnectingToInternet()) {


            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
        }

        getOrder();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(PreferenceClass.getStringPreferences(context,Constant.USER_CURRENT_LATITUDE)), Double.parseDouble(PreferenceClass.getStringPreferences(context,Constant.USER_CURRENT_LONGITUDE))));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }




        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_new_order:
                startActivity(new Intent(getActivity(), OrderRestaurentActivity.class));
                break;
            case R.id.txtOrderId:
            case R.id.txtOrderStatus:
            case R.id.txtOrderAmt:
                int posi = Integer.parseInt(view.getTag().toString());
                JSONObject object = lstOrder.get(posi);
                startActivity(new Intent(context, OrderDetailsActivity.class).putExtra("OrderDetails",object.toString()));
                break;
        }

    }

    private void getOrder() {
        AQuery aquery = new AQuery(context);
        JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.GET_ORDER;
        System.out.println("getOrder url is: "+url);
        try {
            jsonObject.putOpt("user_id", PreferenceClass.getIntegerPreferences(context,Constant.LOGINEMPID));
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
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);

                            OrderHistoryAdapter rvAdapter = new OrderHistoryAdapter(context, lstOrder,HomeFragment.this);
                            recyclerView.setAdapter(rvAdapter);
                            rvAdapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.method(AQuery.METHOD_POST).header("Authorization",PreferenceClass.getStringPreferences(context,Constant.LOGINTOKEN)).header("Content-Type","application/json"));
    }
}
