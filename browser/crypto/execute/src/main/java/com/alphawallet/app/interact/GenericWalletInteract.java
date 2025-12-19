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

package com.alphawallet.app.interact;

import static com.alphawallet.app.C.ETHER_DECIMALS;
import static com.alphawallet.app.entity.tokens.Token.TOKEN_BALANCE_PRECISION;

import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.repository.WalletItem;
import com.alphawallet.app.repository.WalletRepositoryType;
import com.alphawallet.app.util.BalanceUtils;

import java.math.BigDecimal;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import timber.log.Timber;

public class GenericWalletInteract
{
	private final WalletRepositoryType walletRepository;

	public GenericWalletInteract(WalletRepositoryType walletRepository) {
		this.walletRepository = walletRepository;
	}

	public Single<Wallet> find() {
		return walletRepository.getDefaultWallet()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Called when wallet marked as backed up.
	 * Update the wallet date
	 *
	 * @param walletAddr
	 */
	public void updateBackupTime(String walletAddr)
	{
		walletRepository.updateBackupTime(walletAddr);
	}

	/**
	 * Called when wallet has had its backup warning dismissed
	 * Update the wallet date
	 *
	 * @param walletAddr
	 */
	public void updateWarningTime(String walletAddr)
	{
		walletRepository.updateWarningTime(walletAddr);
	}

	public Single<String> getWalletNeedsBackup(String walletAddr)
	{
		return walletRepository.getWalletRequiresBackup(walletAddr);
	}

	public void setIsDismissed(String walletAddr, boolean isDismissed)
	{
		walletRepository.setIsDismissed(walletAddr, isDismissed);
	}

	/**
	 * Check if a wallet needs to be backed up.
	 * @param walletAddr
	 * @return
	 */
	public Single<Boolean> getBackupWarning(String walletAddr)
	{
		return walletRepository.getWalletBackupWarning(walletAddr);
	}

	public void updateWalletItem(Wallet wallet, WalletItem item, Realm.Transaction.OnSuccess onSuccess) {
		walletRepository.updateWalletItem(wallet, item, onSuccess);
	}

	public void updateBalanceIfRequired(Wallet wallet, BigDecimal newBalance)
	{
		String newBalanceStr = BalanceUtils.getScaledValueFixed(newBalance, ETHER_DECIMALS, TOKEN_BALANCE_PRECISION);
		if (!newBalance.equals(BigDecimal.valueOf(-1)) && !wallet.balance.equals(newBalanceStr))
		{
			wallet.balance = newBalanceStr;
			walletRepository.updateWalletItem(wallet, WalletItem.BALANCE, () -> {
				Timber.tag(getClass().getCanonicalName()).d("Updated balance");
			});
		}
	}

	public Realm getWalletRealm()
	{
		return walletRepository.getWalletRealm();
	}

    public enum BackupLevel
	{
		BACKUP_NOT_REQUIRED, WALLET_HAS_LOW_VALUE, WALLET_HAS_HIGH_VALUE
	}
}
