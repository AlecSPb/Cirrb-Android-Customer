package cirrb.com.cirrab.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import cirrb.com.cirrab.LoginActivity;
import cirrb.com.cirrab.MainActivity;
import cirrb.com.cirrab.constant.Constant;


public class FusedLocationService implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final long INTERVAL = 0;//1000
    private static final long FASTEST_INTERVAL = 0;//500
    //	Activity locationActivity;
    Context locationActivity;
    private LocationRequest locationRequest;
    public static GoogleApiClient googleApiClient;
    private Location currentLocation;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    public FusedLocationService(Context context) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.locationActivity = context;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);

            if (currentLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }

            if (currentLocation != null) {
                PreferenceClass.setStringPreference(locationActivity, Constant.USER_CURRENT_LATITUDE, "" + currentLocation.getLatitude());
                PreferenceClass.setStringPreference(locationActivity, Constant.USER_CURRENT_LONGITUDE, "" + currentLocation.getLongitude());

            }
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        PreferenceClass.setStringPreference(locationActivity, Constant.USER_CURRENT_LATITUDE, "" + location.getLatitude());
        PreferenceClass.setStringPreference(locationActivity, Constant.USER_CURRENT_LONGITUDE, "" + location.getLongitude());

        //Log.e("Lat service", location.getLatitude() + "");
        //Log.e("Long service", location.getLongitude() + "");

        if(PreferenceClass.getBooleanPreferences(locationActivity,Constant.IS_LOGIN)){
            updateCordinate(""+PreferenceClass.getIntegerPreferences(locationActivity,Constant.LOGINEMPID),""+location.getLatitude(),""+location.getLongitude());
        }
    }

    public Location getLocation() {
        return this.currentLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        googleApiClient.connect();
    }

    private void updateCordinate(String userID,String latitude, String longitude) {
        AQuery aquery = new AQuery(locationActivity);
        JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.UPDATE_CORDINATES;
        //System.out.println("URL is: " + url);
        try {
            jsonObject.putOpt("user_id", userID);
            jsonObject.putOpt("lat", latitude);
            jsonObject.putOpt("long", longitude);

           // System.out.println("jsonObject for cordinate update"+jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
        aquery.post(url,jsonObject,JSONObject.class,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                //System.out.println("Update Cordinats Response: "+object);
            }
        }.method(AQuery.METHOD_POST).header("Authorization",PreferenceClass.getStringPreferences(locationActivity,Constant.LOGINTOKEN)));
    }

}