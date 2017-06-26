package cirrb.com.cirrab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import java.util.Locale;

import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.FusedLocationService;
import cirrb.com.cirrab.util.PreferenceClass;


public class SplashActivity extends Activity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        new FusedLocationService(this);
        String userLang = PreferenceClass.getStringPreferences(this, Constant.USER_LANG);
        if (!userLang.equals("")) {
            setLocaleLanguage(this, userLang);
        }
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if ((PreferenceClass.getBooleanPreferences(mContext, Constant.IS_LOGIN))) {
                    startActivity(new Intent(mContext, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    public void setLocaleLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }

}
