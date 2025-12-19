/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.schoolonair.wallet.component.resources

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContextCompat

object ImageResourcesHelper {

    fun dp(dp: Float, context: Context?) = context?.let {
        val density = context.resources.displayMetrics.density
        if (dp == 0f) 0 else Math.ceil((density * dp).toDouble()).toInt()
    } ?: dp.toInt()

    fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    fun dpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun getAttr(attr: Int, theme: Resources.Theme): Int? {
        val typedValue = TypedValue()
        return if (theme.resolveAttribute(attr, typedValue, true))
            typedValue.data
        else
            null
    }

    fun getAttr(attr: Int, theme: Resources.Theme, default: Int): Int {
        return getAttr(attr, theme) ?: default
    }

    fun d(id: Int, context: Context): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    fun getCurrencyDrawableResource(context: Context, currencyCode: String): Int {
        Log.d("TAG", "105 cc $currencyCode  " + context.packageName)
        return context.resources.getIdentifier("currency_$currencyCode", "drawable", context.packageName)
    }
}
