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
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.ConnectionDetector;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;

/**
 * Created by yuva on 12/6/17.
 */

public class SetForgotActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressHUD progressHUD;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        context = this;
        String email = PreferenceClass.getStringPreferences(context, Constant.EMAIL);
        ((TextView) findViewById(R.id.tv_txt_email)).setText(email);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_reset_pass)).setOnClickListener(this);
    }

    private void setforgot(String code, String email, String pass, String confirmpass) {
        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.RESET_PASS;
        try {

            jsonObject.putOpt("code", code);
            jsonObject.putOpt("email", email);
            jsonObject.putOpt("password", pass);
            jsonObject.putOpt("password_confirmation", confirmpass);
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
                    startActivity(new Intent(SetForgotActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_pass:
                String code = ((EditText) findViewById(R.id.edtResetCode)).getText().toString();
                String pass = ((EditText) findViewById(R.id.edtNewPass)).getText().toString();
                String email = PreferenceClass.getStringPreferences(context, Constant.EMAIL);
                if (code.isEmpty()) {
                    Toast.makeText(context, R.string.txt_code_empty, Toast.LENGTH_SHORT).show();
                } else if (code.length() > 4 || code.length() < 4) {
                    Toast.makeText(context, R.string.txt_code_wrong, Toast.LENGTH_SHORT).show();
                } else if (pass.isEmpty()) {
                    Toast.makeText(context, R.string.txt_entr_password, Toast.LENGTH_SHORT).show();
                } else {
                    ConnectionDetector cd = new ConnectionDetector(context);
                    if (cd.isConnectingToInternet()) {
                        setforgot(code, email, pass, pass);
                    } else {
                        Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;

        }
    }
}
