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

package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import dong.lan.base.ui.customView.ItemLabelData;

/**
 * Created by 梁桂栋 on 2017/4/12.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
@AVClassName("Label")
public class AVOLabel extends AVObject implements ItemLabelData{

    public String getLabel(){
        return getString("label");
    }

    public void setLabel(String label){
        put("label",label);
    }

    public AVOUser getCreator() {
        try {
            return getAVObject("creator",AVOUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCreator(AVOUser user) {
        super.put("creator", user);
    }

    @Override
    public String labelText() {
        return getLabel();
    }
}
