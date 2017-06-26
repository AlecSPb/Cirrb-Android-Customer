package cirrb.com.cirrab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.DevicePermission;
import cirrb.com.cirrab.util.EmailChecker;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 12/6/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    ProgressHUD progressHUD;
    private String registrationId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DevicePermission(LoginActivity.this);
        setContentView(R.layout.activity_login);
        context = this;

        ((Button) findViewById(R.id.btn_login)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_register)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_forgotpswrd)).setOnClickListener(this);

    }

    private void loginApi(final String userName, final String password, String deviceType, String deviceToken, String remeber, String role) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.LOGIN_URL;
        System.out.println("URL is: " + url);
        try {
            jsonObject.putOpt("email", userName);
            jsonObject.putOpt("password", password);
            jsonObject.putOpt("device_type", deviceType);
            jsonObject.putOpt("device_token", deviceToken);
            jsonObject.putOpt("remember", remeber);
            jsonObject.putOpt("role", role);

            System.out.println("jsonObject is: " + jsonObject);

            aquery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    if (status.getCode() == 200) {

                        if (object.has("message")) {
                            try {
                                String messgae = object.getString("message");
                                Toast.makeText(context, messgae, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                PreferenceClass.setIntegerPreference(context, Constant.LOGINEMPID, object.getInt("id"));
                                PreferenceClass.setStringPreference(context, Constant.LOGINNAME, object.getString("name"));
                                PreferenceClass.setStringPreference(context, Constant.LOGINLASTNAME, object.getString("last_name"));
                                PreferenceClass.setStringPreference(context, Constant.LOGINEMAIL, object.getString("email"));
                                PreferenceClass.setStringPreference(context, Constant.LOGINPHONE, object.getString("phone"));
                                PreferenceClass.setStringPreference(context, Constant.LOGINTOKEN, object.getString("auth_token"));
                                PreferenceClass.setStringPreference(context, Constant.MINIMUM_DELIVERY_CHARGE, object.getString("minimum_delivery_charges"));
                                PreferenceClass.setStringPreference(context, Constant.DELIVERY_CHARGE_PER_KM, object.getString("delivery_charges_per_km"));
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void isValidData() {

        String userName = ((EditText) findViewById(R.id.edtEmailLogin)).getText().toString();
        String password = ((EditText) findViewById(R.id.edtPasswordLogin)).getText().toString();


        if (userName.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.txt_entr_username, Toast.LENGTH_SHORT).show();
        } else if (!(EmailChecker.checkEmail(userName))) {
            Toast.makeText(context, R.string.txt_enter_valid_email, Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Toast.makeText(context, R.string.txt_entr_password, Toast.LENGTH_SHORT).show();
        } else {
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                loginApi(userName, password, "android", "asdnsad87897sd89aasasds", "false", "customer");
            } else {
                Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                isValidData();
                // startActivity(new Intent(LoginActivity.this, MainActivity.class));

                break;
            case R.id.tv_register:

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.tv_forgotpswrd:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;

        }
    }

}
