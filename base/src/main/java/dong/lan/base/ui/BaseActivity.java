
package dong.lan.base.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dong.lan.base.R;

/**
 */

public class BaseActivity extends AppCompatActivity implements ProgressView {

    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    public void dialog(String text) {
        if (dialog == null) {
            dialog = new Dialog(this);
        }
        dialog.setMessageText(text).show();
    }

    public void alert(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_anim));
        }
        progressDialog.setMessage(text);
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public boolean isProcessing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    public void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void bindView() {
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void show(String text) {
        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
        unbinder = null;
        if (dialog != null)
            dialog.destroy();
        dialog = null;
    }
}
