package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.EmailChecker;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 12/6/17.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = this;

        ((Button) findViewById(R.id.btn_reset)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
    }


    private void forgot(String email) {
        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.FORGOT_PASS;
        try {

            jsonObject.putOpt("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        aquery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                if (status.getCode() == 200) {
                    startActivity(new Intent(ForgotPasswordActivity.this, SetForgotActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset:
                String email = ((EditText) findViewById(R.id.edtEmailLogin)).getText().toString();
                PreferenceClass.setStringPreference(context, Constant.EMAIL, email);
                if (!email.isEmpty()) {
                    ConnectionDetector cd = new ConnectionDetector(context);
                    if (cd.isConnectingToInternet()) {
                        forgot(email);
                    } else {
                        Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
                    }

                } else if (!(EmailChecker.checkEmail(email))) {
                    Toast.makeText(context, R.string.txt_enter_valid_email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.txt_email_empty, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }
}
