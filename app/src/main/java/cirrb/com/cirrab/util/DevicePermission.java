package cirrb.com.cirrab.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class DevicePermission extends AppCompatActivity {
    public static final int MY_PERMISSIONS = 100;
    private Activity act;

    public DevicePermission(Activity activity) {
        this.act = activity;
        checkPermissions();
    }

    boolean checkPermissions() {
        Log.e("DevicePermission", "checkPermissions ");
        if (ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.CAMERA},
                    MY_PERMISSIONS);
        } else {
          //  createDirectry();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    //createDirectry();
                    Log.e("onRequestPermisResult", "DEVICE Permission Granted ");
                    PreferenceClass.setBooleanPreference(act, "Permission_Granted", true);
                } else {
                    Log.e("onRequestPermisResult", "DEVICE Permission Denied ");
                    finish();
                }
                return;
            }
        }
    }

   /* //////////////////////// Create Directry ////////////////////////////////////
    public void createDirectry() {

        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String createdDirPath = "";
        if (isSDPresent) {
            File dir = new File(NewConstants.StoragePaths.EXTERNAL_ROOT_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.e("createDirectry", "createDirectry  in  External Storage");
            createdDirPath = dir.toString();
        } else {
            File mydir = new File(NewConstants.StoragePaths.INTERNAL_ROOT_PATH);
            if (!mydir.exists()) {
                mydir.mkdirs();
            }
            Log.e("createDirectry", "createDirectry  in  Internal Storage");
            createdDirPath = mydir.toString();
        }
        Log.e("createdDirPath", createdDirPath);
        ConstantsModel.setRootPath(createdDirPath);
        ConstatnsPreference.setStringPreference(act, NewConstants.StoragePaths.PrefStrings.CREATED_DIR_PATH, createdDirPath);
    }*/


}
