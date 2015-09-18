package com.industry.thomas.circleprogres;

/**
 * Created by Thomas on 2015-09-18.
 */
public interface AnimationStateChangedListener {

    /**
     * Call if animation state changes.
     * This code runs in the animation loop, so keep your code short!
     * @param animationState
     */
    void onAnimationStateChanged(AnimationState animationState);
}
