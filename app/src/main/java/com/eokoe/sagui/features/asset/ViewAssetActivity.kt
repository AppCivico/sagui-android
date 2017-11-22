package com.eokoe.sagui.features.asset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_view_asset.*

/**
 * @author Pedro Silva
 */
class ViewAssetActivity : BaseActivity() {

    private lateinit var assets: List<Asset>
    private var showSendButton: Boolean = false
    private var currentPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_asset)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        assets = intent.extras.getParcelableArrayList(EXTRA_ASSETS)
        showSendButton = intent.extras.getBoolean(EXTRA_SHOW_SEND_BUTTON)
        if (currentPosition == -1) {
            currentPosition = intent.extras.getInt(EXTRA_CURRENT_POSITION)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (showSendButton) {
            fabSend.show()
        }
        fabSend.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        viewPager.adapter = PagerAdapter(getFragments())
        viewPager.currentItem = currentPosition
    }

    private fun getFragments(): List<Fragment> {
        val fragments = ArrayList<Fragment>()
        assets.mapTo(fragments) { ViewAssetFragment.newInstance(it) }
        return fragments
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_POSITION, currentPosition)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        currentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION)
    }

    companion object {
        private val EXTRA_ASSETS = "EXTRA_ASSETS"
        private val EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION"
        private val EXTRA_SHOW_SEND_BUTTON = "EXTRA_SHOW_SEND_BUTTON"

        private val STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION"

        fun getIntent(context: Context, assets: List<Asset>, currentPosition: Int = 0,
                      showSendButton: Boolean = false): Intent =
                Intent(context, ViewAssetActivity::class.java)
                        .putExtra(EXTRA_ASSETS, ArrayList<Asset>(assets))
                        .putExtra(EXTRA_SHOW_SEND_BUTTON, showSendButton)
                        .putExtra(EXTRA_CURRENT_POSITION, currentPosition)

        /*fun getIntent(context: Context, asset: Asset, showSendButton: Boolean = false): Intent {
            val assets = ArrayList<Asset>()
            assets.add(asset)
            return getIntent(context, assets, 0, showSendButton)
        }*/
    }

    inner class PagerAdapter(private val fragments: List<Fragment>) :
            FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int) = fragments[position]

        override fun getCount() = fragments.size
    }
}