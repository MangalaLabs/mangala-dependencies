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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.ActionSheetInterface;
import com.alphawallet.app.entity.nftassets.NFTAsset;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.util.Utils;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jenny Jingjing Li on 13/05/2021
 */

public class AssetDetailView extends LinearLayout
{
    private final TextView assetName;
    private final TextView assetDescription;
    private final LinearLayout layoutDetails;
    private final ImageView assetDetails;
    private final LinearLayout layoutHolder;
    private final ProgressBar loadingSpinner;
    private final NFTImageView imageView;

    @Nullable
    private Disposable disposable;

    public AssetDetailView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.item_asset_detail, this);
        assetName = findViewById(R.id.text_asset_name);
        assetDescription = findViewById(R.id.text_asset_description);
        assetDetails = findViewById(R.id.image_more);
        layoutDetails = findViewById(R.id.layout_details);
        layoutHolder = findViewById(R.id.layout_holder);
        imageView = findViewById(R.id.asset_image);
        loadingSpinner = findViewById(R.id.loading_spinner);
    }

    public void setupAssetDetail(Token token, String tokenId, final ActionSheetInterface actionSheetInterface)
    {
        NFTAsset asset = token.getAssetForToken(tokenId);
        if (asset == null)
        {
            loadingSpinner.setVisibility(View.VISIBLE);
            disposable = fetchAsset(token, tokenId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fetchedAsset -> setupAssetDetail(fetchedAsset, actionSheetInterface), error -> {  });
        }
        else
        {
            setupAssetDetail(asset, actionSheetInterface);
        }
    }

    private void setupAssetDetail(NFTAsset asset, ActionSheetInterface actionSheetInterface) throws IllegalArgumentException
    {
        if (!Utils.stillAvailable(getContext())) return;
        loadingSpinner.setVisibility(View.GONE);

        layoutHolder.setVisibility(View.VISIBLE);
        assetName.setText(asset.getName());
        imageView.setupTokenImage(asset);

        assetDescription.setText(asset.getDescription());

        if (assetDetails.getVisibility() != View.GONE)
        {
            layoutHolder.setOnClickListener(v -> {
                if (layoutDetails.getVisibility() == View.GONE)
                {
                    layoutDetails.setVisibility(View.VISIBLE);
                    assetDetails.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_expand_less_black);
                    if (actionSheetInterface != null) actionSheetInterface.fullExpand();
                }
                else
                {
                    layoutDetails.setVisibility(View.GONE);
                    assetDetails.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_expand_more);
                }
            });
        }
    }

    private Single<NFTAsset> fetchAsset(Token token, String tokenId)
    {
        return Single.fromCallable(() -> {
            return token.fetchTokenMetadata(new BigInteger(tokenId)); //fetch directly from token
        });
    }

    public void setFullyExpanded()
    {
        layoutDetails.setVisibility(View.VISIBLE);
        assetDetails.setVisibility(View.GONE);
        findViewById(R.id.spacing_line).setVisibility(View.GONE);
        layoutHolder.setOnClickListener(null);
    }

    public void onDestroy()
    {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
    }
}
