package com.eokoe.sagui.features.asset

import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_view_asset.*

/**
 * @author Pedro Silva
 * @since 21/11/17
 */
class ViewAssetFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_view_asset, container, false)

    override fun init(view: View?, savedInstanceState: Bundle?) {
        val asset = arguments!!.getParcelable<Asset>(EXTRA_ASSET)
        when {
            asset.isImage -> {
                ivImage.setImageURI(asset.uri.toString())
            }
            asset.isVideo -> {
                if (asset.isLocal) {
                    val videoThumbnail = ThumbnailUtils.createVideoThumbnail(asset.uri.toString(),
                            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)
                    ivImage.setImageBitmap(videoThumbnail)
                } else {
                    ivImage.setImageURI(asset.thumbnail)
                }
                ivPlay.setOnClickListener(OpenMediaClickListener(context!!, asset))
                ivPlay.show()
            }
            else -> {
                ivAudio.setOnClickListener(OpenMediaClickListener(context!!, asset))
                ivAudio.show()
            }
        }
    }

    override fun saveInstanceState(outState: Bundle) {}

    override fun restoreInstanceState(savedInstanceState: Bundle?) {}

    companion object {
        private val EXTRA_ASSET = "EXTRA_ASSET"

        fun newInstance(asset: Asset): ViewAssetFragment {
            val fragment = ViewAssetFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_ASSET, asset)
            fragment.arguments = args
            return fragment
        }
    }
}