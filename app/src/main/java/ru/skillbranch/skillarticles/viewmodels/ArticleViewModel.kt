package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class ArticleViewModel(private val articleId: String) :
    BaseViewModel<ArticleState>(ArticleState()) {
    private val repository = ArticleRepository

    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            with(article) {
                state.copy(
                    shareLink = shareLink,
                    title = title,
                    category = category,
                    categoryIcon = categoryIcon,
                    date = date.format(),
                    author = author,
                    poster = poster,
                    content = content
                )
            }
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            with(info) {
                state.copy(
                    isBookmark = isBookmark,
                    isLike = isLike
                )
            }
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            with(settings) {
                state.copy(
                    isDarkMode = isDarkMode,
                    isBigText = isBigText
                )
            }
        }
    }

    private fun getArticleContent(): LiveData<List<Any>?> =
        repository.loadArticleContent(articleId)

    private fun getArticleData(): LiveData<ArticleData?> =
        repository.getArticle(articleId)

    private fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> =
        repository.loadArticlePersonalInfo(articleId)

    fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    fun handleNightMode() {
        repository.updateSettings(
            currentState.toAppSettings().copy(isDarkMode = !currentState.isDarkMode)
        )
    }

    fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }
        toggleLike()
        val msg = if (currentState.isLike) {
            Notify.TextMessage("Mark is liked")
        } else {
            Notify.ActionMessage(
                "Don`t like it anymore",
                "No, still like it",
                toggleLike
            )
        }
        notify(msg)
    }

    fun handleBookmark() {
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }
        toggleBookmark()
        val msg = if (currentState.isBookmark) {
            Notify.TextMessage("Add to bookmarks")
        } else {
            Notify.ActionMessage(
                "Remove from bookmarks",
                "No, still bookmark it",
                toggleBookmark
            )
        }
        notify(msg)
    }

    fun handleShare() {
        notify(Notify.ErrorMessage("Share is not implemented", "OK", null))
    }

    fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState {
            it.copy(
                isSearch = isSearch,
                searchQuery = if (isSearch) it.searchQuery else null,
                isShowMenu = false,
                searchPosition = 0
            )
        }
    }

    fun handleSearch(query: String?) {
        query ?: return
        val result = (currentState.content.firstOrNull() as? String).indexesOf(query)
            .map { it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result) }
    }

    fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }
}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
    val isDarkMode: Boolean = false,
    val isBigText: Boolean = false,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int, Int>> = emptyList(),
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()
) : IViewModelState {

    override fun save(outState: Bundle) {
        outState.putAll(
            bundleOf(
                "isSearch" to isSearch,
                "searchQuery" to searchQuery,
                "searchResults" to searchResults,
                "searchPosition" to searchPosition
            )
        )
    }

    override fun restore(savedState: Bundle): ArticleState {
        @Suppress("UNCHECKED_CAST")
        return copy(
            isSearch = savedState["isSearch"] as Boolean,
            searchQuery = savedState["searchQuery"] as? String,
            searchResults = savedState["searchResults"] as List<Pair<Int, Int>>,
            searchPosition = savedState["searchPosition"] as Int
        )
    }

}