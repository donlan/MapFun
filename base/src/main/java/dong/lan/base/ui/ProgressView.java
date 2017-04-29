
package dong.lan.base.ui;


/**
 */

public interface ProgressView extends BaseView {

    void alert(String text);

    boolean isProcessing();

    void dismiss();
}
