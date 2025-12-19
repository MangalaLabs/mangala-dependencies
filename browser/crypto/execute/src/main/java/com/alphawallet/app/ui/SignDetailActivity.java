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

package com.alphawallet.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.repository.SignRecord;
import com.alphawallet.app.util.Utils;
import com.alphawallet.app.widget.StandardHeader;

import java.util.ArrayList;
public class SignDetailActivity extends BaseActivity
{
    private RecyclerView recyclerView;
    private ArrayList<SignRecord> signRecords;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_list_activity);
        toolbar();
        setTitle(getString(R.string.signed_transactions));
        signRecords = getIntent().getParcelableArrayListExtra(C.EXTRA_STATE);

        if (signRecords != null)
        {
            recyclerView = findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>
    {
        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sign_instance, parent, false);

            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position)
        {
            SignRecord record = signRecords.get(position);
            holder.date.setText(record.type +
                    " (" +
                    Utils.localiseUnixTime(getApplicationContext(), record.date / 1000) +
                    ")");
            holder.detail.setText(record.message);
        }

        @Override
        public int getItemCount()
        {
            return signRecords.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder
        {
            StandardHeader date;
            TextView detail;

            CustomViewHolder(View view)
            {
                super(view);
                date = view.findViewById(R.id.date);
                detail = view.findViewById(R.id.details);
            }
        }
    }
}
