package de.felix_h.focustimerforproductivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private long START_TIME_IN_MILLIS = 0;

    private boolean activated;

    private RelativeLayout mRelativeLayout;
    private RelativeLayout mRelativeLayoutBig;
    private TextView mTextView;
    private CountDownTimer mCountDownTimer;
    private TextView countdown;
    private NumberPicker np;
    private ImageView volume;
    private ImageView volume_off;
    private boolean volume_setting;
    private boolean hint;
    private TextView hintText;


    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreencall();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        np = findViewById(R.id.np);
        mTextView = findViewById(R.id.reset);
        countdown = findViewById(R.id.text_view_countdown);
        mRelativeLayout = findViewById(R.id.rl);
        mRelativeLayoutBig = findViewById(R.id.rlbig);
        hintText = findViewById(R.id.hint);
        activated = false;
        volume = findViewById(R.id.volume);
        volume_off = findViewById(R.id.volume_off);

        final SharedPreferences sharedPref = getSharedPreferences("focus", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        START_TIME_IN_MILLIS = sharedPref.getInt("time", 600000);
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        volume_setting = sharedPref.getBoolean("volume", true);
        hint = sharedPref.getBoolean("hint", true);

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activated){
                    activated = true;
                    FullScreencall();
                    startTimer();
                    if(hint)
                    {
                        Toast.makeText(getApplicationContext(), "Click again to pause the timer", Toast.LENGTH_LONG).show();
                        hintText.setVisibility(View.INVISIBLE);
                        editor.putBoolean("hint", false);
                        hint = false;
                    }
                }
                else
                {
                    activated = false;
                    pauseTimer();
                }
            }
        });

        countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activated){
                    activated = true;
                    FullScreencall();
                    startTimer();
                    if(hint)
                    {
                        Toast.makeText(getApplicationContext(), "Click again to pause the timer", Toast.LENGTH_LONG).show();
                        hintText.setVisibility(View.INVISIBLE);
                        editor.putBoolean("hint", false);
                        hint = false;
                    }
                }
                else
                {
                    activated = false;
                    pauseTimer();
                }
            }
        });

        if(hint){
            hintText.setVisibility(View.VISIBLE);
        }

        if(volume_setting)
            volume.setVisibility(View.VISIBLE);
        else
            volume_off.setVisibility(View.VISIBLE);

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("volume", false);
                editor.commit();
                volume_setting = false;
                volume.setVisibility(View.INVISIBLE);
                volume_off.setVisibility(View.VISIBLE);
            }
        });

        volume_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("volume", true);
                editor.commit();
                volume_setting = true;
                volume.setVisibility(View.VISIBLE);
                volume_off.setVisibility(View.INVISIBLE);
            }
        });

        np.setMinValue(1);
        np.setMaxValue(60);
        np.setValue((int) (START_TIME_IN_MILLIS/60/1000));
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                int time = newVal*60*1000;
                START_TIME_IN_MILLIS = time;
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();

                editor.putInt("time", time);
                editor.commit();
            }
        });


        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTextView.setVisibility(View.VISIBLE);
                np.setVisibility(View.VISIBLE);
                mRelativeLayout.setBackgroundColor(Color.WHITE);
                mRelativeLayoutBig.setBackgroundColor(Color.WHITE);
                countdown.setTextColor(Color.BLACK);
                resetTimer();
                if(volume_setting)
                {
                    final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.accomplished);
                    mp.start();
                }

            }
        }.start();

        mTextView.setVisibility(View.INVISIBLE);
        np.setVisibility(View.INVISIBLE);
        mRelativeLayout.setBackgroundColor(Color.BLACK);
        mRelativeLayoutBig.setBackgroundColor(Color.BLACK);
        countdown.setTextColor(Color.WHITE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTextView.setVisibility(View.VISIBLE);
        np.setVisibility(View.VISIBLE);
        mRelativeLayout.setBackgroundColor(Color.WHITE);
        mRelativeLayoutBig.setBackgroundColor(Color.WHITE);
        countdown.setTextColor(Color.BLACK);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdown.setText(timeLeftFormatted);
    }


    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
