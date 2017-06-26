package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.EmailChecker;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 12/6/17.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        init();
    }

    private void init() {
        ((EditText) findViewById(R.id.edtEmailLogin)).getText().toString();
        ((EditText) findViewById(R.id.edtPasswordLogin)).getText().toString();
        ((EditText) findViewById(R.id.edtCnfrmPasswordLogin)).getText().toString();
        ((Button) findViewById(R.id.btn_register)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
    }


    private void register(String name, String lname, String email, String password, final String confirmPas, String deviceToken, String deviceType, String role, String lat, String longi) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.REGISTER_URL;
        System.out.println("URL is: " + url);
        try {

            jsonObject.putOpt("name", name);
            jsonObject.putOpt("last_name", lname);
            jsonObject.putOpt("email", email);
            jsonObject.putOpt("password", password);
            jsonObject.putOpt("password_confirmation", confirmPas);
            jsonObject.putOpt("device_token", deviceToken);
            jsonObject.putOpt("device_type", deviceType);
            jsonObject.putOpt("role", role);
            jsonObject.putOpt("lat", lat);
            jsonObject.putOpt("long", longi);

            System.out.println("jsonObject is: " + jsonObject);

            aquery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    Log.e("reg status is", status.getCode() + "");

                    if (status.getCode() == 200) {
                        try {
                            if (object.has("email")) {
                                JSONArray jsonArray = object.getJSONArray("email");
                                String msg = jsonArray.getString(0);
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.txt_register_success, Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.method(AQuery.METHOD_POST).header("Content-Type", "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void isValidData() {

        String userName = ((EditText) findViewById(R.id.edtEmailLogin)).getText().toString();
        String password = ((EditText) findViewById(R.id.edtPasswordLogin)).getText().toString();
        String confirmPass = ((EditText) findViewById(R.id.edtCnfrmPasswordLogin)).getText().toString();
        String currentLat = PreferenceClass.getStringPreferences(context, Constant.USER_CURRENT_LATITUDE);
        String currentLong = PreferenceClass.getStringPreferences(context, Constant.USER_CURRENT_LONGITUDE);
        Log.e("Lat ", currentLat + "");
        Log.e("Long ", currentLong + "");

        if (userName.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.txt_entr_username, Toast.LENGTH_SHORT).show();
        } else if (!(EmailChecker.checkEmail(userName))) {
            Toast.makeText(context, R.string.txt_enter_valid_email, Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Toast.makeText(context, R.string.txt_entr_password, Toast.LENGTH_SHORT).show();
        } else if (!Objects.equals(password, confirmPass)) {
            Toast.makeText(context, R.string.txt_confirm_pass, Toast.LENGTH_SHORT).show();
        } else {
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                register("", "", userName, password, confirmPass, "sadsads2zds213jjsajj", "android", "customer", currentLat, currentLong);
            } else {
                Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                isValidData();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }

    }
}
