package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx

class RootActivity : AppCompatActivity() {

    private val vb: ActivityRootBinding
        get() = checkNotNull(_vb)

    private var _vb: ActivityRootBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _vb = ActivityRootBinding.inflate(LayoutInflater.from(this))
        setContentView(vb.root)

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