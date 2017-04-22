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

package dong.lan.base.ui;

/**
 * Created by 梁桂栋 on 17-3-23 ： 下午3:29.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 用于延迟加载的接口,例如首页初始化完用户信息后,才进行其他数据的初始化
 */

public interface DelayInitView<T> {

    void start(T data);
}
