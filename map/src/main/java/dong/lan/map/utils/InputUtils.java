
package dong.lan.map.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 */

public class InputUtils {

    private InputUtils() {
    }

    public static void hideInputKeyboard(EditText editText) {
        Context context = editText.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        }
    }

    public static void showInputKeyboard(EditText editText) {
        Context context = editText.getContext();
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(editText, 0);
    }

}
