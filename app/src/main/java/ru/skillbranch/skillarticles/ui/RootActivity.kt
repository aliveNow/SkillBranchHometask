package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_bottombar.view.*
import kotlinx.android.synthetic.main.layout_submenu.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private lateinit var viewModel: ArticleViewModel

    private val vb: ActivityRootBinding
        get() = checkNotNull(_vb)

    private var _vb: ActivityRootBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _vb = ActivityRootBinding.inflate(LayoutInflater.from(this))
        setContentView(vb.root)
        setupToolbar()
        setupSubmenu()
        setupBottombar()

        val vmFactory = ViewModelFactory("0")
        viewModel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this) {
            renderUi(it)
        }
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }

    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(vb.bottombar)

        snackbar.apply {
            when (notify) {
                is Notify.ActionMessage -> {
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(notify.actionLabel) {
                        notify.actionHandler.invoke()
                    }
                }
                is Notify.ErrorMessage -> {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }.show()
    }

    private fun setupBottombar() {
        with(vb.bottombar) {
            btn_like.setOnClickListener { viewModel.handleLike() }
            btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
            btn_share.setOnClickListener { viewModel.handleShare() }
            btn_settings.setOnClickListener { viewModel.handleToggleMenu() }
        }
    }

    private fun setupSubmenu() {
        with(vb.submenu) {
            btn_text_up.setOnClickListener { viewModel.handleUpText() }
            btn_text_down.setOnClickListener { viewModel.handleDownText() }
            switch_mode.setOnClickListener { viewModel.handleNightMode() }
        }
    }

    private fun renderUi(data: ArticleState) {
        with(vb.bottombar) {
            btn_settings.isChecked = data.isShowMenu
            btn_like.isChecked = data.isLike
            btn_bookmark.isChecked = data.isBookmark
        }

        with(vb.submenu) {
            if (data.isShowMenu) open() else close()
            switch_mode.isChecked = data.isDarkMode
            delegate.localNightMode = if (data.isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            if (data.isBigText) {
                vb.tvTextContent.textSize = 18f
                btn_text_up.isChecked = true
                btn_text_down.isChecked = false
            } else {
                vb.tvTextContent.textSize = 14f
                btn_text_up.isChecked = false
                btn_text_down.isChecked = true
            }
        }

        vb.tvTextContent.text =
            if (data.isLoadingContent) "loading" else data.content.first() as String

        vb.toolbar.apply {
            title = data.title ?: "loading"
            subtitle = data.category ?: "loading"
            if (data.categoryIcon != null) logo = getDrawable(data.categoryIcon as Int)
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(vb.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (vb.toolbar.childCount > 2) vb.toolbar.getChildAt(2) as? ImageView else null
        logo?.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            logo.layoutParams = (logo.layoutParams as Toolbar.LayoutParams).apply {
                width = dpToIntPx(40)
                height = dpToIntPx(40)
                marginEnd = dpToIntPx(16)
            }
        }

    }

    override fun onDestroy() {
        _vb = null
        super.onDestroy()
    }
}