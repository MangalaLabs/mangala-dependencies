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



package com.mangala.app.bookmarks.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mangala.app.bookmarks.model.SavedSite
import com.mangala.mobile.android.R as CommonR
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.baseHost
import com.mangala.mobile.android.ui.menu.PopupMenu
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ViewSavedSiteEmptyHintBinding
import com.schoolonair.wallet.browser.app.databinding.ViewSavedSiteEntryBinding
import kotlinx.coroutines.launch

class BookmarksAdapter(
    private val layoutInflater: LayoutInflater,
    private val viewModel: BookmarksViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val faviconManager: FaviconManager
) : ListAdapter<BookmarksAdapter.BookmarksItemTypes, BookmarkScreenViewHolders>(
    BookmarksDiffCallback()
) {

    companion object {
        const val EMPTY_STATE_TYPE = 0
        const val BOOKMARK_TYPE = 1
    }

    interface BookmarksItemTypes
    object EmptyHint : BookmarksItemTypes
    data class BookmarkItem(val bookmark: SavedSite.Bookmark) : BookmarksItemTypes

    fun setItems(
        bookmarkItems: List<BookmarkItem>,
        showEmptyHint: Boolean
    ) {
        val generatedList = generateNewList(bookmarkItems, showEmptyHint)
        submitList(generatedList)
    }

    private fun generateNewList(
        value: List<BookmarksItemTypes>,
        showEmptyHint: Boolean
    ): List<BookmarksItemTypes> {
        if (!showEmptyHint) {
            return value
        }
        return if (value.isEmpty()) listOf(EmptyHint) else value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkScreenViewHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            BOOKMARK_TYPE -> {
                val binding = ViewSavedSiteEntryBinding.inflate(inflater, parent, false)
                return BookmarkScreenViewHolders.BookmarksViewHolder(
                    layoutInflater,
                    binding,
                    viewModel,
                    lifecycleOwner,
                    faviconManager
                )
            }
            EMPTY_STATE_TYPE -> {
                val binding = ViewSavedSiteEmptyHintBinding.inflate(inflater, parent, false)
                BookmarkScreenViewHolders.EmptyHint(binding)
            }
            else -> throw IllegalArgumentException("viewType not found")
        }
    }

    override fun onBindViewHolder(
        holder: BookmarkScreenViewHolders,
        position: Int
    ) {
        when (holder) {
            is BookmarkScreenViewHolders.BookmarksViewHolder -> {
                holder.update((getItem(position) as BookmarkItem).bookmark)
            }
            is BookmarkScreenViewHolders.EmptyHint -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EmptyHint -> EMPTY_STATE_TYPE
            else -> BOOKMARK_TYPE
        }
    }
}

sealed class BookmarkScreenViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class EmptyHint(private val binding: ViewSavedSiteEmptyHintBinding) : BookmarkScreenViewHolders(binding.root) {
        fun bind() {
            binding.savedSiteEmptyHint.setText(com.schoolonair.wallet.component.resources.R.string.bookmarksEmptyHint)
        }
    }

    class BookmarksViewHolder(
        private val layoutInflater: LayoutInflater,
        private val binding: ViewSavedSiteEntryBinding,
        private val viewModel: BookmarksViewModel,
        private val lifecycleOwner: LifecycleOwner,
        private val faviconManager: FaviconManager
    ) : BookmarkScreenViewHolders(binding.root) {

        private val context: Context = binding.root.context

        fun update(bookmark: SavedSite.Bookmark) {
            val twoListItem = binding.root

            twoListItem.setContentDescription(
                context.getString(
                    com.schoolonair.wallet.component.resources.R.string.bookmarkOverflowContentDescription,
                    bookmark.title
                )
            )
            twoListItem.setTitle(bookmark.title)
            twoListItem.setSubtitle(parseDisplayUrl(bookmark.url))
            loadFavicon(bookmark.url)

            twoListItem.setOverflowClickListener { anchor ->
                showOverFlowMenu(anchor, bookmark)
            }

            twoListItem.setClickListener {
                viewModel.onSelected(bookmark)
            }
        }

        private fun loadFavicon(url: String) {
            lifecycleOwner.lifecycleScope.launch {
                faviconManager.loadToViewFromLocalOrFallback(url = url, view = itemView.findViewById(CommonR.id.image))
            }
        }

        private fun parseDisplayUrl(urlString: String): String {
            val uri = Uri.parse(urlString)
            return uri.baseHost ?: return urlString
        }

        private fun showOverFlowMenu(
            anchor: View,
            bookmark: SavedSite.Bookmark
        ) {
            val popupMenu = PopupMenu(layoutInflater, R.layout.popup_window_edit_delete_menu)
            val view = popupMenu.contentView
            popupMenu.apply {
                onMenuItemClicked(view.findViewById(R.id.edit)) { editBookmark(bookmark) }
                onMenuItemClicked(view.findViewById(R.id.delete)) { deleteBookmark(bookmark) }
            }
            popupMenu.show(binding.root, anchor)
        }

        private fun editBookmark(bookmark: SavedSite.Bookmark) {
            viewModel.onEditSavedSiteRequested(bookmark)
        }

        private fun deleteBookmark(bookmark: SavedSite.Bookmark) {
            viewModel.onDeleteSavedSiteRequested(bookmark)
        }
    }
}
