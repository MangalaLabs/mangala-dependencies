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



package com.mangala.mobile.android.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.mangala.mobile.android.R

class IconButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.Widget_Mangala_IconButton
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    init {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.IconButton,
                0,
                R.style.Widget_Mangala_IconButton
            )

        val resourceId = typedArray.getResourceId(
            R.styleable.IconButton_srcCompat,
            com.schoolonair.wallet.component.resources.R.drawable.ic_union
        )

        setImageResource(resourceId)
        setBackgroundDrawable(typedArray.getDrawable(R.styleable.IconButton_android_background))

        typedArray.recycle()
    }
}
