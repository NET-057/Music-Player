package com.mytech.lab.musicplayer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.telephony.TelephonyManager
import android.view.KeyEvent
import android.widget.Toast
import com.mytech.lab.musicplayer.Constants
import com.mytech.lab.musicplayer.Controls
import com.mytech.lab.musicplayer.SongService
import java.util.concurrent.TimeUnit


/**
 * Created by lnx on 26/3/18.
 */
public class PhoneStateReceiver : BroadcastReceiver(), AudioManager.OnAudioFocusChangeListener{

    var mgr:TelephonyManager? = null
    override fun onReceive(context: Context?, intent: Intent?) {

        try {
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            mgr = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                if (intent!!.getAction() == Intent.ACTION_MEDIA_BUTTON)
                {
                    val keyEvent = intent.getExtras()!!.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
                    if (keyEvent.action != KeyEvent.ACTION_DOWN)
                        return

                    when (keyEvent.keyCode)
                    {
                        KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> if (!Constants.SONG_PAUSED) {
                            Toast.makeText(context,Constants.HEADPHONE,Toast.LENGTH_SHORT).show()
                            Controls.playPauseControl(Constants.PAUSE)

                        } else {
                            Controls.playPauseControl(Constants.PLAY)
                        }
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            Controls.previousControl(context)
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            Controls.nextControl(context)
                        }

                    }
                }

                if (intent.getAction() == SongService.NOTIFY_PAUSEPLAY)
                {

                    var status:String? = null

                    if(Constants.SONG_PAUSED){
                        status = Constants.PLAY
                    } else {
                        status = Constants.PAUSE
                    }
                    Controls.playPauseControl(status)

                }
                else if (intent.getAction() == SongService.NOTIFY_NEXT) {
                    Controls.nextControl(context)

                }
                else if (intent.getAction() == SongService.NOTIFY_DELETE) {

                    val i = Intent(context, SongService::class.java)
                    context.stopService(i)
                    Constants.SONG_PAUSED = true
                    Constants.PLAYER_UI?.sendMessage(Constants.PLAYER_UI?.obtainMessage(0))

                }
                else if (intent.getAction() == SongService.NOTIFY_PREVIOUS) {
                    Controls.previousControl(context)
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onAudioFocusChange(p0: Int) {

        if (SongService.mPlayer != null && !Constants.SELF_CHANGE) {
            when (p0) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    Constants.SONG_PAUSED = false
                    Controls.playPauseControl(Constants.PLAY)
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    Controls.playPauseControl(Constants.PAUSE)
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    Controls.playPauseControl(Constants.PAUSE)
                    Handler().postDelayed({
                        Controls.playPauseControl(Constants.PLAY)
                    }, TimeUnit.SECONDS.toMillis(2))
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    Controls.playPauseControl(Constants.PAUSE)

                }
            }
        }

    }


    fun ComponentName(): String {
        return this.javaClass.name
    }



}