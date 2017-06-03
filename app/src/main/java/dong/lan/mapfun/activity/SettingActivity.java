
package dong.lan.mapfun.activity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.base.Config;
import dong.lan.base.utils.SPHelper;
import dong.lan.mapfun.R;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity{

    private TextView radiusTv;
    private SeekBar radiusSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        radiusTv = (TextView) findViewById(R.id.setting_radius_tv);
        radiusSb = (SeekBar) findViewById(R.id.setting_radius_sb);

        radiusTv.setText("附近搜索半径："+Config.RADIUS+" Km");

        radiusSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SPHelper.instance().putInt("radius",progress);
                radiusTv.setText("附近搜索半径："+ progress+" Km");
                Config.RADIUS = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
