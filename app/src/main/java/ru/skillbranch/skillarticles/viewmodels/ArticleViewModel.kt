package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

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
                searchQuery = if (isSearch) it.searchQuery else null
            )
        }
    }

    fun handleSearch(query: String?) {
        updateState { it.copy(searchQuery = query) }
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
)