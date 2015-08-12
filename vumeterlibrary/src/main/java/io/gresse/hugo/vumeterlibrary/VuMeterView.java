package io.gresse.hugo.vumeterlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class VuMeterView extends View {

    public static final int NUMBER_BLOCK = 3;
    public static final int BLOCK_SPACING = 20;
    public static final int FPS = 60;

    private int mColor = Color.RED; // TODO: use a default from R.color...

    private Paint mPaint = new Paint();
    private Random mRandom = new Random();

    // Draw field
    private int mBlockWidth = 0;
    private int mBlockPass = 0;
    private int mContentHeight = 0;
    private int mContentWidth = 0;
    private int mPaddingLeft;
    private int mPaddingTop;

    private int mLeftBlock;
    private int mTopBlock;

    public VuMeterView(Context context) {
        super(context);
        init(null, 0);
    }

    public VuMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VuMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VuMeterView, defStyle, 0);

        mColor = a.getColor(
                R.styleable.VuMeterView_backgroundColor,
                mColor);

        a.recycle();

        mPaint.setColor(mColor);

        mBlockPass = mContentHeight = mContentWidth = mPaddingLeft = mPaddingTop = mLeftBlock = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        mContentWidth = getWidth() - mPaddingLeft - paddingRight;
        mContentHeight = getHeight() - mPaddingTop - paddingBottom;

        if (mBlockWidth == 0) {
            mBlockWidth = mContentWidth / NUMBER_BLOCK - (NUMBER_BLOCK - 1) * BLOCK_SPACING;
        }

        mBlockPass = mLeftBlock = 0;
        for (mBlockPass = 0; mBlockPass < NUMBER_BLOCK; mBlockPass++) {
            mLeftBlock = mBlockPass * mBlockWidth;
            if(mBlockPass > 0){
                mLeftBlock += BLOCK_SPACING;
            }

            mTopBlock = mRandom.nextInt(200);

            canvas.drawRect(
                    mLeftBlock,
                    mTopBlock,
                    mBlockWidth + mBlockPass * mBlockWidth,
                    mContentHeight,
                    mPaint);
        }


        this.postInvalidateDelayed( 1000 / FPS);
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param color The example color attribute value to use.
     */
    public void setColor(int color) {
        mColor = color;
    }

}
