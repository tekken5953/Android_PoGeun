package com.example.pogeun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity  {

    MediaPlayer mediaPlayer;
    ImageView play_btn, next_btn, previous_btn, music_img;
    TextView music_tx, seek_progress, seek_current;
    final Field[] fields = R.raw.class.getFields();
    int count = 0;
    int resID, imgID;
    String music_name, genre;
    SeekBar seekBar;
    ImageView back_press;
    int total;
    Intent musicIntent;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        getWindow().setStatusBarColor(Color.parseColor("#353433"));
        play_btn = findViewById(R.id.playbtn);
        next_btn = findViewById(R.id.nextbtn);
        previous_btn = findViewById(R.id.previousbtn);
        music_img = findViewById(R.id.musicimg);
        music_tx = findViewById(R.id.musictx);
        back_press = findViewById(R.id.backpress);
        seek_progress = findViewById(R.id.seekbar_progress);
        seek_current = findViewById(R.id.seekbar_current);
        genre = Objects.requireNonNull(getIntent().getExtras()).getString("genre");
        count = Objects.requireNonNull(getIntent().getExtras()).getInt("index");
        resID = getResources().getIdentifier(fields[count].getName(), "raw", getPackageName());
        imgID = getResources().getIdentifier(fields[count].getName(), "drawable", getPackageName());
        seekBar = findViewById(R.id.seekbar);
        mediaPlayer = MediaPlayer.create(PlayerActivity.this, resID);
        play_btn.setImageResource(R.drawable.pauseimg);
        resizeImg();
        translateText();
        music_tx.setText(music_name);
        music_tx.setSelected(true);
        total = mediaPlayer.getDuration() / 1000;
        seek_progress.setText((total / 60) + ":" + (total % 60));

        musicIntent = new Intent(this, MusicPlayClass.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataField(resID);
                startService(musicIntent);
                seekBar.setMax(mediaPlayer.getDuration());
                thread();
            }
        }, 1000);

        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(musicIntent);
                finish();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {  // 사용자가 시크바를 움직이면
                    mediaPlayer.seekTo(progress);   // 재생위치를 바꿔준다(움직인 곳에서의 음악재생)
                    thread();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //push play or pause
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_btn.setImageResource(R.drawable.pauseimg);
                if (mediaPlayer.isPlaying()) {
                    play_btn.setImageResource(R.drawable.playimg);
                    mediaPlayer.pause();
                } else {
                    play_btn.setImageResource(R.drawable.pauseimg);
                    mediaPlayer.start();
                    thread();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                if (count >= fields.length - 1) {
                                    count = 0;
                                    changeMusic();
                                } else {
                                    stopService(musicIntent);
                                    count++;
                                    changeMusic();
                                    play_btn.setImageResource(R.drawable.pauseimg);
                                    dataField(resID);
                                    startService(musicIntent);
                                }
                            } else {
                                if (count >= fields.length - 1) {
                                    count = 0;
                                    changeMusic();
                                } else {
                                    stopService(musicIntent);
                                    count++;
                                    changeMusic();
                                    play_btn.setImageResource(R.drawable.playimg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                previous_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                if (count <= 0) {
                                    count = fields.length - 1;
                                    changeMusic();
                                } else {
                                    mediaPlayer.stop();
                                    count--;
                                    changeMusic();
                                    dataField(resID);
                                    startService(musicIntent);
                                }
                            } else {
                                if (count <= 0) {
                                    count = fields.length - 1;
                                    changeMusic();
                                } else {
                                    stopService(musicIntent);
                                    count--;
                                    changeMusic();
                                    play_btn.setImageResource(R.drawable.playimg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        if (count < fields.length - 1) {
                            count++;
                            changeMusic();
                            mediaPlayer.start();
                        } else if (count >= fields.length - 1) {
                            count = 0;
                            changeMusic();
                            mediaPlayer.start();
                        }
                    }
                });
            }
        }, 1000);// 1초 정도 딜레이를 준 후 시작
    }



    public void translateText() {
        music_name = fields[count].getName();
        try {
            if (music_name.startsWith(genre)) {
                int i = music_name.indexOf("_");
                String s = music_name.substring(0, i + 1);
                music_name = music_name.replaceFirst(s, "");
                music_name = music_name.replace("9", " ").replace("_", " - ");
                String a = music_name.substring(0, 1).toUpperCase() + music_name.substring(1);
                String b = music_name.substring(a.lastIndexOf("-") + 2, a.indexOf("-") + 3);
                music_name = a.replace(b, b.toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resizeImg() {
        try {
            imgID = getResources().getIdentifier(fields[count].getName(), "drawable", getPackageName());
            music_img.setImageResource(imgID);
            Bitmap bitmap = ((BitmapDrawable) music_img.getDrawable()).getBitmap();
            float height = 300;
            float width = 300;
            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
            music_img.setImageBitmap(resizedBmp);
        } catch (Exception e) {
            e.printStackTrace();
            music_img.setImageResource(R.drawable.no_img);
            Bitmap bitmap = ((BitmapDrawable) music_img.getDrawable()).getBitmap();
            float height = 300;
            float width = 300;
            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
            music_img.setImageBitmap(resizedBmp);
        }
    }

    public void thread() {
        new Thread(new Runnable() {// 쓰레드 생성
            @Override
            public void run() {
                while (mediaPlayer.isPlaying()) {  // 음악이 실행중일때 계속 돌아가게 함
                    try {
                        int current = mediaPlayer.getCurrentPosition() / 1000;
                        // 1초마다 시크바 움직이게 함
                        if ((current % 60) < 10) {
                            seek_current.setText((current / 60) + ":0" + (current % 60));
                            // 현재 재생중인 위치를 가져와 시크바에 적용
                        } else {
                            seek_current.setText((current / 60) + ":" + (current % 60));
                            // 현재 재생중인 위치를 가져와 시크바에 적용
                        }
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        Thread.sleep(1000); // 1초마다 시크바 움직이게 함
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(musicIntent);
        finish();
    }

    public void changeMusic() {
        resID = getResources().getIdentifier(fields[count].getName(), "raw", getPackageName());
        imgID = getResources().getIdentifier(fields[count].getName(), "drawable", getPackageName());
        mediaPlayer = MediaPlayer.create(PlayerActivity.this, resID);
        resizeImg();
        translateText();
        total = mediaPlayer.getDuration() / 1000;
        seek_progress.setText((total / 60) + ":" + (total % 60));
        music_tx.setText(music_name);
        music_tx.setSelected(true);
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());
        thread();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                if (count < fields.length - 1) {
                    count++;
                    changeMusic();
                    dataField(resID);
                    startService(musicIntent);
                } else if (count >= fields.length - 1) {
                    count = 0;
                    changeMusic();
                    dataField(resID);
                    stopService(musicIntent);
                }
            }
        });
    }

    public void dataField(int res) {
        res = getResources().getIdentifier(fields[count].getName(), "raw", getPackageName());
        musicIntent.putExtra("resId",res);
    }
}