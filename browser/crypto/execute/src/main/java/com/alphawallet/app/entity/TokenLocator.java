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

import com.alphawallet.app.entity.tokenscript.TokenScriptFile;
import com.alphawallet.token.entity.ContractInfo;

public class TokenLocator extends ContractInfo
{
    private final TokenScriptFile tokenScriptFile;
    private final String name;
    private final boolean error;
    private final String errorMessage;

    public TokenLocator(String name, ContractInfo origins, TokenScriptFile file) {
        super(origins.contractInterface, origins.addresses);
        this.name = name;
        this.tokenScriptFile = file;
        this.error = false;
        this.errorMessage = "";
    }

    public TokenLocator(String name, ContractInfo origins, TokenScriptFile file, boolean error, String errorMessage) {
        super(origins.contractInterface, origins.addresses);
        this.name = name;
        this.tokenScriptFile = file;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return tokenScriptFile.getName();
    }
    public String getFullFileName() { return tokenScriptFile.getAbsolutePath(); }

    public ContractInfo getContracts() {
        return this;
    }

    public boolean isDebug() { return tokenScriptFile.isDebug(); }

    public String getDefinitionName() { return name; }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
