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

package dong.lan.mapfun.presentation;

import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.mapfun.feature.presenter.IRegisterPresenter;
import dong.lan.mapfun.feature.view.IRegisterView;

/**
 * Created by 梁桂栋 on 2017/4/12.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class RegisterPresenter implements IRegisterPresenter {

    private IRegisterView view;

    public RegisterPresenter(IRegisterView view) {
        this.view = view;
    }

    @Override
    public void register(final String username, final String password) {
        if (TextUtils.isEmpty(username)) {
            view.toast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            view.toast("密码长度为6到16为数字字母组合");
            return;
        }
        AVOUser user = new AVOUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setSex(-1);
        user.setLastLocation(0, 0);
        user.setNickname("");
        user.setAvatar(null);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    view.toast("即将为你自动登录");
                    Intent intent = new Intent();
                    intent.putExtra("password", password);
                    intent.putExtra("username", username);
                    view.activity().setResult(1, intent);
                    view.activity().finish();
                } else {
                    e.printStackTrace();
                    view.dialog("注册失败，错误码：" + e.getCode());
                }
            }
        });

    }
}
