
package dong.lan.base.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import dong.lan.base.R;

/**
 */

public class Dialog implements View.OnClickListener {

    public static final int CLICK_LEFT = 0;
    public static final int CLICK_RIGHT = 1;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private TextView left;
    private TextView right;
    private TextView message;
    private FrameLayout container;

    public Dialog(Context context) {
        builder = new AlertDialog.Builder(context);
        View baseView = LayoutInflater.from(context).inflate(R.layout.dialog, null);

        left = (TextView) baseView.findViewById(R.id.dialog_left);
        right = (TextView) baseView.findViewById(R.id.dialog_right);
        message = (TextView) baseView.findViewById(R.id.dialog_msg);
        container = (FrameLayout) baseView.findViewById(R.id.dialog_container);

        left.setOnClickListener(this);
        right.setOnClickListener(this);

        builder.setView(baseView);

    }



    public Dialog setClickListener(DialogClickListener listener) {
        this.listener = listener;
        return this;
    }

    public Dialog setView(int layoutRes) {
        container.removeAllViews();
        container.addView(LayoutInflater.from(builder.getContext()).inflate(layoutRes, null));
        return this;
    }

    public Dialog setView(View view) {
        container.removeAllViews();
        container.addView(view);
        return this;
    }

    public Dialog setLeftText(String text) {
        left.setText(text);
        return this;
    }

    public Dialog setRightText(String text) {
        right.setText(text);
        return this;
    }

    public Dialog setMessageText(String text) {
        message.setText(text);
        return this;
    }


    public void dismiss(boolean removeViews) {
        if (alertDialog != null && alertDialog.isShowing()) {
            if (removeViews)
                container.removeAllViews();
            alertDialog.dismiss();
        }
    }

    public View findView(int id){
        return container.findViewById(id);
    }

    public Dialog show() {
        if (alertDialog == null)
            alertDialog = builder.create();
        if (!alertDialog.isShowing())
            alertDialog.show();
        return this;
    }

    private boolean removeViews = true;

    public Dialog setRemoveViews(boolean removeViews) {
        this.removeViews = removeViews;
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (listener != null) {
            boolean dis = listener.onDialogClick(id == R.id.dialog_left ? CLICK_LEFT : CLICK_RIGHT);
            if (dis)
                dismiss(removeViews);
        } else {
            dismiss(removeViews);
        }
    }


    private DialogClickListener listener;

    public void destroy() {
        container.removeAllViews();
        container = null;
        alertDialog.dismiss();
        alertDialog =null;
        builder = null;
        left = null;
        right = null;
        message = null;
    }


    public interface DialogClickListener {
        boolean onDialogClick(int which);
    }


}
