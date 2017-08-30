package spor.automato.com.sporprojecttest.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public SweetAlertDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mProgressDialog.setTitleText("Загрузка");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
