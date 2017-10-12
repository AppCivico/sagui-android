package com.eokoe.sagui.widgets.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.extensions.fromHtml
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.utils.Files
import kotlinx.android.synthetic.main.dialog_audiorecorder.view.*
import java.io.File
import java.util.*

/**
 * @author Pedro Silva
 * @since 11/10/17
 */
class AudioRecorderDialog private constructor() : DialogFragment() {
    val TAG = AudioRecorderDialog::class.simpleName

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private lateinit var audioFile: File

    private var startedAt = 0L
    private var maxDuration = 0L
    private var audioDuration = 0L

    var onSendClickListener: (File) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        maxDuration = arguments.getLong(EXTRA_MAX_DURATION)

        audioFile = createAudioFile(getString(R.string.app_name) + "_audio_")
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
            audioDuration = elapsedTime()
            dialogView.tvChronometer.text = getFormattedTime(audioDuration, maxDuration)
            if (audioDuration >= maxDuration) {
                stopRecord(dialogView)
            }
        }
        dialogView.btnCancel.setOnClickListener {
            dismiss()
        }
        return dialog.create()
    }

    private fun createAudioFile(filename: String): File {
        val audioDir = File(context.filesDir, Files.Path.AUDIO_PATH)
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        return File.createTempFile(filename, Files.Extensions.AMR, audioDir)
    }

    private fun startRecord(view: ViewGroup) {
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
        recorder!!.setOutputFile(audioFile.absolutePath)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        startedAt = SystemClock.elapsedRealtime()
        view.chronometer.base = startedAt
        view.chronometer.start()
        recorder!!.prepare()
        recorder!!.start()

        view.btnCancel.hide()
        view.hDivider.hide()
        view.btnOk.setText(R.string.stop)
        view.btnOk.setOnClickListener {
            stopRecord(view)
        }
    }

    private fun stopRecord(view: ViewGroup) {
        view.chronometer.stop()

        recorder!!.stop()
        recorder!!.reset()
        recorder!!.release()
        recorder = null

        view.btnOk.setText(R.string.send)
        view.btnCancel.show()
        view.hDivider.show()
        view.ibAudioAction.show()
        stopPlaying(view)
        view.btnOk.setOnClickListener {
            sendAudio()
        }
    }

    private fun startPlaying(view: ViewGroup) {
        val button = view.ibAudioAction
        player = MediaPlayer()

        player!!.setDataSource(audioFile.absolutePath)
        player!!.prepare()
        player!!.start()

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    if (player != null && player!!.isPlaying) {
                        val tv = view.tvChronometer
                        tv.post {
                            tv.text = getFormattedTime(
                                    player!!.currentPosition.toLong() / 1000, audioDuration)
                        }
                    } else {
                        timer.cancel()
                        timer.purge()
                    }
                }
            }
        }, 0, 100)

        player!!.setOnCompletionListener {
            stopPlaying(view)
        }

        button.setImageResource(R.drawable.ic_stop)
        button.setOnClickListener {
            stopPlaying(view)
        }
    }

    private fun stopPlaying(view: ViewGroup) {
        val button = view.ibAudioAction

        if (player != null) {
            player!!.release()
            player = null
        }

        view.tvChronometer.text = getFormattedTime(0, audioDuration)

        button.setImageResource(R.drawable.ic_play_arrow)
        button.setOnClickListener {
            startPlaying(view)
        }
    }

    private fun sendAudio() {
        onSendClickListener(audioFile)
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

        fun newInstance(maxDuration: Long = DEFAULT_MAX_DURATION,
                        listener: (File) -> Unit = {}): AudioRecorderDialog {
            val dialog = AudioRecorderDialog()
            dialog.arguments = Bundle()
            dialog.arguments.putLong(EXTRA_MAX_DURATION, maxDuration)
            dialog.isCancelable = false
            dialog.onSendClickListener = listener
            return dialog
        }
    }
}