/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.app.widget;

import static android.view.animation.Animation.INFINITE;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alphawallet.app.R;

/**
 * Created by JB on 28/11/2020.
 */
public class ProgressKnobkerry extends RelativeLayout
{
    private final ProgressBar spinner;
    private final ProgressBar spinnerKnob;
    private final ProgressBar indeterminate;
    private final ImageView progressComplete;
    private final Context context;

    public ProgressKnobkerry(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        inflate(ctx, R.layout.item_progress_knobkerry, this);
        spinner = (ProgressBar) findViewById(R.id._progress_bar_main);
        spinnerKnob = (ProgressBar) findViewById(R.id._progress_bar_knob);
        indeterminate = (ProgressBar) findViewById(R.id._progress_bar_waiting);
        progressComplete = (ImageView) findViewById(R.id._progress_complete);
        context = ctx;
    }

    public void startAnimation(long secondsToComplete)
    {
        spinner.setVisibility(View.VISIBLE);
        spinnerKnob.setVisibility(View.VISIBLE);
        indeterminate.setVisibility(View.GONE);

        ObjectAnimator animation = ObjectAnimator.ofInt(spinner, "progress", 0, 500);
        animation.setDuration(secondsToComplete * DateUtils.SECOND_IN_MILLIS);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_knob_anim);
        rotation.setDuration(secondsToComplete * DateUtils.SECOND_IN_MILLIS);
        rotation.setRepeatCount(INFINITE);
        spinnerKnob.startAnimation(rotation);
    }

    public void startAnimation(long startSeconds, long endSeconds)
    {
        spinner.setVisibility(View.VISIBLE);
        spinnerKnob.setVisibility(View.VISIBLE);
        indeterminate.setVisibility(View.GONE);

        if (endSeconds < startSeconds) endSeconds = startSeconds + 60; //hack to avoid crashes

        final long currentSeconds = System.currentTimeMillis() / 1000;
        final long spinnerCycleTime = endSeconds - startSeconds;

        float fractionComplete = (float) (currentSeconds - startSeconds) / (float) (spinnerCycleTime);
        if (fractionComplete > 1.0)
            fractionComplete = 1.0f;
        final int completionDuration = currentSeconds < endSeconds ?
                (int) (endSeconds - currentSeconds) : 1;

        ObjectAnimator animation = ObjectAnimator.ofInt(spinner, "progress", (int) (fractionComplete * 500), 500);
        animation.setDuration(completionDuration * DateUtils.SECOND_IN_MILLIS);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        float startDegrees = 360.0f * fractionComplete;

        //rotate the knob to completion, then continue spinning at the same rate
        rotateKnob(startDegrees, completionDuration, false).setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                rotateKnob(0, spinnerCycleTime, true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    private RotateAnimation rotateKnob(float startDegrees, long completionDuration, boolean infinite)
    {
        RotateAnimation rotate = new RotateAnimation(startDegrees, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration((completionDuration) * 1000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(infinite ? INFINITE : 0);
        spinnerKnob.startAnimation(rotate);
        return rotate;
    }

    public void waitCycle()
    {
        spinner.setVisibility(View.GONE);
        spinnerKnob.setVisibility(View.GONE);
        indeterminate.setVisibility(View.VISIBLE);

        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_knob_anim);
        rotation.setDuration(2 * DateUtils.SECOND_IN_MILLIS);
        rotation.setRepeatCount(INFINITE);
        indeterminate.startAnimation(rotation);
    }

    public void setComplete(boolean succeeded)
    {
        indeterminate.clearAnimation();
        spinnerKnob.clearAnimation();
        spinner.clearAnimation();

        if (succeeded)
        {
            progressComplete.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_correct);
        }
        else
        {
            progressComplete.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_tx_fail);
        }

        progressComplete.setAlpha(0.0f);
        progressComplete.animate().alpha(1.0f).setDuration(500).setListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                spinner.setVisibility(View.GONE);
                spinnerKnob.setVisibility(View.GONE);
                indeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        progressComplete.setVisibility(View.VISIBLE);
    }
}
