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

package com.alphawallet.app.viewmodel;

import static com.alphawallet.ethereum.EthereumNetworkBase.MAINNET_ID;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphawallet.app.C;
import com.alphawallet.app.entity.AnalyticsProperties;
import com.alphawallet.app.entity.DAppFunction;
import com.alphawallet.app.entity.GenericCallback;
import com.alphawallet.app.entity.NetworkInfo;
import com.alphawallet.app.entity.SendTransactionInterface;
import com.alphawallet.app.entity.SignAuthenticationCallback;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.WalletConnectActions;
import com.alphawallet.app.entity.walletconnect.WalletConnectSessionItem;
import com.alphawallet.app.interact.CreateTransactionInteract;
import com.alphawallet.app.interact.FindDefaultNetworkInteract;
import com.alphawallet.app.interact.GenericWalletInteract;
import com.alphawallet.app.repository.EthereumNetworkRepositoryType;
import com.alphawallet.app.repository.SignRecord;
import com.alphawallet.app.repository.entity.RealmWCSignElement;
import com.alphawallet.app.service.AnalyticsServiceType;
import com.alphawallet.app.service.GasService;
import com.alphawallet.app.service.KeyService;
import com.alphawallet.app.service.RealmManager;
import com.alphawallet.app.service.TokensService;
import com.alphawallet.app.service.WalletConnectService;
import com.alphawallet.app.walletconnect.WCClient;
import com.alphawallet.app.walletconnect.entity.GetClientCallback;
import com.alphawallet.app.walletconnect.entity.WCUtils;
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject;
import com.alphawallet.app.web3.entity.Web3Transaction;
import com.alphawallet.token.entity.EthereumMessage;
import com.alphawallet.token.entity.EthereumTypedMessage;
import com.alphawallet.token.entity.SignMessageType;
import com.alphawallet.token.entity.Signable;
import com.alphawallet.token.tools.Numeric;
import com.google.gson.Gson;
import com.schoolonair.wallet.local.walletconnect.WC1SessionStorage;
import com.schoolonair.wallet.local.walletconnect.WCSignElement;
import com.schoolonair.wallet.local.walletconnect.WCSignElementStorage;
import com.schoolonair.wallet.local.walletconnect.WalletConnectSession;
import com.trustwallet.walletconnect.models.WCPeerMeta;
import com.trustwallet.walletconnect.models.session.WCSession;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;


public class WalletConnectViewModel extends BaseViewModel
{
    public static final String WC_SESSION_DB = "wc_data-db.realm";
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<Boolean> serviceReady = new MutableLiveData<>();
    protected Disposable disposable;
    private final KeyService keyService;
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    private final GenericWalletInteract genericWalletInteract;
    private final CreateTransactionInteract createTransactionInteract;
    private final RealmManager realmManager;
    private final GasService gasService;
    private final TokensService tokensService;
    private final AnalyticsServiceType analyticsService;
    private final EthereumNetworkRepositoryType ethereumNetworkRepository;

    private final WC1SessionStorage wc1SessionStorage;

    private final WCSignElementStorage wcSignElementStorage;

    private final HashMap<String, WCClient> clientBuffer = new HashMap<>();

    private Wallet wallet;

    @Nullable
    private Disposable prepareDisposable;

    private static final String TAG = "WCClientVM";


    public WalletConnectViewModel(KeyService keyService,
                           FindDefaultNetworkInteract findDefaultNetworkInteract,
                           CreateTransactionInteract createTransactionInteract,
                           GenericWalletInteract genericWalletInteract,
                           RealmManager realmManager,
                           GasService gasService,
                           TokensService tokensService,
                           AnalyticsServiceType analyticsService,
                           EthereumNetworkRepositoryType ethereumNetworkRepository,
                           WC1SessionStorage wc1SessionStorage,
                           WCSignElementStorage wcSignElementStorage
                           )
    {
        this.keyService = keyService;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.createTransactionInteract = createTransactionInteract;
        this.genericWalletInteract = genericWalletInteract;
        this.realmManager = realmManager;
        this.gasService = gasService;
        this.tokensService = tokensService;
        this.analyticsService = analyticsService;
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.wc1SessionStorage = wc1SessionStorage;
        this.wcSignElementStorage = wcSignElementStorage;
        prepareDisposable = null;
//        disposable = genericWalletInteract
//                .find()
//                .subscribe(w -> this.wallet = w, this::onError);
    }

