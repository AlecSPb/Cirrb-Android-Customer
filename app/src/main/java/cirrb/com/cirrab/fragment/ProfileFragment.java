package cirrb.com.cirrab.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import cirrb.com.cirrab.R;
import cirrb.com.cirrab.constant.Constant;
import cirrb.com.cirrab.util.CircleImageView;
import cirrb.com.cirrab.util.PreferenceClass;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by yuva on 13/6/17.
 */

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    View rootView;
    Spinner spinnerLang;
    Context context;
    private final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 1, CROP_PIC = 2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        setHasOptionsMenu(true);
        init();
        return rootView;
    }


    private void init() {

        ((CircleImageView) rootView.findViewById(R.id.circleView)).setOnClickListener(this);

        String userLang = PreferenceClass.getStringPreferences(context, Constant.USER_LANG);

        if (!userLang.equals("")) {
            setLocaleLanguage(context, userLang);
        }
        spinnerLang = (Spinner) rootView.findViewById(R.id.langSpinner);
        spinnerLang.setOnItemSelectedListener(this);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(0, "English");
        arrayList.add(1, "Arabic");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item, arrayList);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerLang.setAdapter(dataAdapter);
        if (PreferenceClass.getIntegerPreferences(context, "LANGUAGE") == 0 || PreferenceClass.getIntegerPreferences(context, "LANGUAGE") == 1) {
            int lanPos = PreferenceClass.getIntegerPreferences(context, "LANGUAGE");
            spinnerLang.setSelection(lanPos);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circleView:
                selectImage();
                break;
        }

    }

    private void selectImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent1.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent1, "Select File"), SELECT_FILE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA && data.getData() != null) {
                Uri picUri = data.getData();
                performCrop(picUri);
            }
        }
        try {
            if (requestCode == SELECT_FILE && data.getData() != null) {
                Uri picUri = data.getData();
                performCrop(picUri);

            } else if (requestCode == CROP_PIC && data.getExtras() != null) {
                Bitmap bitmap = data.getExtras().getParcelable("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                ((CircleImageView) rootView.findViewById(R.id.circleView)).setImageBitmap(bitmap);
            }
        } catch (NullPointerException e) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void performCrop(Uri picUri) {
        int aspectX = 400;
        int aspectY = 400;
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", aspectX);
            cropIntent.putExtra("aspectY", aspectY);
            cropIntent.putExtra("outputX", aspectX);
            cropIntent.putExtra("outputY", aspectY);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUIForLanguage() {

        ((TextView) rootView.findViewById(R.id.tv_inhand)).setText(context.getResources().getString(R.string.txtinhand));
        ((TextView) rootView.findViewById(R.id.tv_lstRec)).setText(context.getResources().getString(R.string.txtlstrcvd));
        ((TextView) rootView.findViewById(R.id.tv_totlaorder)).setText(context.getResources().getString(R.string.txttotlaordr));
        ((TextView) rootView.findViewById(R.id.tv_limit)).setText(context.getResources().getString(R.string.txtlimit));
        ((TextView) rootView.findViewById(R.id.txt_prfl)).setText(context.getResources().getString(R.string.txtProfile));

    }

    public void setLocaleLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
        setUIForLanguage();
        PreferenceClass.setStringPreference(context, Constant.USER_LANG, lang);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            setLocaleLanguage(context, "en");
            PreferenceClass.setIntegerPreference(context, "LANGUAGE", parent.getSelectedItemPosition());

        } else {
            setLocaleLanguage(context, "ar");
            PreferenceClass.setIntegerPreference(context, "LANGUAGE", parent.getSelectedItemPosition());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.action_addItem) {

        } else {

        }
        return super.onOptionsItemSelected(item);
    }


}
