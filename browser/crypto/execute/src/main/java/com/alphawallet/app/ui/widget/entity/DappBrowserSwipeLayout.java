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

package com.alphawallet.app.ui.widget.entity;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by James on 8/07/2019.
 * Stormbird in Sydney
 *
 * This class overrides the SwipeRefreshLayout and makes the swipe refresh less sensitive.
 * To create a swipe refresh event user must make a quick, medium to large downward swipe of less than 300ms;
 * Otherwise a slower event will be treated as a browser scroll event.
 *
 */
public class DappBrowserSwipeLayout extends SwipeRefreshLayout
{
    private float trackMove;
    private boolean alwaysDown;
    private float lastY;
    private boolean canRefresh;
    private DappBrowserSwipeInterface refreshInterface;

    public DappBrowserSwipeLayout(Context context)
    {
        super(context);
    }

    public DappBrowserSwipeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setRefreshInterface(DappBrowserSwipeInterface refresh)
    {
        refreshInterface = refresh;
        alwaysDown = true;
        trackMove = 0.0f;
        canRefresh = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case ACTION_DOWN:
                trackMove = ev.getY();
                canRefresh = (refreshInterface.getCurrentScrollPosition() == 0 && trackMove < 300);
                lastY = trackMove;
                alwaysDown = true;
                break;
            case ACTION_UP:
                float flingDistance = ev.getY() - trackMove;
                if (canRefresh && alwaysDown && flingDistance > 500 && (ev.getEventTime() - ev.getDownTime()) < 500) //User wants a swipe refresh
                {
                    refreshInterface.RefreshEvent();
                }
                break;
            case ACTION_MOVE:
                if ((ev.getY() - lastY) < 0) alwaysDown = false;
                lastY = ev.getY();
                break;
            default:
                break;
        }

        return false;
    }
}
