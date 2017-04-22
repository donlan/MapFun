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

package dong.lan.base.ui.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import dong.lan.base.R;


/**
 * Created by 梁桂栋 on 17-1-18 ： 下午10:09.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class CircleImageButton extends android.support.v7.widget.AppCompatImageButton {

    private int bg_color = Color.BLUE;
    private Paint mPaint;

    public CircleImageButton(Context context) {
        this(context, null);
    }

    public CircleImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleImageButton);
            bg_color = ta.getColor(R.styleable.CircleImageButton_cib_bg_color, bg_color);
            ta.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(bg_color);
    }


    public void setBgColor(int bgColor) {
        this.bg_color = bgColor;
        mPaint.setColor(bgColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int d = Math.min(getWidth(),getHeight());
        canvas.drawCircle(d / 2, d / 2, d / 2, mPaint);
        super.onDraw(canvas);
    }
}
