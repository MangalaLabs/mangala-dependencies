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

import com.alphawallet.app.ui.widget.holder.BinderViewHolder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class SortedItem<T> {
    protected final List<Integer> tags = new ArrayList<>();

    public int viewType;
    public final T value;
    public TokenPosition weight;
    public BinderViewHolder view;

    public SortedItem(int viewType, T value, TokenPosition weight) {
        this.viewType = viewType;
        this.value = value;
        this.weight = weight;
    }

    public int compare(SortedItem other)
    {
        if (value instanceof TokenSortedItem && other.value instanceof TokenSortedItem) //we may need to order tokens with the same name
        {
            return ((TokenSortedItem) value).compare(other);
        }
        else
        {
            return weight.compare(other.weight);
        }
    }

    public abstract boolean areContentsTheSame(SortedItem newItem);

    public abstract boolean areItemsTheSame(SortedItem other);

    public boolean isRadioExposed()
    {
        return false;
    }

    public boolean isItemChecked()
    {
        return false;
    }

    public void setIsChecked(boolean b) { }

    public void setExposeRadio(boolean expose) { }

    public List<BigInteger> getTokenIds()
    {
        return new ArrayList<>();
    }
}
