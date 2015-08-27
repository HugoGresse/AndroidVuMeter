package io.gresse.hugo.vumeterlibrary;

/**
 * Dynamics point
 * <p/>
 * Based on https://github.com/andersericsson/ViewTutorialPart3/blob/master/src/com/jayway/viewtutorial/part3/Dynamics.java
 */
public class Dynamics {

    /**
     * Used to compare floats, if the difference is smaller than this, they are
     * considered equal
     */
    private static final float TOLERANCE = 0.01f;

    private int mStep;

    /**
     * The mPosition the dynamics should to be at
     */
    private float mTargetPosition;

    /**
     * The current mPosition of the dynamics
     */
    private float mPosition;

    private boolean mIsToTarget;

    public Dynamics(int speed, float position) {
        mStep = speed;
        mPosition = position;
        mIsToTarget = false;
    }

    public void setTargetPosition(float targetPosition) {
        mTargetPosition = targetPosition;
        mIsToTarget = false;
    }

    public void setPosition(float position) {
        mPosition = position;
        mIsToTarget = true;
    }

    public void update() {
        if(mIsToTarget){
            return;
        }

        if(mTargetPosition > mPosition){
            mPosition += mStep;
            if(mPosition >= mTargetPosition){
                mPosition = mTargetPosition;
                mIsToTarget = true;
            }
        } else {
            mPosition -= mStep;
            if(mPosition <= mTargetPosition){
                mPosition = mTargetPosition;
                mIsToTarget = true;
            }
        }

    }

    public boolean isAtRest() {
        return mIsToTarget;
    }

    public void setAtRest(boolean atRest){
        mIsToTarget = atRest;
    }

    public float getPosition() {
        return mPosition;
    }

    public float getTargetPos() {
        return mTargetPosition;
    }

}
