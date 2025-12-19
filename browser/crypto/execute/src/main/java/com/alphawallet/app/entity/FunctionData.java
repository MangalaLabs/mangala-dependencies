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

package com.alphawallet.app.entity;

import static com.alphawallet.app.entity.ContractType.CREATION;
import static com.alphawallet.app.entity.ContractType.ERC20;
import static com.alphawallet.app.entity.ContractType.ERC875;
import static com.alphawallet.app.entity.ContractType.ERC875_LEGACY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by James on 2/02/2018.
 */

public class FunctionData
{
    public final String functionName;
    public final String functionFullName;
    public String functionRawHex;
    public final List<String> args;
    public final boolean hasSig;
    public final List<ContractType> contractType;

    public FunctionData(String fName, ContractType t)
    {
        functionName = fName;
        functionFullName = fName;
        args = new ArrayList<>();
        hasSig = false;
        contractType = new ArrayList<>();
    }

    public FunctionData(String methodSig, ContractType t, boolean hasSignature)
    {
        int b1Index = methodSig.indexOf("(");
        int b2Index = methodSig.lastIndexOf(")");

        functionName = methodSig.substring(0, b1Index);
        String args = methodSig.substring(b1Index + 1, b2Index);
        String[] argArray = args.split(",");
        List<String> temp = Arrays.asList(argArray);
        this.args = new ArrayList<>();
        this.args.addAll(temp);
        functionFullName = methodSig;
        contractType = new ArrayList<>();
        contractType.add(t);
        hasSig  = hasSignature;

        for (int i = 0; i < temp.size(); i++)//String arg : data.args)
        {
            String arg = temp.get(i);
            if (arg.contains("[]") || arg.equals("string") || arg.equals("bytes"))
            {
                //rearrange to end, no need to store this arg
                this.args.add(arg);
                String argPlaceholder = "nodata";
                this.args.set(i, argPlaceholder);
            }
        }
    }

    public void addType(ContractType type)
    {
        contractType.add(type);
    }

    public boolean isERC20()
    {
        return (contractType.contains(ERC20));
    }

    public boolean isERC875()
    {
        return (contractType.contains(ERC875) || contractType.contains(ERC875_LEGACY));
    }

    public boolean isConstructor()
    {
        return (contractType.contains(CREATION));
    }
}
