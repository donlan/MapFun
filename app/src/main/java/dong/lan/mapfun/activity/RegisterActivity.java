/*
 *   Copyright 2016, donlan(梁桂栋)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me: stonelavender@hotmail.com
 */

package dong.lan.mapfun.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.base.ui.BaseActivity;
import dong.lan.mapfun.R;
import dong.lan.mapfun.feature.presenter.IRegisterPresenter;
import dong.lan.mapfun.feature.view.IRegisterView;
import dong.lan.mapfun.presentation.RegisterPresenter;

public class RegisterActivity extends BaseActivity implements IRegisterView {

    @BindView(R.id.register_password)
    EditText passwordET;
    @BindView(R.id.register_username)
    EditText usernameEt;
    @OnClick(R.id.register)
    void register(){
        String username = usernameEt.getText().toString();
        String password = passwordET.getText().toString();
        presenter.register(username,password);
    }


    IRegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        bindView();
        usernameEt = (EditText) findViewById(R.id.register_username);
        passwordET = (EditText) findViewById(R.id.register_password);
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        presenter = new RegisterPresenter(this);
    }

    @Override
    public Activity activity() {
        return this;
    }
}
