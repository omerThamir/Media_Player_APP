package com.omar.myapps.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class PlayingActivity extends AppCompatActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "1000001";

    private ArrayList<Song> songsList;
    int songIndex;
    String songName;
    String songUri;

    void showNotification(String songName) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Title")
                .setContentText("Content")
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        Toast.makeText(this, "notification show be shown", Toast.LENGTH_SHORT).show();
    }

    private ImageView imageView;

    private MediaPlayer.OnCompletionListener mediaOncompleteLisiner = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //if there no loop => stop and clean
            if (!Utils.IsOneByOneLoop && !Utils.isRandomLoop && !Utils.isItSelfLoop) {
                clearResources();
                mHandler.removeCallbacks(runnable);

            } else if (Utils.isRandomLoop) {
                clearResources();
                int randomIndex = new Random().nextInt(songsList.size() - 1);
                playSound(randomIndex);
            } else if (Utils.IsOneByOneLoop) {
                int currentIndex = Song.currentSongIndex;
                currentIndex += 1;
                if (currentIndex >= songsList.size()) currentIndex = 0;
                playSound(currentIndex);

            } else if (Utils.isItSelfLoop) {
                int curretIndex = Song.currentSongIndex;
                playSound(curretIndex);

            }
        }
    };

    public Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (mediaPlayer != null) {
                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 800;
                seekBar.setProgress(mCurrentPosition);
            }
            mHandler.postDelayed(this, 800);
        }
    };


    Handler mHandler;

    public MediaPlayer mediaPlayer;
    private SeekBar seekBar;

    private TextView currentProgressTV, songLenghTV;

    public ImageButton playPauseBTN,
            loopItselfBtn, oneByOneLoopBtn, randomLoopBtn;


    private AudioManager audioManager;
    private AudioMangerHelper audioMangerHelper = new AudioMangerHelper(PlayingActivity.this);


    void init() {

        songsList = new ArrayList<>();
        seekBar = findViewById(R.id.seekBar);
        songLenghTV = findViewById(R.id.songLength);
        currentProgressTV = findViewById(R.id.currentProgress);
        playPauseBTN = findViewById(R.id.playPauseBTN);
        oneByOneLoopBtn = findViewById(R.id.loopOneByOneBTN);
        loopItselfBtn = findViewById(R.id.loopIsSelfBTN);
        randomLoopBtn = findViewById(R.id.loopRandomBTN);

        imageView = findViewById(R.id.imageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        init();

        Glide.with(getApplicationContext()).load(R.drawable.gif).into(imageView);
        Intent myIntent = getIntent();
        songsList = (ArrayList<Song>) myIntent.getExtras().getSerializable("songsList");
        songIndex = myIntent.getIntExtra("songIndex", 0);
        songName = myIntent.getStringExtra("songName");
        songUri = myIntent.getStringExtra("songUri");

        playSound(songIndex);


        mHandler = new Handler();
        //Make sure you update Seekbar on UI thread
        PlayingActivity.this.runOnUiThread(runnable);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 800);
                }
                if (mediaPlayer != null) {
                    String currentPos = Utils.convertMediaPalyerDurationToTimeString(mediaPlayer.getCurrentPosition());
                    currentProgressTV.setText(currentPos);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.postDelayed(runnable, 0);
            }
        });


        findViewById(R.id.playPauseBTN).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // if the user clicked on play without selection song play currentSongIndex
                        //  initialy =Zero
                        if (mediaPlayer == null) {
                            playSound(Song.currentSongIndex);
                            playPauseBTN.setImageResource(R.drawable.ic_round_pause_40);

                            /**
                             * to keep the screen from dimming or the processor from sleeping
                             */
                            mediaPlayer.setScreenOnWhilePlaying(true);
                            return;
                        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            playPauseBTN.setImageResource(R.drawable.ic_round_pause_40);

                            /**
                             * to keep the screen from dimming or the processor from sleeping
                             */
                            mediaPlayer.setScreenOnWhilePlaying(true);
                            return;
                        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            playPauseBTN.setImageResource(R.drawable.ic_round_play_40);
                            mediaPlayer.setScreenOnWhilePlaying(false);
                            releaseAudioFocusForMyApp(PlayingActivity.this);
                            return;
                        }
                    }
                });

        findViewById(R.id.stopBTN).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mediaPlayer != null) {
                            mediaPlayer.setScreenOnWhilePlaying(false);
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            releaseAudioFocusForMyApp(PlayingActivity.this);
                            playPauseBTN.setImageResource(R.drawable.ic_round_play_40);
                        }
                    }
                });

        findViewById(R.id.nextBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentIndex = Song.currentSongIndex;
                currentIndex += 1;
                if (currentIndex >= songsList.size()) currentIndex = 0;
                playSound(currentIndex);
                Song.currentSongIndex = currentIndex;
            }
        });

        findViewById(R.id.prevBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentIndex = Song.currentSongIndex;
                currentIndex -= 1;
                if (currentIndex < 0) currentIndex = songsList.size() - 1;
                playSound(currentIndex);
                Song.currentSongIndex = currentIndex;
            }
        });


        oneByOneLoopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.IsOneByOneLoop) {
                    Toast.makeText(PlayingActivity.this, "one by one loop is on", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = false;
                    Utils.IsOneByOneLoop = true;
                    Utils.isRandomLoop = false;
                    updateRandomRepeat();
                } else {
                    Toast.makeText(PlayingActivity.this, "one by one  loop is off", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = false;
                    Utils.IsOneByOneLoop = false;
                    Utils.isRandomLoop = false;
                    updateRandomRepeat();
                }

            }
        });

        loopItselfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isItSelfLoop) {
                    Toast.makeText(PlayingActivity.this, "itself loop is on", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = true;
                    Utils.IsOneByOneLoop = false;
                    Utils.isRandomLoop = false;
                    updateRandomRepeat();
                } else {
                    Toast.makeText(PlayingActivity.this, "itself loop is off", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = false;
                    Utils.IsOneByOneLoop = false;
                    Utils.isRandomLoop = false;
                    updateRandomRepeat();
                }
            }

        });

        randomLoopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isRandomLoop) {
                    Toast.makeText(PlayingActivity.this, "random loop is on", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = false;
                    Utils.IsOneByOneLoop = false;
                    Utils.isRandomLoop = true;
                    updateRandomRepeat();
                } else {
                    Toast.makeText(PlayingActivity.this, "random loop is off", Toast.LENGTH_SHORT).show();
                    Utils.isItSelfLoop = false;
                    Utils.IsOneByOneLoop = false;
                    Utils.isRandomLoop = false;
                    updateRandomRepeat();
                }
            }
        });

    }

    private boolean requestAudioFocusForMyApp(final Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(audioMangerHelper.mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
            return true;
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
            return false;
        }

    }

    void releaseAudioFocusForMyApp(final Context context) {
        if (audioMangerHelper.mOnAudioFocusChangeListener != null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.abandonAudioFocus(audioMangerHelper.mOnAudioFocusChangeListener);
        }
    }


    public void clearResources() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;

            //  stopping audio focus
            if (audioManager != null)
                audioManager.abandonAudioFocus(audioMangerHelper.mOnAudioFocusChangeListener);
            audioManager = null;
        }
    }

    public void playSound(Uri uri) {
        try {
            clearResources();
            if (requestAudioFocusForMyApp(PlayingActivity.this)) {

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                // setCurrentWorkingReaderAndTitle(songIndex);
                seekBar.setMax(mediaPlayer.getDuration() / 800);
                playPauseBTN.setImageResource(R.drawable.ic_round_play_40);

                String time = Utils.convertMediaPalyerDurationToTimeString(mediaPlayer.getDuration());
                songLenghTV.setText(time);
                mediaPlayer.setOnCompletionListener(mediaOncompleteLisiner);

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
    }


    public void playSound(int index) {


        clearResources();// for previous song
        if (requestAudioFocusForMyApp(PlayingActivity.this)) {

            mediaPlayer = MediaPlayer.create(PlayingActivity.this, Uri.parse(songsList.get(index).filepath));
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 800);
            playPauseBTN.setImageResource(R.drawable.ic_round_pause_40);
            mediaPlayer.setLooping(false);
            String time = Utils.convertMediaPalyerDurationToTimeString(mediaPlayer.getDuration());
            songLenghTV.setText(time);
            mediaPlayer.setOnCompletionListener(mediaOncompleteLisiner);
            showNotification("testtttt song name");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler == null) {
            mHandler = new Handler();
            mHandler.postDelayed(runnable, 0);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
            mHandler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
            mHandler = null;
        }
    }

    protected void updateRandomRepeat() {
        if (Utils.isItSelfLoop && !Utils.IsOneByOneLoop && !Utils.isRandomLoop) {
            loopItselfBtn.setImageResource(R.drawable.ic_loop_it_self_on_24);
            oneByOneLoopBtn.setImageResource(R.drawable.ic_loop_one_by_one_off_24);
            randomLoopBtn.setImageResource(R.drawable.ic_random_loop_off_24);
        } else if (!Utils.isItSelfLoop && Utils.IsOneByOneLoop && !Utils.isRandomLoop) {
            oneByOneLoopBtn.setImageResource(R.drawable.ic_loop_one_by_one_on_24);
            loopItselfBtn.setImageResource(R.drawable.ic_loop_it_self_off_24);
            randomLoopBtn.setImageResource(R.drawable.ic_random_loop_off_24);
        } else if (!Utils.isItSelfLoop && !Utils.IsOneByOneLoop && Utils.isRandomLoop) {
            randomLoopBtn.setImageResource(R.drawable.ic_random_loop_on_24);
            oneByOneLoopBtn.setImageResource(R.drawable.ic_loop_one_by_one_off_24);
            loopItselfBtn.setImageResource(R.drawable.ic_loop_it_self_off_24);
        } else {
            // all are off
            randomLoopBtn.setImageResource(R.drawable.ic_random_loop_off_24);
            oneByOneLoopBtn.setImageResource(R.drawable.ic_loop_one_by_one_off_24);
            loopItselfBtn.setImageResource(R.drawable.ic_loop_it_self_off_24);
        }

    }


}