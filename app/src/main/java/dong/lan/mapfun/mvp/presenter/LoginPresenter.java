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

package dong.lan.mapfun.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.mapfun.activity.MainActivity;
import dong.lan.mapfun.feature.presenter.ILoginPresenter;
import dong.lan.mapfun.feature.view.ILoginView;

/**
 * Created by 梁桂栋 on 2017/4/12.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class LoginPresenter implements ILoginPresenter {

    private ILoginView view;

    public LoginPresenter(ILoginView view) {
        this.view = view;
    }

    @Override
    public void login(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            view.toast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            view.toast("密码长度为6到16为数字字母组合");
            return;
        }

        AVOUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                AVQuery<AVOUser> query = new AVQuery<AVOUser>("MyUser");
                query.whereEqualTo("user",query);
                query.include("user");
                query.findInBackground(new FindCallback<AVOUser>() {
                    @Override
                    public void done(List<AVOUser> list, AVException e) {
                        if (e == null ) {
                            if(list!=null && !list.isEmpty()) {
                                AVOUser.setCurrentUser(list.get(0));
                                view.toast("欢迎你回来");
                                view.activity().startActivity(new Intent(view.activity(), MainActivity.class));
                                view.activity().finish();
                            }else{
                                view.dialog("登录失败，该用户不存在");
                            }
                        } else {
                            e.printStackTrace();
                            if (e.getCode() == 211) {
                                view.dialog("登录失败，该用户不存在");
                            } else {
                                view.dialog("登录失败，错误码：" + e.getCode());
                            }
                        }
                    }
                });
            }
        });
    }
}
