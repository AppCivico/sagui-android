package com.eokoe.sagui.widgets.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.widget.ImageButton
import com.eokoe.sagui.R
import com.eokoe.sagui.extensions.fromHtml
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import kotlinx.android.synthetic.main.dialog_audiorecorder.view.*

/**
 * @author Pedro Silva
 * @since 11/10/17
 */
class AudioRecorderDialog private constructor() : DialogFragment() {
    val TAG = AudioRecorderDialog::class.simpleName

    private var startedAt = 0L
    private var maxDuration = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        maxDuration = arguments.getLong(EXTRA_MAX_DURATION)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_audiorecorder, null) as ViewGroup

        val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle(getString(R.string.record_audio))

        dialogView.tvChronometer.text = getFormattedTime(0, maxDuration)

        dialogView.btnOk.setOnClickListener {
            startRecord(dialogView)
        }
        dialogView.chronometer.setOnChronometerTickListener {
            dialogView.tvChronometer.text = getFormattedTime(elapsedTime(), maxDuration)
            if (elapsedTime() >= maxDuration) {
                stopRecord(dialogView)
            }
        }
        dialogView.btnCancel.setOnClickListener {
            dismiss()
        }
        return dialog.create()
    }

    private fun startRecord(view: ViewGroup) {
        startedAt = SystemClock.elapsedRealtime()
        view.chronometer.base = startedAt
        view.chronometer.start()
        view.btnCancel.hide()
        view.hDivider.hide()
        view.btnOk.setText(R.string.stop)
        view.btnOk.setOnClickListener {
            stopRecord(view)
        }
    }

    private fun stopRecord(view: ViewGroup) {
        view.chronometer.stop()
        view.btnOk.setText(R.string.send)
        view.btnCancel.show()
        view.hDivider.show()
        playButton(view.ibAudioAction)
        view.btnOk.setOnClickListener {
            sendAudio()
        }
    }

    private fun stopButton(view: ImageButton) {
        view.setImageResource(R.drawable.ic_stop)
        view.setOnClickListener {
            playButton(view)
        }
    }

    private fun playButton(view: ImageButton) {
        view.setImageResource(R.drawable.ic_play_arrow)
        view.setOnClickListener {
            stopButton(view)
        }
    }

    private fun sendAudio() {
        dismiss()
    }

    private fun getFormattedTime(time: Long, maxDuration: Long) =
            String.format("<big><big>%s</big></big> / <small>%s</small>",
                    formatTime(time), formatTime(maxDuration)).fromHtml()

    private fun elapsedTime() = (SystemClock.elapsedRealtime() - startedAt) / 1000

    private fun formatTime(time: Long) = String.format("%02d:%02d", time / 60, time % 60)

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    companion object {
        private val DEFAULT_MAX_DURATION = 600L
        private val EXTRA_MAX_DURATION = "EXTRA_MAX_DURATION"

        fun newInstance(maxDuration: Long = DEFAULT_MAX_DURATION): AudioRecorderDialog {
            val dialog = AudioRecorderDialog()
            dialog.arguments = Bundle()
            dialog.arguments.putLong(EXTRA_MAX_DURATION, maxDuration)
            dialog.isCancelable = false
            return dialog
        }
    }
}