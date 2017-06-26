package cirrb.com.cirrab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cirrb.com.cirrab.adapter.CustomAdapter;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.fragment.HomeFragment;
import cirrb.com.cirrab.fragment.LogoutFragment;
import cirrb.com.cirrab.fragment.OrderFragment;
import cirrb.com.cirrab.fragment.ProfileFragment;
import cirrb.com.cirrab.model.ItemObject;
import cirrb.com.cirrab.util.FusedLocationService;
import cirrb.com.cirrab.util.PreferenceClass;
import cirrb.com.cirrab.util.ProgressHUD;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    String[] titles = {"Home", "Home", "Order", "Profile", "Logout"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar topToolBar;
    Context context;
    ProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        PreferenceClass.setBooleanPreference(context, Constant.IS_LOGIN, true);
        init();
    }

    private void init() {

        mTitle = mDrawerTitle = getTitle();

        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.header_list, null, false);

        mDrawerList.addHeaderView(listHeaderView);

        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        listViewItems.add(new ItemObject(R.string.txt_nav_home, R.drawable.home));
        listViewItems.add(new ItemObject(R.string.txt_nav_order, R.drawable.order_food));
        listViewItems.add(new ItemObject(R.string.txt_nav_profile, R.drawable.profile));
        listViewItems.add(new ItemObject(R.string.txt_nav_logout, R.drawable.logout));
        mDrawerList.setAdapter(new CustomAdapter(this, listViewItems));


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectItemFragment(1);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                selectItemFragment(position);
            }
        });
    }

    private void selectItemFragment(int position) {

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                fragment = new OrderFragment();
                break;
            case 3:
                fragment = new ProfileFragment();
                break;
            case 4:
                fragment = new LogoutFragment();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.txt_logout_msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                logOut(PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID));
                            }
                        })
                        .setNegativeButton(R.string.txt_no, null);
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(titles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void setLocaleLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
        PreferenceClass.setStringPreference(context, Constant.USER_LANG, lang);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut(int id) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);

        String url = Constant.BASE_URL + "logout?user_id=" + id;
        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                Log.e("Log out Stattus is...", String.valueOf(status.getCode()));
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                if (status.getCode() == 200) {
                    PreferenceClass.clearPreference(context);
                    finish();
                }

            }
        });

    }

}
