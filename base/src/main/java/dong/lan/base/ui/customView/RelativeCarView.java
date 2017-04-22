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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import dong.lan.base.R;

/**
 * Created by 梁桂栋 on 17-3-25 ： 下午3:55.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class RelativeCarView extends RelativeLayout {

    private static final String TAG = RelativeCarView.class.getSimpleName();
    private int elevation = 0;
    private int elevationColor = Color.GRAY;
    private int backgroundColor = Color.WHITE;
    private int radius = 0;
    private int marginX = 0;
    private int marginY = 0;
    private boolean isAnim = false;
    private Paint paint;
    private RectF bound;

    public RelativeCarView(Context context) {
        this(context, null);
    }

    public RelativeCarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeCarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RelativeCarView);
            backgroundColor = ta.getColor(R.styleable.RelativeCarView_rcv_backgroundColor, Color.WHITE);
            radius = ta.getDimensionPixelSize(R.styleable.RelativeCarView_rcv_radius, 0);
            elevation = ta.getDimensionPixelSize(R.styleable.RelativeCarView_rcv_elevation, 0);
            elevationColor = ta.getColor(R.styleable.RelativeCarView_rcv_elevationColor, Color.GRAY);
            isAnim = ta.getBoolean(R.styleable.RelativeCarView_rcv_anim, false);
            marginX = ta.getDimensionPixelSize(R.styleable.RelativeCarView_rcv_marginX, marginX);
            marginY = ta.getDimensionPixelSize(R.styleable.RelativeCarView_rcv_marginY, marginY);
            ta.recycle();
        }
        bound = new RectF(0, 0, 0, 0);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShadowLayer(elevation, 0, 0, elevationColor);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }


    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.setColor(backgroundColor);
        bound.set(marginX, marginY, getWidth() - marginX, getHeight() - marginY);
        canvas.drawRoundRect(bound, radius, radius, paint);
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isAnim && event.getAction() == MotionEvent.ACTION_DOWN) {
            ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f, 1.0f).setDuration(200).start();
        }
        return super.dispatchTouchEvent(event);
    }

}
