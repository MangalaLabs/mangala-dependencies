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

package com.alphawallet.app.service;

import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.cryptokeys.SignatureFromKey;
import com.alphawallet.token.entity.Signable;

import java.math.BigInteger;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface AccountKeystoreService {
	/**
	 * Create account in keystore
	 * @param password account password
	 * @return new {@link Wallet}
	 */
	Single<Wallet> createAccount(String password);

	/**
	 * Include new existing keystore
	 * @param store store to include
	 * @param password store password
	 * @return included {@link Wallet} if success
	 */
	Single<Wallet> importKeystore(String store, String password, String newPassword);
    Single<Wallet> importPrivateKey(String privateKey, String newPassword);

	/**
	 * Export wallet to keystore
	 * @param wallet wallet to export
	 * @param password password from wallet
	 * @param newPassword new password to store
	 * @return store data
	 */
	Single<String> exportAccount(Wallet wallet, String password, String newPassword);

	/**
	 * Delete account from keystore
	 * @param address account address
	 * @param password account password
	 */
	Completable deleteAccount(String address, String password);

	/**
	 * Sign transaction
	 * @param signer {@link Wallet}
	 * @param toAddress transaction destination address
	 * @param nonce
	 * @return sign data
	 */
	Single<SignatureFromKey> signTransaction(
			Wallet signer,
			String toAddress,
			BigInteger amount,
			BigInteger gasPrice,
			BigInteger gasLimit,
			long nonce,
			byte[] data,
			long chainId);

	/**
	 * Sign transaction EIP1559
	 * @param signer {@link Wallet}
	 * @param toAddress transaction destination address
	 * @param nonce
	 * @return sign data
	 */
	Single<SignatureFromKey> signTransactionEIP1559(
			Wallet signer,
			String toAddress,
			BigInteger amount,
			BigInteger gasLimit,
			BigInteger gasPremium,
			BigInteger gasMax,
			long nonce,
			byte[] data,
			long chainId);

	Single<SignatureFromKey> signMessage(
			Wallet signer,
			Signable message,
			long chainId);

	Single<byte[]> signTransactionFast(
			Wallet signer,
			String password,
			byte[] message,
			long chainId);

	/**
	 * Check if there is an address in the keystore
	 * @param address {@link Wallet} address
	 */
	boolean hasAccount(String address);

	/**
	 * Return all {@link Wallet} from keystore
	 * @return wallets
	 */
	Single<Wallet[]> fetchAccounts();
}
