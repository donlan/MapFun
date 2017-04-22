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
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

import dong.lan.base.R;
import dong.lan.base.utils.DisplayUtils;

/**
 * 圆形图片控件
 */
public class PinCircleImageView extends android.support.v7.widget.AppCompatImageView {


    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint paint;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private RectF bound;
    private int radius = 0;
    private int bgColor = Color.YELLOW;
    private int padding = 10;
    private Paint bgPaint;
    private int mWidth = 0;
    private int mHeight = 0;

    public PinCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PinCircleImageView);
            bgColor = ta.getColor(R.styleable.PinCircleImageView_pci_bg_color, bgColor);
            padding = ta.getDimensionPixelSize(R.styleable.PinCircleImageView_pci_padding, padding);
            ta.recycle();
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bound = new RectF(0, 0, 0, 0);
        init();
    }

    public void setBgColor(int color) {
        bgColor = color;
        invalidate();
    }

    public void setPadding(int paddingDpVal) {
        paddingDpVal = DisplayUtils.dip2px(getContext(), paddingDpVal);
        invalidate();
    }

    public void init(Bitmap bmp, int width, int height, int bgColor, int padding) {
        setImageBitmap(bmp);
        this.bgColor = bgColor;
        this.padding = padding;
        measure(MeasureSpec.makeMeasureSpec(DisplayUtils.dip2px(getContext(), width), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(DisplayUtils.dip2px(getContext(), width), MeasureSpec.EXACTLY));
    }


    public void init(int width, int height, int bgColor, int padding) {
        this.bgColor = bgColor;
        this.padding = padding;

        mWidth = Math.min(width, height);
        radius = mWidth / 2;
        int h = (int) (radius / Math.sin(Math.PI / 4) * 3);
        if (height < mWidth + h) {
            height = mWidth + h;
        }
        if (bound == null)
            bound = new RectF(0, 0, 0, 0);
        bound.set(0, 0, radius, radius);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hs = MeasureSpec.getSize(heightMeasureSpec);
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        if (wm == MeasureSpec.EXACTLY && hm == MeasureSpec.EXACTLY) {
            mWidth = Math.min(hs, ws);
            radius = mWidth / 2;
            int h = (int) (radius / Math.sin(Math.PI / 4) * 3);
            if (hs < mWidth + h) {
                hs = mWidth + h;
            }
        } else if (wm == MeasureSpec.AT_MOST && hm == MeasureSpec.AT_MOST) {
            mWidth = Math.max(hs, ws);
            radius = mWidth / 2;
            int h = (int) (radius / Math.sin(Math.PI / 4) * 3);
            hs = mWidth + h;
        }
        mHeight = hs;
        bound.set(0, 0, radius, radius);
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        bgPaint.setColor(bgColor);
        canvas.save();
        canvas.translate(radius, radius);
        canvas.rotate(45);
        canvas.drawRect(bound, bgPaint);
        canvas.restore();
        canvas.drawCircle(radius, radius, radius, bgPaint);

        if (mBitmap == null || mBitmapShader == null || mBitmap.getHeight() == 0 || mBitmap.getWidth() == 0) {
            bgPaint.setColor(Color.WHITE);
            canvas.drawCircle(radius, radius, radius - padding, bgPaint);
        } else {
            updateBitmapShader();
            paint.setShader(mBitmapShader);
            canvas.drawCircle(radius, radius, radius - padding, paint);
        }

    }

    private void init() {
        if (mBitmap == null) return;
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        init();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        init();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        init();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
        init();
    }


    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateBitmapShader() {
        if (mBitmap == null)
            return;
        int canvasSize = Math.min(mWidth, mHeight) - padding;
        if (canvasSize == 0) return;
        if (canvasSize != mBitmap.getWidth() || canvasSize != mBitmap.getHeight()) {
            Matrix matrix = new Matrix();
            float scale = (float) canvasSize / (float) mBitmap.getWidth();
            matrix.setScale(scale, scale);
            mBitmapShader.setLocalMatrix(matrix);
        }
    }
}
