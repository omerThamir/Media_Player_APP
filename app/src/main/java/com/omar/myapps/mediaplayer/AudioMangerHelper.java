package com.omar.myapps.mediaplayer;

import android.content.Context;

import android.media.AudioManager;

public class AudioMangerHelper {
    public Context mContext;

    public AudioMangerHelper(Context context) {
        mContext = context;
    }
    public AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {

                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mContext instanceof PlayingActivity) {
                        if (((PlayingActivity) mContext).mediaPlayer.isPlaying())
                            ((PlayingActivity) mContext).mediaPlayer.setVolume(0.4f, 0.4f);
                    }
                    return;


                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mContext instanceof PlayingActivity) {
                        ((PlayingActivity) mContext).mediaPlayer.pause();
                        ((PlayingActivity) mContext).playPauseBTN.setImageResource(R.drawable.ic_play_circle_filled_24);
                    }
                    return;

                // Lost focus for an unbounded amount of time: stop playback and release media player
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mContext instanceof PlayingActivity) {
                        ((PlayingActivity) mContext).clearResources();
                    }
                    return;

                // resume playback
                case AudioManager.AUDIOFOCUS_GAIN:
                    try {

                        if (mContext instanceof PlayingActivity) {
                            ((PlayingActivity) mContext).mediaPlayer.start();
                            ((PlayingActivity) mContext).mediaPlayer.setVolume(1.0f, 1.0f);
                        }
                        return;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    };
}
