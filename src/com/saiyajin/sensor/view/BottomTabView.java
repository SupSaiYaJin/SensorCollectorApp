package com.saiyajin.sensor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.saiyajin.sensor.R;

/**
 * 主页底部图标View
 */
public class BottomTabView extends View
{
    /** 遮罩Bitmap */
    private Bitmap mBitmap;
    /** 遮罩Paint */
    private Paint mPaint;
    /** 遮罩Canvas */
    private Canvas mCanvas;
    /** 选中时的颜色 */
    private int mColor = 0xFF45C01A;
    /** 遮罩透明度 0.0-1.0 */
    private float mAlpha = 0f;
    /** 图标 */
    private Bitmap mIconBitmap;
    /** 图标的绘制范围 */
    private Rect mIconRect = new Rect();
    /** 底部文本 */
    private String mText = "微信";
    /** 底部文本的大小 */
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
    /** 绘制底部文本的Paint */
    private Paint mTextPaint;
    /** 底部文本的绘制范围 */
    private Rect mTextBound = new Rect();

    public BottomTabView(Context context)
    {
        super(context);
    }

    /**
     * 初始化自定义属性值
     * @param context 上下文
     * @param attrs 属性
     */
    public BottomTabView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // 获取设置的图标
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BottomTabView);

        BitmapDrawable drawable = (BitmapDrawable) attributes.getDrawable(R.styleable.BottomTabView_icon);
        if (drawable != null) {
            mIconBitmap = drawable.getBitmap();
        }
        mColor = attributes.getColor(R.styleable.BottomTabView_color, 0xff45C01A);
        mText = attributes.getString(R.styleable.BottomTabView_text);
        mTextSize = (int) attributes.getDimension(R.styleable.BottomTabView_text_size, mTextSize);

        attributes.recycle();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

        //绘制遮罩
        mBitmap = mIconBitmap.copy(Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mCanvas.drawColor(mColor, PorterDuff.Mode.SRC_IN);
        mCanvas.save();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 得到绘制icon的宽
        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom() - mTextBound.height());

        int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth
                / 2;
        // 设置icon的绘制范围
        mIconRect.set(left, top, left + bitmapWidth, top + bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //转化透明度
        int alpha = (int) Math.ceil((255 * mAlpha));

        setupTargetBitmap(canvas, alpha);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);

    }

    private void setupTargetBitmap(Canvas canvas, int alpha)
    {
        //绘制原始图标
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);

        //重用遮罩
        mCanvas.restore();
        mCanvas.save();
        mPaint.setAlpha(alpha);

        //绘制遮罩
        canvas.drawBitmap(mBitmap, null, mIconRect, mPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha)
    {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff6C7578);
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height(), mTextPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha)
    {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height(), mTextPaint);
    }

    public void setIconAlpha(float alpha)
    {
        this.mAlpha = alpha;
        invalidate();
    }
}
