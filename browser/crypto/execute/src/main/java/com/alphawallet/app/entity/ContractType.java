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

/**
 * Created by James on 6/12/2018.
 * Stormbird in Singapore
 */
//NB: if you add to this list, place your new entry at the end of the list before 'CREATION' because the ordinal is used in storage

public enum ContractType
{
    NOT_SET,
    ETHEREUM,
    ERC20,
    ERC721,
    ERC875_LEGACY,
    ERC875,
    OTHER,
    CURRENCY,
    DELETED_ACCOUNT,
    ERC721_LEGACY,
    ERC721_TICKET,
    ERC721_UNDETERMINED, //when we receive an ERC721 we don't know what kind it is
    DYNAMIC_CONTRACT,
    LEGACY_DYNAMIC_CONTRACT,
    ETHEREUM_INVISIBLE,
    MAYBE_ERC20,
    ERC1155,
    CREATION //Placeholder for generic, should be at end of list
}
