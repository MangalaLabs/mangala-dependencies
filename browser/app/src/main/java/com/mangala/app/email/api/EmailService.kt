/*
 * Copyright (c) DuckDuckGo, Inc.
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/duckduckgo/Android
 */



package com.mangala.app.email.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface EmailService {
    @POST("")
    suspend fun newAlias(@Header("Authorization") authorization: String): EmailAlias

    @POST("")
    suspend fun joinWaitlist(): WaitlistResponse

    @GET("")
    suspend fun waitlistStatus(): WaitlistStatusResponse

    @FormUrlEncoded
    @POST("")
    suspend fun getCode(@Field("token") token: String): EmailInviteCodeResponse
}

data class EmailAlias(val address: String)
data class WaitlistResponse(
    val token: String?,
    val timestamp: Int?
)

data class WaitlistStatusResponse(val timestamp: Int)
data class EmailInviteCodeResponse(val code: String)
