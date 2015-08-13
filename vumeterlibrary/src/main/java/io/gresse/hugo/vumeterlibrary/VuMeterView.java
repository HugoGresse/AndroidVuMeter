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
 * A (fake) VuMeterView.
 *
 * Created by Hugo Gresse on 13/08/2015
 */
public class VuMeterView extends View {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = "VuMeterView";

    public static final int DEFAULT_NUMBER_BLOCK = 3;
    public static final int DEFAULT_NUMBER_RANDOM_VALUES = 10;
    public static final int DEFAULT_BLOCK_SPACING = 20;
    public static final int DEFAULT_SPEED = 10;
    public static final int FPS = 60;

    private int mColor = Color.BLACK;
    private int mBlockNumber;
    private float mBlockSpacing;
    private int mSpeed;

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
    private int mLeft;
    private int mTop;
    private int mRight;

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
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VuMeterView, defStyle, 0);
        mColor = a.getColor(R.styleable.VuMeterView_backgroundColor, mColor);
        mBlockNumber = a.getInt(R.styleable.VuMeterView_blockNumber, DEFAULT_NUMBER_BLOCK);
        mBlockSpacing = a.getDimension(R.styleable.VuMeterView_blockSpacing, DEFAULT_BLOCK_SPACING);
        mSpeed = a.getInt(R.styleable.VuMeterView_speed, DEFAULT_SPEED);
        a.recycle();

        // Init
        mBlockValues = new float[mBlockNumber][DEFAULT_NUMBER_RANDOM_VALUES];
        mDestinationValues = new Dynamics[mBlockNumber];
        mPaint.setColor(mColor);

        mDrawPass = mBlockPass = mContentHeight = mContentWidth = mPaddingLeft = mPaddingTop = mLeft = mTop =
                mPaddingRight = mPaddingBottom = mRight = 0;

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
            mBlockWidth = (int) ((mContentWidth - (mBlockNumber-1) * mBlockSpacing) / mBlockNumber);
        }

        mBlockPass = 0;
        for (mBlockPass = 0; mBlockPass < mBlockNumber; mBlockPass++) {

            mLeft = mPaddingLeft + mBlockPass * mBlockWidth;

            // Add spacing between blocks
            mLeft += mBlockSpacing * mBlockPass;

            mRight = mLeft + mBlockWidth;

            if(mDestinationValues[mBlockPass] == null){
                pickNewDynamics(mContentHeight, mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
            }

            if (mDestinationValues[mBlockPass].isAtRest()) {
                changeDynamicsTarget(mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
            } else {
                mDestinationValues[mBlockPass].update();
            }

            mTop = mPaddingTop + (int) (mDestinationValues[mBlockPass].getPosition());

            canvas.drawRect(
                    mLeft,
                    mTop,
                    mRight,
                    mContentHeight,
                    mPaint);
        }

        this.postInvalidateDelayed(1000 / FPS);
    }

    /**
     * Create random values to be picked when creating a new Dynamics
     */
    private void updateRandomValues(){
        for(int i = 0; i < mBlockNumber; i++){
            for(int j = 0; j < DEFAULT_NUMBER_RANDOM_VALUES; j++){
                mBlockValues[i][j] = mRandom.nextFloat();
                if(mBlockValues[i][j] < 0.1){
                    mBlockValues[i][j] = 0.1f;
                }
            }
        }
    }

    /**
     * Create a new Dynamics to be used as first destination
     *
     * @param max max height
     * @param position the current block position
     */
    private void pickNewDynamics(int max, float position){
        mDestinationValues[mBlockPass] = new Dynamics(mSpeed, position);
        incrementAndGetDrawPass();
        mDestinationValues[mBlockPass].setTargetPosition(max * mBlockValues[mBlockPass][mDrawPass]);
    }

    /**
     * Update current dynamics to a new destination point
     * @param target the random values created in {@link #updateRandomValues()}
     */
    private void changeDynamicsTarget(float target){
        incrementAndGetDrawPass();
        mDestinationValues[mBlockPass].setTargetPosition(target);
    }

    /**
     * Increment the drawPass. Used to pick a value in arrays
     *
     * @return the DrawPass
     */
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
    public float getBlockSpacing() {
        return mBlockSpacing;
    }

    /**
     * Set the spacing between each block
     *
     * @param blockSpacing dp space between block
     */
    @SuppressWarnings("unused")
    public void setBlockSpacing(float blockSpacing) {
        mBlockSpacing = blockSpacing;
    }
}
