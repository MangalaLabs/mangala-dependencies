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

package com.alphawallet.app.ui.widget.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.entity.nftassets.NFTAsset;
import com.alphawallet.app.ui.widget.holder.NFTAssetAmountHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by JB on 22/08/2021.
 */
public class NFTAssetCountAdapter extends RecyclerView.Adapter<NFTAssetAmountHolder> {
    private final List<NFTAsset> assets;

    public NFTAssetCountAdapter(List<NFTAsset> data)
    {
        this.assets = data;
    }

    @NotNull
    @Override
    public NFTAssetAmountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new NFTAssetAmountHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NotNull NFTAssetAmountHolder holder, int position)
    {
        holder.bind(assets.get(position));
    }

    @Override
    public int getItemCount()
    {
        return assets.size();
    }
}
