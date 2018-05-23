package io.gresse.hugo.vumeterlibrary;

import android.annotation.SuppressLint;
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
 * <p/>
 * Created by Hugo Gresse on 13/08/2015
 */
public class VuMeterView extends View {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = "VuMeterView";

    public static final int DEFAULT_NUMBER_BLOCK = 3;
    public static final int DEFAULT_NUMBER_RANDOM_VALUES = 10;
    public static final int DEFAULT_BLOCK_SPACING = 20;
    public static final int DEFAULT_SPEED = 10;
    public static final int DEFAULT_STOP_SIZE = 30;
    public static final boolean DEFAULT_START_OFF = false;
    public static final int FPS = 60;

    public static final int STATE_PAUSE = 0;
    public static final int STATE_STOP = 1;
    public static final int STATE_PLAYING = 2;

    private int mColor;
    private int mBlockNumber;
    private float mBlockSpacing;
    private int mSpeed;
    private float mStopSize;

    private Paint mPaint = new Paint();
    private Random mRandom = new Random();

    private int mState;

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
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.vumeter_VuMeterView, defStyle, 0);
        mColor = a.getColor(R.styleable.vumeter_VuMeterView_vumeter_backgroundColor, Color.BLACK);
        mBlockNumber = a.getInt(R.styleable.vumeter_VuMeterView_vumeter_blockNumber, DEFAULT_NUMBER_BLOCK);
        mBlockSpacing = a.getDimension(R.styleable.vumeter_VuMeterView_vumeter_blockSpacing, DEFAULT_BLOCK_SPACING);
        mSpeed = a.getInt(R.styleable.vumeter_VuMeterView_vumeter_speed, DEFAULT_SPEED);
        mStopSize = a.getDimension(R.styleable.vumeter_VuMeterView_vumeter_stopSize, DEFAULT_STOP_SIZE);
        boolean startOff = a.getBoolean(R.styleable.vumeter_VuMeterView_vumeter_startOff, DEFAULT_START_OFF);
        a.recycle();

        // Init
        initialiseCollections();
        mPaint.setColor(mColor);

        if(startOff){
            mState = STATE_PAUSE;
        } else {
            mState = STATE_PLAYING;
        }

        mDrawPass = mBlockPass = mContentHeight = mContentWidth = mPaddingLeft = mPaddingTop = mLeft = mTop =
                mPaddingRight = mPaddingBottom = mRight = 0;

    }

    @SuppressLint("DrawAllocation")
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
            mBlockWidth = (int) ((mContentWidth - (mBlockNumber - 1) * mBlockSpacing) / mBlockNumber);

            // called if startOff is true
            if(mState == STATE_PAUSE){
                int stopSize = (int) (mContentHeight - mStopSize);
                for (int i = 0; i < mBlockNumber; i++) {
                    mDestinationValues[i] = new Dynamics(mSpeed, stopSize);
                    mDestinationValues[i].setAtRest(true);
                }
            }
        }


        mBlockPass = 0;
        for (mBlockPass = 0; mBlockPass < mBlockNumber; mBlockPass++) {

            mLeft = mPaddingLeft + mBlockPass * mBlockWidth;

            // Add spacing between blocks
            mLeft += mBlockSpacing * mBlockPass;

            mRight = mLeft + mBlockWidth;

            if (mDestinationValues[mBlockPass] == null) {
                pickNewDynamics(mContentHeight, mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
            }

            if (mDestinationValues[mBlockPass].isAtRest() && mState == STATE_PLAYING) {
                changeDynamicsTarget(mBlockPass, mContentHeight * mBlockValues[mBlockPass][mDrawPass]);
            } else if(mState != STATE_PAUSE){
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
    private void updateRandomValues() {
        for (int i = 0; i < mBlockNumber; i++) {
            for (int j = 0; j < DEFAULT_NUMBER_RANDOM_VALUES; j++) {
                mBlockValues[i][j] = mRandom.nextFloat();
                if (mBlockValues[i][j] < 0.1) {
                    mBlockValues[i][j] = 0.1f;
                }
            }
        }
    }

    /**
     * Create a new Dynamics to be used as first destination
     *
     * @param max      max height
     * @param position the current block position
     */
    private void pickNewDynamics(int max, float position) {
        mDestinationValues[mBlockPass] = new Dynamics(mSpeed, position);
        incrementAndGetDrawPass();
        mDestinationValues[mBlockPass].setTargetPosition(max * mBlockValues[mBlockPass][mDrawPass]);
    }

    /**
     * Update current dynamics to a new destination point
     *
     * @param target the random values created in {@link #updateRandomValues()}
     */
    private void changeDynamicsTarget(int block, float target) {
        incrementAndGetDrawPass();
        mDestinationValues[block].setTargetPosition(target);
    }

    /**
     * Increment the drawPass. Used to pick a value in arrays
     *
     * @return the DrawPass
     */
    private int incrementAndGetDrawPass() {
        mDrawPass++;
        if (mDrawPass >= DEFAULT_NUMBER_RANDOM_VALUES) {
            mDrawPass = 0;
        }
        return mDrawPass;
    }

    private void initialiseCollections(){
        mBlockValues = new float[mBlockNumber][DEFAULT_NUMBER_RANDOM_VALUES];
        mDestinationValues = new Dynamics[mBlockNumber];

        updateRandomValues();
    }

    /**
     * Gets the color of the VuMeter
     *
     * @return The color value.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the VuMeter color value.
     *
     * @param color The example color attribute value to use.
     */
    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
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
        initialiseCollections();
        mBlockPass = 0;
        mBlockWidth = 0;
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
        mBlockWidth = 0;
    }

    /**
     * Get the current animation speed
     *
     * @return the speed
     */
    public int getSpeed() {
        return mSpeed;
    }

    /**
     * Set the current animation speed
     *
     * @param speed The desired speed value
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * Pause the player.
     *
     * Call {@link #resume(boolean)} to replay the VuMeter
     */
    public void pause(){
        mState = STATE_PAUSE;
    }

    /**
     * Stop the VuMeterAnimation by going to the minimum values.
     *
     * @param withAnimation if you want to have an animation from current state to stop state
     */
    public void stop(boolean withAnimation){
        if(mDestinationValues == null){
            initialiseCollections();
        }
        mState = STATE_STOP;
        int collapseSize = (int) (mContentHeight - mStopSize);

        // Prevent NPE in the destinations table is empty
        if(mDestinationValues.length <= 0){
            return;
        }

        for(int i = 0; i < mBlockNumber; i++){
            if(mDestinationValues[i] == null){
                continue;
            }
            if(withAnimation){
                mDestinationValues[i].setTargetPosition(collapseSize);
            } else {
                mDestinationValues[i].setPosition(collapseSize);
            }
        }
    }

    /**
     * Resume/play the VuMeter animation.
     *
     * @param withAnimation if you want to have transition from stop to resume state, set to true
     */
    public void resume(boolean withAnimation){
        if(mState == STATE_PAUSE){
            mState = STATE_PLAYING;
            return;
        }

        mState = STATE_PLAYING;

        if(!withAnimation){
            for(int i = 0; i < mBlockNumber; i++){
                mDestinationValues[i].setPosition(mContentHeight * mBlockValues[i][mDrawPass]);
                changeDynamicsTarget(i, mContentHeight * mBlockValues[i][mDrawPass]);
            }
        }
    }
}
