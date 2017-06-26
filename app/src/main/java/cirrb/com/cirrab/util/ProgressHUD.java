package cirrb.com.cirrab.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.ImageView;

import cirrb.com.cirrab.R;


public class ProgressHUD extends Dialog {
    public ProgressHUD(Context context) {
        super(context);
    }

    public ProgressHUD(Context context, int theme) {
        super(context, theme);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }



    @SuppressWarnings("deprecation")
    public static ProgressHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener) {

        ProgressHUD dialog = new ProgressHUD(context, R.style.ProgressHUD);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_hud);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(lp);

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        try {
            dialog.show();
        } catch (BadTokenException e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static void dismissDialog(ProgressHUD progress) {
        if (progress.isShowing() && progress != null) {
            progress.dismiss();
        }
    }
}
