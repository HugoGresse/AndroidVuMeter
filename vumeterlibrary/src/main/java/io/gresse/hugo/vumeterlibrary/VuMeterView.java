package io.gresse.hugo.vumeterlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class VuMeterView extends View {

    public static final String LOG_TAG = "VuMeterView";

    public static final int DEFAULT_NUMBER_BLOCK = 5;
    public static final int DEFAULT_NUMBER_RANDOM_VALUES = 10;
    public static final int DEFAULT_BLOCK_SPACING = 20;
    public static final int FPS = 60;

    private int mColor = Color.BLACK;
    private int mBlockNumber;
    private int mBlockSpacing;

    private Paint mPaint = new Paint();
    private Random mRandom = new Random();

    // Draw field
    private int mBlockWidth;
    private int mDrawPass;
    private int mBlockPass;
    private int mContentHeight;
    private int mContentWidth;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int mLeftBlock;
    private int mTopBlock;

    private float[][] mBlockValues;
    private Dynamics[] mDestinationValues;

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

        mBlockNumber = a.getInt(R.styleable.VuMeterView_blockNumber, DEFAULT_NUMBER_BLOCK);
        mBlockSpacing = a.getInt(R.styleable.VuMeterView_blockSpacing, DEFAULT_BLOCK_SPACING);

        a.recycle();

        // Init
        mBlockValues = new float[mBlockNumber][DEFAULT_NUMBER_RANDOM_VALUES];
        mDestinationValues = new Dynamics[mBlockNumber];
        mPaint.setColor(mColor);

        mDrawPass = mBlockPass = mContentHeight = mContentWidth = mPaddingLeft = mPaddingTop = mLeftBlock = mTopBlock =
                mPaddingRight = mPaddingBottom = 0;

        updateRandomValues();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();

        mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;

        if (mBlockWidth == 0) {
            mBlockWidth = mContentWidth / mBlockNumber - (mBlockNumber - 1) * mBlockSpacing;
        }

        mBlockPass = mLeftBlock = 0;
        for (mBlockPass = 0; mBlockPass < mBlockNumber; mBlockPass++) {
            mLeftBlock = mBlockPass * mBlockWidth;
            if(mBlockPass > 0){
                mLeftBlock += mBlockSpacing;
            }


            if(mDestinationValues[mBlockPass] == null){
                pickNewDynamics(0, mContentHeight, mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
            }

            if (mDestinationValues[mBlockPass].isAtRest()) {
                Log.d(LOG_TAG, "isAtResty");
                changeDynamicsTarget(mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
//                needNewFrame = true;
            } else {

                mDestinationValues[mBlockPass].update();
            }


            mTopBlock = (int) (mDestinationValues[mBlockPass].getPosition());

//            mTopBlock = (int) (mBlockValues[mBlockPass][mDrawPass] * mContentHeight);


            canvas.drawRect(
                    mLeftBlock,
                    mTopBlock,
                    mBlockWidth + mBlockPass * mBlockWidth,
                    mContentHeight,
                    mPaint);
        }

        this.postInvalidateDelayed(1000 / FPS);
    }

    private void updateRandomValues(){
        for(int i = 0; i < mBlockNumber; i++){
            for(int j = 0; j < DEFAULT_NUMBER_RANDOM_VALUES; j++){
                mBlockValues[i][j] = mRandom.nextFloat();
                if(mBlockValues[i][j] < 0.1){
                    mBlockValues[i][j] = 0.1f;
                }
            }

//            mDestinationValues[i] = new Dynamics(70f, 0.30f);
//
//            mDestinationValues[i].setPosition(mBlockValues[i][0], System.currentTimeMillis());
//            mDestinationValues[i].setTargetPosition(mBlockValues[i][1], System.currentTimeMillis());
        }
    }

    private void pickNewDynamics(int min, int max, float position){
        Log.d(LOG_TAG, "pick new Dynamics");
        mDestinationValues[mBlockPass] = new Dynamics(min, max, position);
        incrementAndGetDrawPass();
        mDestinationValues[mBlockPass].setTargetPosition(max * mBlockValues[mBlockPass][mDrawPass]);
//        mDestinationValues[block] = new Dynamics(70f, 0.30f);
    }

    private void changeDynamicsTarget(float target){
        incrementAndGetDrawPass();
        mDestinationValues[mBlockPass].setTargetPosition(target);


    }

    private int incrementAndGetDrawPass(){
        mDrawPass ++;
        if(mDrawPass >= DEFAULT_NUMBER_RANDOM_VALUES){
            mDrawPass = 0;
        }
        return mDrawPass;
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

    /**
     * Get the number of block/rect that the VuMeter will display.
     *
     * @return The number of block
     */
    @SuppressWarnings("unused")
    public int getBlockNumber() {
        return mBlockNumber;
    }

    /**
     * Set the number of block/rect that the VuMeter will display.
     *
     * @param blockNumber The number of block you want to display
     */
    @SuppressWarnings("unused")
    public void setBlockNumber(int blockNumber) {
        mBlockNumber = blockNumber;
    }

    /**
     * Return the spacing between each block
     *
     * @return Spacing value, in px
     */
    @SuppressWarnings("unused")
    public int getBlockSpacing() {
        return mBlockSpacing;
    }

    /**
     * Set the spacing between each block
     *
     * @param blockSpacing Px space between block
     */
    @SuppressWarnings("unused")
    public void setBlockSpacing(int blockSpacing) {
        mBlockSpacing = blockSpacing;
    }
}
