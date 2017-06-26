package cirrb.com.cirrab;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import cirrb.com.cirrab.util.FusedLocationService;


public class Application extends MultiDexApplication {
	FusedLocationService fusedLocationService;
	@Override
	public void onCreate() {
		super.onCreate();
		fusedLocationService = new FusedLocationService(this);
	}
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
