package com.eokoe.sagui.features.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.view.View
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.help.faq.FaqActivity
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import kotlinx.android.synthetic.main.activity_help.*
import org.koin.android.ext.android.inject

/**
 * @author Pedro Silva
 * @since 12/10/17
 */
class HelpActivity : BaseActivity() {

    private val helpAdapter by inject<HelpAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        rvHelp.setHasFixedSize(true)
        helpAdapter.onItemClickListener = this::onItemClick
        rvHelp.adapter = helpAdapter
    }

    fun onItemClick(itemType: HelpAdapter.ItemType) {
        when (itemType) {
            HelpAdapter.ItemType.FAQ -> {
                val intent = FaqActivity.getIntent(this)
                startActivity(intent)
            }
            HelpAdapter.ItemType.CONTACT -> openContactDialog()
            HelpAdapter.ItemType.ABOUT -> openAboutDialog()
        }
    }

    private fun openContactDialog() {
        val mail = "contato@appcivico.com"
        val text = SpannableString("Dúvidas, sugestões ou reclamações sobre o funcionamento " +
                "do aplicativo, entrar em contato com: $mail")

        val urlSpan = object : URLSpan("mailto:$mail?subject=" +
                Uri.encode("Contato - Projeto Sagui")) {

            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse(url))
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
        text.setSpan(urlSpan, text.indexOf(mail), text.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        AlertDialogFragment
                .create(this) {
                    title = "Contato"
                    message = text
                    cancelable = true
                    hasLink = true
                }
                .show(supportFragmentManager)
    }

    private fun openAboutDialog() {
        val url = "http://appcivico.com"
        val text = SpannableString("Enquetes desenvolvidas pelo Centro de Pesquisa sobre " +
                "Direitos Humanos e Empresas da Fundação Getulio Vargas - FGV.\n\n" +
                "Aplicativo desenvolvido pela AppCívico. $url")

        val urlSpan = object : URLSpan(url) {
            override fun onClick(view: View) {
                startActivity(Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(url)))
            }
        }

        text.setSpan(urlSpan, text.indexOf(url), text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        AlertDialogFragment
                .create(this) {
                    title = "Sobre"
                    message = text
                    cancelable = true
                    hasLink = true
                }
                .show(supportFragmentManager)
    }

    override fun saveInstanceState(outState: Bundle) {}

    override fun restoreInstanceState(savedInstanceState: Bundle) {}

    companion object {
        val TAG = HelpActivity::class.simpleName!!

        fun getIntent(context: Context) = Intent(context, HelpActivity::class.java)
    }
}