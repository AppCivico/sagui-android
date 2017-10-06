package com.eokoe.sagui.features.show_asset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_view_asset.*

/**
 * @author Pedro Silva
 */
class ShowAssetActivity : BaseActivity() {

    lateinit var assets: List<Asset>
    var showSendButton: Boolean = false
    var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_asset)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        assets = intent.extras.getParcelableArrayList(EXTRA_ASSETS)
        showSendButton = intent.extras.getBoolean(EXTRA_SHOW_SEND_BUTTON)
        currentPosition = intent.extras.getInt(EXTRA_CURRENT_POSITION)
    }

    override fun init(savedInstanceState: Bundle?) {
        if (showSendButton) {
            fabSend.show()
        }
        val asset = assets[currentPosition]
        val path = if (!asset.isLocal) {
            asset.remotePath
        } else {
            "file://" + asset.uri.getRealPath(this)
        }
        ivImage.setImageURI(path)
        fabSend.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        private val EXTRA_ASSETS = "EXTRA_ASSETS"
        private val EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION"
        private val EXTRA_SHOW_SEND_BUTTON = "EXTRA_SHOW_SEND_BUTTON"

        fun getIntent(context: Context, assets: List<Asset>, currentPosition: Int = 0, showSendButton: Boolean = false): Intent =
                Intent(context, ShowAssetActivity::class.java)
                        .putExtra(EXTRA_ASSETS, ArrayList<Asset>(assets))
                        .putExtra(EXTRA_SHOW_SEND_BUTTON, showSendButton)
                        .putExtra(EXTRA_CURRENT_POSITION, currentPosition)

        fun getIntent(context: Context, asset: Asset, showSendButton: Boolean = false): Intent {
            val assets = ArrayList<Asset>()
            assets.add(asset)
            return getIntent(context, assets, 0, showSendButton)
        }

        fun getIntent(context: Context, path: String, isLocal: Boolean, showSendButton: Boolean = false): Intent {
            val assets = ArrayList<Asset>()
            val asset = if (isLocal) {
                Asset(localPath = path)
            } else {
                Asset(remotePath = path)
            }
            assets.add(asset)
            return getIntent(context, assets, 0, showSendButton)
        }
    }
}