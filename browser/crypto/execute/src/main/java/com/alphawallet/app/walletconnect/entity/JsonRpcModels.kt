/*
 * Copyright (c) 2019-2023 AlphaWallet
 * Copyright (c) 2023-2025 Mangala Wallet
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
 * Modified from original source: https://github.com/AlphaWallet/alpha-wallet-android
 */

package com.alphawallet.app.walletconnect.entity

private const val JSONRPC_VERSION = "2.0"
private const val SERVER_ERROR = -32000;
private const val INVALID_PARAMS = -32602;
private const val INVALID_REQUEST = -32600;
private const val PARSE_ERROR = -32700;
private const val NOT_FOUND = -32601;
private const val UNRECOGNISED = 4902;

data class JsonRpcRequest<T>(
        val id: Long,
        val jsonrpc: String = JSONRPC_VERSION,
        val method: WCMethod?,
        val params: T
)

data class JsonRpcResponse<T>(
        val jsonrpc: String = JSONRPC_VERSION,
        val id: Long,
        val result: T?
)

data class JsonRpcErrorResponse(
        val jsonrpc: String = JSONRPC_VERSION,
        val id: Long,
        val error: JsonRpcError
)

data class JsonRpcError(
        val code: Int,
        val message: String
) {
    companion object {
        fun serverError(message: String) = JsonRpcError(SERVER_ERROR, message)
        fun invalidParams(message: String) = JsonRpcError(INVALID_PARAMS, message)
        fun invalidRequest(message: String) = JsonRpcError(INVALID_REQUEST, message)
        fun parseError(message: String) = JsonRpcError(PARSE_ERROR, message)
        fun methodNotFound(message: String) = JsonRpcError(NOT_FOUND, message)
        fun unrecognisedChain(message: String) = JsonRpcError(UNRECOGNISED, message)
    }
}