    public void startService(Context context)
    {
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                Timber.tag(TAG).d("Service connected");
                for (String sessionId : clientBuffer.keySet())
                {
                    Timber.tag(TAG).d("put from buffer: %s", sessionId);
                    WCClient c = clientBuffer.get(sessionId);
                    walletConnectService.putClient(sessionId, c);
                }
                clientBuffer.clear();
                serviceReady.postValue(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                Timber.tag(TAG).d("Service disconnected");
            }
        };

        WCUtils.startServiceLocal(context, connection, WalletConnectActions.CONNECT);
    }

    public void prepare()
    {
        prepareDisposable = genericWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    public void startGasCycle(long chainId)
    {
        gasService.startGasPriceCycle(chainId);
    }

    public TokensService getTokensService()
    {
        return tokensService;
    }

    public void onDestroy()
    {
        gasService.stopGasPriceCycle();
    }

    public void onDefaultWallet(Wallet w)
    {
        this.wallet = w;
        defaultWallet.postValue(w);
    }

    public LiveData<Wallet> defaultWallet()
    {
        return defaultWallet;
    }

    public Wallet getWallet()
    {
        return wallet;
    }

    public LiveData<Boolean> serviceReady()
    {
        return serviceReady;
    }

    public void getAuthenticationForSignature(Wallet wallet, Activity activity, SignAuthenticationCallback callback)
    {
        keyService.getAuthenticationForSignature(wallet, activity, callback);
    }

    public void signMessage(Signable message, DAppFunction dAppFunction)
    {
        resetSignDialog();
        disposable = createTransactionInteract.sign(defaultWallet.getValue(), message, MAINNET_ID)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sig -> dAppFunction.DAppReturn(sig.signature, message),
                        error -> dAppFunction.DAppError(error, message));
    }

    public void signTransaction(Context ctx, Web3Transaction w3tx, DAppFunction dAppFunction, String requesterURL, long chainId)
    {
        resetSignDialog();
        EthereumMessage etm = new EthereumMessage(w3tx.getFormattedTransaction(ctx, chainId, getNetworkSymbol(chainId)).toString(),
                requesterURL, w3tx.leafPosition, SignMessageType.SIGN_MESSAGE);

        if (w3tx.isConstructor())
        {
            disposable = createTransactionInteract.signTransaction(defaultWallet.getValue(), w3tx, chainId)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sig -> dAppFunction.DAppReturn(Numeric.hexStringToByteArray(sig.signature), etm),
                            error -> dAppFunction.DAppError(error, etm));
        }
        else
        {
            disposable = createTransactionInteract.signTransaction(defaultWallet.getValue(), w3tx, chainId)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sig -> dAppFunction.DAppReturn(Numeric.hexStringToByteArray(sig.signature), etm),
                            error -> dAppFunction.DAppError(error, etm));
        }
    }

    public void sendTransaction(final Web3Transaction finalTx, long chainId, SendTransactionInterface callback)
    {
        if (finalTx.isConstructor())
        {
            disposable = createTransactionInteract
                    .createWithSig(defaultWallet.getValue(), finalTx.gasPrice, finalTx.gasLimit, finalTx.payload, chainId)
                    .subscribe(txData -> callback.transactionSuccess(finalTx, txData.txHash),
                            error -> callback.transactionError(finalTx.leafPosition, error));
        }
        else
        {
            disposable = createTransactionInteract
                    .createWithSig(defaultWallet.getValue(), finalTx, chainId)
                    .subscribe(txData -> callback.transactionSuccess(finalTx, txData.txHash),
                            error -> callback.transactionError(finalTx.leafPosition, error));
        }
    }

    public Single<BigInteger> calculateGasEstimate(Wallet wallet, byte[] transactionBytes, long chainId, String sendAddress, BigDecimal sendAmount, BigInteger defaultLimit)
    {
        return gasService.calculateGasEstimate(transactionBytes, chainId, sendAddress, sendAmount.toBigInteger(), wallet, defaultLimit);
    }

    public void resetSignDialog()
    {
        keyService.resetSigningDialog();
    }

    // shoulde replace remotePeerId
    public WCSession getSession(String sessionId)
    {
        WCSession session = null;
        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
//        if(data != null) session = data.getSession();
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//
//            if (sessionData != null)
//            {
////                session = sessionData.getSession();
//            }
//        }

        return session;
    }

    public WCPeerMeta getRemotePeer(String sessionId)
    {
        WCPeerMeta peerMeta = null;
        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
//        if(data != null) peerMeta = data.getRemotePeerMeta();

//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//
//            if (sessionData != null)
//            {
//                peerMeta = sessionData.getRemotePeerData();
//            }
//        }

        return peerMeta;
    }

    public String getRemotePeerId(String sessionId)
    {
        String remotePeerId = null;

        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
        if(data != null) remotePeerId = data.getRemotePeerId();

//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//
//            if (sessionData != null)
//            {
//                remotePeerId = sessionData.getRemotePeerId();
//            }
//        }

        return remotePeerId;
    }

    public String getPeerId(String sessionId)
    {
        String peerId = null;
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//
//            if (sessionData != null)
//            {
//                peerId = sessionData.getPeerId();
//            }
//        }

        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
        if(data != null) peerId = data.getPeerId();

        return peerId;
    }

    public long getChainId(String sessionId)
    {
        long chainId = MAINNET_ID;
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//
//            if (sessionData != null)
//            {
//                chainId = sessionData.getChainId();
//            }
//        }
        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
        if(data != null) chainId = data.getChainId();

        return chainId;
    }

    private Gson gson = new Gson();

    public void createNewSession(String sessionId, String peerId, String remotePeerId, String sessionData,
                                 String remotePeerData, long sessionChainId, String accountId)
    {
        WCSession session = gson.fromJson(sessionData, WCSession.class);
        WCPeerMeta wCPeerMeta = gson.fromJson(remotePeerData, WCPeerMeta.class);
//        WalletConnectSession walletConnectSession = new WalletConnectSession(sessionId, (int)sessionChainId, accountId, session, peerId, remotePeerId, wCPeerMeta, false, new Date());
        WalletConnectSession walletConnectSession = new WalletConnectSession(sessionId, (int)sessionChainId, accountId, peerId, remotePeerId, false, new Date());
        wc1SessionStorage.save(walletConnectSession);
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                RealmWCSession sessionAux = r.where(RealmWCSession.class)
//                        .equalTo("sessionId", sessionId)
//                        .findFirst();
//
//                if (sessionAux == null)
//                    sessionAux = r.createObject(RealmWCSession.class, sessionId);
//
//                sessionAux.setLastUsageTime(System.currentTimeMillis());
//                sessionAux.setRemotePeerId(remotePeerId);
//                sessionAux.setPeerId(peerId);
//                sessionAux.setRemotePeerData(remotePeerData);
//                sessionAux.setSessionData(sessionData);
//                sessionAux.setUsageCount(0);
//                sessionAux.setWalletAccount(defaultWallet.getValue().address);
//                sessionAux.setChainId(sessionChainId);
//            });
//        }

        gasService.startGasPriceCycle(sessionChainId);
    }

    public void updateSession(String sessionId, long sessionChainId)
    {
//        Timber.tag(TAG).d("updateSession: sessionId: %s, updated chainId: %s", sessionId, sessionChainId);
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmWCSession sessionData = realm.where(RealmWCSession.class)
//                    .equalTo("sessionId", sessionId)
//                    .findFirst();
//            realm.beginTransaction();
//            if (sessionData != null)
//            {
//                sessionData.setChainId(sessionChainId);
//            }
//            else
//            {
//                Timber.tag("TAG").d("updateSession: could not fin session!");
//            }
//            realm.commitTransaction();
//        }
//        catch (Exception e)
//        {
//            Timber.e(e);
//        }

        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
        if(data != null) {
            data.setChainId((int)sessionChainId);
            wc1SessionStorage.updateWalletConnectSession(data);
        }

    }

    public void deleteSession(String sessionId)
    {
        Timber.d("deleteSession: %s", sessionId);
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                RealmWCSession sessionAux = r.where(RealmWCSession.class)
//                        .equalTo("sessionId", sessionId)
//                        .findFirst();
//
//                if (sessionAux != null)
//                {
//                    sessionAux.deleteFromRealm();
//                }
//            });
//        }
        wc1SessionStorage.deleteSessionsByPeerId(sessionId);
    }

    public void recordSign(Signable signable, String sessionId)
    {
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                RealmWCSignElement signMessage = r.createObject(RealmWCSignElement.class);
//
//                String signType = "Message";
//                if (signable instanceof EthereumTypedMessage) signType = "TypedMessage";
//
//                signMessage.setSessionId(sessionId);
//                signMessage.setSignType(signType);
//                signMessage.setSignTime(System.currentTimeMillis());
//                signMessage.setSignMessage(signable.getUserMessage());
//            }, onSuccess);
//        }

        String signType = "Message";
        if (signable instanceof EthereumTypedMessage) signType = "TypedMessage";
        WCSignElement data = new WCSignElement(sessionId, signable.getUserMessage().toString().getBytes(StandardCharsets.UTF_8), new Date(), signType);
        wcSignElementStorage.save(data);
    }

    public void recordSignTransaction(Context ctx, Web3Transaction tx, String chainIdStr, String sessionId)
    {
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                long chainId = chainIdStr != null ? Long.parseLong(chainIdStr) : MAINNET_ID;
//                RealmWCSignElement signMessage = r.createObject(RealmWCSignElement.class);
//                String signType = "Transaction";
//                signMessage.setSessionId(sessionId);
//                signMessage.setSignType(signType);
//                signMessage.setSignTime(System.currentTimeMillis());
//                signMessage.setSignMessage(tx.getFormattedTransaction(ctx, chainId, getNetworkSymbol(chainId)));
//            });
//        }
        String signType = "Transaction";
        long chainId = chainIdStr != null ? Long.parseLong(chainIdStr) : MAINNET_ID;
        WCSignElement data = new WCSignElement(sessionId, tx.getFormattedTransaction(ctx, chainId, getNetworkSymbol(chainId)).toString().getBytes(StandardCharsets.UTF_8), new Date(), signType);
        wcSignElementStorage.save(data);
    }

    public ArrayList<SignRecord> getSignRecords(String sessionId)
    {
        ArrayList<SignRecord> records = new ArrayList<>();
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmResults<RealmWCSignElement> sessionList = realm.where(RealmWCSignElement.class)
//                    .equalTo("sessionId", sessionId)
//                    .findAll();
//
//            for (RealmWCSignElement s : sessionList)
//            {
//                records.add(new SignRecord(s));
//            }
//        }

        List<WCSignElement> wcSignElements = wcSignElementStorage.getByRemotePeerId(sessionId);
        if(wcSignElements != null && !wcSignElements.isEmpty()){
            for(WCSignElement wc : wcSignElements){
                records.add(new SignRecord(wc));
            }
        }

        return records;
    }

    public void getAllSession(String accountId){
        List<WalletConnectSession> data = wc1SessionStorage.getSessions(accountId);
        Timber.d("1995 getAllSession size " + data.size());
    }

    public List<WalletConnectSessionItem> getSessions(String accountId)
    {
        List<WalletConnectSessionItem> sessions = new ArrayList<>();
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            RealmResults<RealmWCSession> items = realm.where(RealmWCSession.class)
//                    .sort("lastUsageTime", Sort.DESCENDING)
//                    .findAll();
//
//            for (RealmWCSession r : items)
//            {
//                sessions.add(new WalletConnectSessionItem(r));
//            }
//        }

        List<WalletConnectSession> data = wc1SessionStorage.getSessions(accountId);

        if(data != null && !data.isEmpty()){
            for(WalletConnectSession s : data){
                sessions.add(new WalletConnectSessionItem(s));
            }
        }

        return sessions;
    }

    public void removePendingRequest(Activity activity, long id)
    {
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                walletConnectService.removePendingRequest(id);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                //walletConnectService = null;
                Timber.tag(TAG).d("Service disconnected");
            }
        };

        WCUtils.startServiceLocal(activity, connection, WalletConnectActions.CONNECT);
    }

    public void getClient(Activity activity, String sessionId, GetClientCallback clientCb)
    {
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                clientCb.getClient(walletConnectService.getClient(sessionId));
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                Timber.tag(TAG).d("Service disconnected");
            }
        };

        WCUtils.startServiceLocal(activity, connection, WalletConnectActions.CONNECT);
    }

    public void putClient(Activity activity, String sessionId, WCClient client)
    {
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                walletConnectService.putClient(sessionId, client);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                Timber.tag(TAG).d("Service disconnected");
            }
        };

        WCUtils.startServiceLocal(activity, connection, WalletConnectActions.CONNECT);
    }

    public void disconnectSession(Activity activity, String sessionId)
    {
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                walletConnectService.terminateClient(sessionId);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                Timber.tag(TAG).d("Service disconnected");
            }
        };

        WCUtils.startServiceLocal(activity, connection, WalletConnectActions.CONNECT);
    }

    public void rejectRequest(Context ctx, String sessionId, long id, String message)
    {
        Intent bIntent = new Intent(ctx, WalletConnectService.class);
        bIntent.setAction(String.valueOf(WalletConnectActions.REJECT.ordinal()));
        bIntent.putExtra("sessionId", sessionId);
        bIntent.putExtra("id", id);
        bIntent.putExtra("message", message);
        ctx.startService(bIntent);
    }

    public void approveRequest(Context ctx, String sessionId, long id, String message)
    {
        Intent bIntent = new Intent(ctx, WalletConnectService.class);
        bIntent.setAction(String.valueOf(WalletConnectActions.APPROVE.ordinal()));
        bIntent.putExtra("sessionId", sessionId);
        bIntent.putExtra("id", id);
        bIntent.putExtra("message", message);
        ctx.startService(bIntent);
    }

    public String getNetworkSymbol(long chainId)
    {
        NetworkInfo info = findDefaultNetworkInteract.getNetworkInfo(chainId);
        if (info == null)
        {
            info = findDefaultNetworkInteract.getNetworkInfo(MAINNET_ID);
        }
        return info.symbol;
    }

    public void actionSheetConfirm(String mode)
    {
        AnalyticsProperties analyticsProperties = new AnalyticsProperties();
        analyticsProperties.setData("(WC)" + mode); //disambiguate signs/sends etc through WC

        analyticsService.track(C.AN_CALL_ACTIONSHEET, analyticsProperties);
    }

    public void prepareIfRequired()
    {
        if (prepareDisposable == null)
        {
            prepare();
        }
    }

    public void approveSwitchEthChain(Context context, long requestId, String sessionId, long chainId, boolean approve, boolean chainAvailable)
    {
        Intent i = new Intent(context, WalletConnectService.class);
        i.setAction(String.valueOf(WalletConnectActions.SWITCH_CHAIN.ordinal()));
        i.putExtra(C.EXTRA_WC_REQUEST_ID, requestId);
        i.putExtra(C.EXTRA_SESSION_ID, sessionId);
        i.putExtra(C.EXTRA_CHAIN_ID, chainId);
        i.putExtra(C.EXTRA_APPROVED, approve);
        i.putExtra(C.EXTRA_CHAIN_AVAILABLE, chainAvailable);
        context.startService(i);
    }

    public void approveAddEthereumChain(Context context,
                                        long requestId,
                                        String sessionId,
                                        WalletAddEthereumChainObject chainObject,
                                        boolean approved)
    {
        Intent i = new Intent(context, WalletConnectService.class);
        i.setAction(String.valueOf(WalletConnectActions.ADD_CHAIN.ordinal()));
        i.putExtra(C.EXTRA_WC_REQUEST_ID, requestId);
        i.putExtra(C.EXTRA_SESSION_ID, sessionId);
        i.putExtra(C.EXTRA_CHAIN_OBJ, chainObject);
        i.putExtra(C.EXTRA_APPROVED, approved);

        if (approved)
        {
            // add only if not present
            if (!isChainAdded(chainObject.getChainId()))
            {
                ethereumNetworkRepository.saveCustomRPCNetwork(chainObject.chainName, extractRpc(chainObject), chainObject.getChainId(),
                        chainObject.nativeCurrency.symbol, "", "", false, -1L);
            }
        }
        context.startService(i);
    }

    private String extractRpc(WalletAddEthereumChainObject chainObject)
    {
        for (String thisRpc : chainObject.rpcUrls)
        {
            if (thisRpc.toLowerCase().startsWith("http"))
            {
                return thisRpc;
            }
        }

        return "";
    }

    public boolean isChainAdded(long chainId)
    {
        return ethereumNetworkRepository.getNetworkByChain(chainId) != null;
    }

    public TokensService getTokenService()
    {
        return tokensService;
    }

    public void endSession(String sessionId)
    {
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                RealmWCSession sessionAux = r.where(RealmWCSession.class)
//                        .equalTo("sessionId", sessionId)
//                        .findFirst();
//
//                if (sessionAux != null)
//                {
//                    sessionAux.setPeerId("");
//                    r.insertOrUpdate(sessionAux);
//                }
//            });
//        }


        WalletConnectSession data = wc1SessionStorage.getBySessionId(sessionId);
        if(data != null) {
            data.setPeerId("");
            wc1SessionStorage.updateWalletConnectSession(data);
        }
    }

    // remove wallet connect sessions with no transaction history
    public void removeEmptySessions(Context context, String accountId, Runnable onComplete)
    {
        // create list of sessions which are inactive and has zero txn/sign
        // delete them from realm
        getInactiveSessionIds(context, accountId, sessions -> {
            ArrayList<String> sessionIdsToRemove = new ArrayList<>();
            for (String sessionId : sessions)
            {
                // if no txn/sign history found
                if (getSignRecords(sessionId).isEmpty())
                {
                    // add this sessionId to the list of removable
                    sessionIdsToRemove.add(sessionId);
                }
            }
            deleteSessionsFromRealm(sessionIdsToRemove, onComplete);
        });
    }

    // remove all inactive wallet connect sessions
    public void removeAllSessions(Context context, String accountId, Runnable onSuccess)
    {
        getInactiveSessionIds(context, accountId, list -> {
            Timber.d("removeAllSessions: sessionsToRemove: %d", list.size());
            deleteSessionsFromRealm(list, onSuccess);
        });
    }

    // connects to service to check session state and gives inactive sessions
    private void getInactiveSessionIds(Context context, String accountId, GenericCallback<List<String>> callback)
    {
        List<WalletConnectSessionItem> sessionItems = getSessions(accountId);        // all sessions in DB
        ArrayList<String> inactiveSessions = new ArrayList<>();
        ServiceConnection connection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                WalletConnectService walletConnectService = ((WalletConnectService.LocalBinder) service).getService();
                // loop & populate sessions which are inactive
                for (WalletConnectSessionItem item : sessionItems)
                {
                    WCClient wcClient = walletConnectService.getClient(item.sessionId);
                    // if client is not connected ie: session inactive
                    if (wcClient == null || !wcClient.isConnected())
                    {
                        inactiveSessions.add(item.sessionId);
                    }
                }
                callback.call(inactiveSessions);        // return inactive sessions to caller
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                //walletConnectService = null;
                Timber.tag(TAG).d("Service disconnected");
            }
        };
        Intent i = new Intent(context, WalletConnectService.class);     // not specifying action as no need. we just need to bind to service
        context.startService(i);
        context.bindService(i, connection, Service.BIND_ABOVE_CLIENT);
    }

    // deletes the RealmWCSession objects with the given sessionIds present in the list
    private void deleteSessionsFromRealm(List<String> sessionIds, Runnable onSuccess)
    {
        Timber.d("deleteSessionsFromRealm: sessions: %s", sessionIds);
        if (sessionIds.isEmpty())
            return;
//        try (Realm realm = realmManager.getRealmInstance(WC_SESSION_DB))
//        {
//            realm.executeTransactionAsync(r -> {
//                boolean isDeleted = r.where(RealmWCSession.class)
//                        .in("sessionId", sessionIds.toArray(new String[]{}))
//                        .findAll()
//                        .deleteAllFromRealm();
//                Timber.d("deleteSessions: Success: %s\nList: %s", isDeleted, sessionIds);
//            }, onSuccess::run);
//        }
//        catch (Exception e)
//        {
//            Timber.e(e);
//        }
        for(String id: sessionIds){
            wc1SessionStorage.deleteSessionsByPeerId(id);
        }
    }
}
