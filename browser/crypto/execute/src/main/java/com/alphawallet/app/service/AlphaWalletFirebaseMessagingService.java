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

public class AlphaWalletFirebaseMessagingService
{

}
/*public class AlphaWalletFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = AlphaWalletFirebaseMessagingService.class.getSimpleName();
    public static final String IDENTITY_POOL_ID = "us-west-2:6d3c1431-0764-43f0-8ced-54e584fd01ad";
    public static final String PLATFORM_APPLICATION_ARN = "arn:aws:sns:us-west-2:400248756644:app/GCM/AlphaWallet-Android";

    @Override
    public void onNewToken(String token)
    {
        try
        {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    IDENTITY_POOL_ID,
                    Regions.US_WEST_2
            );
            AmazonSNSClient snsClient = new AmazonSNSClient(credentialsProvider);
            snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
            CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();
            request.setPlatformApplicationArn(PLATFORM_APPLICATION_ARN);
            request.setToken(token);
            CreatePlatformEndpointResult result = snsClient.createPlatformEndpoint(request);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        super.onNewToken(token);
    }
}*/
