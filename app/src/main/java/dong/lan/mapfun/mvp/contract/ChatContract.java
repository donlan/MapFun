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

package dong.lan.mapfun.mvp.contract;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

import java.util.List;

import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 * Created by 梁桂栋 on 2017/4/24.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public interface ChatContract {
    public interface View extends ProgressView, IActivityFunc {
        void initView(String username);

        void showMessage(List<AVIMMessage> list);

        void newMessage(AVIMMessage message);
    }

    public interface Presenter {
        void start(String userSeq);

        void sendTextMessage(String content);

        void newGuide(double latitude, double longitude, String address);

        void handlerMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client);
    }

    public interface Model {
    }
}
