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

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 * Created by 梁桂栋 on 2017/4/15.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public interface FeedDetailContract {
    public interface View extends ProgressView,IActivityFunc {
        void showFeedImages(List<AVOFeedImage> images);

        void showFeedLikes(int likeCount);

        void showLabels(List<AVOLabel> labels);

        void showContent(String content);
    }

    public interface Presenter {
        void like(ImageButton likeIcon, TextView likText);

        void fetchFeed(String feedSeq);

        void saveLike();
    }

    public interface Model {
    }
}
