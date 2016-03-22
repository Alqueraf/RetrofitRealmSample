package com.alexqueudotrafel.retrofitrealmsample.network.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alexqueudotrafel.retrofitrealmsample.BuildConfig;
import com.alexqueudotrafel.retrofitrealmsample.R;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class DialogWaitManager {

    private final static String TAG = "DialogWaitManager";
    private final static boolean D = BuildConfig.DEBUG;

    private static DialogWaitManager dialogWaitManagerInstance;

    private DialogWaitManager() {
    }

    public static DialogWaitManager getInstance() {
        if (dialogWaitManagerInstance == null) {
            dialogWaitManagerInstance = new DialogWaitManager();
        }
        return dialogWaitManagerInstance;
    }

    private static Dialog mDialog;

    public void showDialog(Context context) {
        if (mDialog == null || !mDialog.isShowing()) {
            mDialog = new Dialog(context);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Transparent dialog background
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // Transparent window background
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_wait);
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            }
            catch (IllegalArgumentException ex){
                if(D) Log.e(TAG, ex.getMessage());
            }
            mDialog = null;
        }
    }
}
