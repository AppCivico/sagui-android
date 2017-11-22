package com.eokoe.sagui.features.asset

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.toAuthority

class OpenMediaClickListener(
        private val context: Context,
        private val asset: Asset
) : View.OnClickListener {
    override fun onClick(view: View) {
        val uri: Uri?
        val intent = Intent(Intent.ACTION_VIEW)
        if (asset.isLocal) {
            uri = asset.uri.toAuthority(context)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = asset.uri
        }
        intent.setDataAndType(uri, asset.type ?: context.contentResolver.getType(uri))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Falha ao tentar reproduzir a mídia", Toast.LENGTH_SHORT).show()
        }
    }
}