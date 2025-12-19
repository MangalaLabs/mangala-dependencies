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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.DApp;
import com.alphawallet.app.ui.widget.OnDappClickListener;
import com.alphawallet.app.ui.widget.OnDappEditedListener;
import com.alphawallet.app.ui.widget.OnDappRemovedListener;
import com.alphawallet.app.util.DappBrowserUtils;
import com.alphawallet.app.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MyDappsListAdapter extends RecyclerView.Adapter<MyDappsListAdapter.ViewHolder> {
    private List<DApp> data;
    private final OnDappClickListener listener;
    private final OnDappRemovedListener onDappRemovedListener;
    private final OnDappEditedListener onDappEditedListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, url, remove, edit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            edit = itemView.findViewById(R.id.edit);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
        }
    }

    public MyDappsListAdapter(List<DApp> data,
                              OnDappClickListener listener,
                              OnDappRemovedListener onDappRemovedListener,
                              OnDappEditedListener onDappEditedListener) {
        this.data = data;
        this.listener = listener;
        this.onDappRemovedListener = onDappRemovedListener;
        this.onDappEditedListener = onDappEditedListener;
    }

    public void setDapps(List<DApp> dapps) {
        this.data = dapps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_my_dapp_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DApp dApp = data.get(i);
        String visibleUrl = Utils.getDomainName(dApp.getUrl());
        viewHolder.name.setText(dApp.getName());
        viewHolder.url.setText(visibleUrl);

        String favicon;
        if (!TextUtils.isEmpty(visibleUrl)) {
            favicon = DappBrowserUtils.getIconUrl(visibleUrl);
            Glide.with(viewHolder.icon.getContext())
                    .load(favicon)
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().placeholder(com.schoolonair.wallet.component.resources.R.drawable.logo_full))
                    .into(viewHolder.icon);

            viewHolder.icon.setOnClickListener(v -> {
                listener.onDappClick(dApp);
            });
        }

        viewHolder.remove.setOnClickListener(v -> onDappRemovedListener.onDappRemoved(dApp));

        viewHolder.edit.setOnClickListener(v -> onDappEditedListener.onDappEdited(dApp));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
