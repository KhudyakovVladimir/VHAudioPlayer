package com.example.vhaudioplayer;

import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;

public class Equalizer extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    SeekBar seekBarEqualizer1;
    SeekBar seekBarEqualizer2;
    SeekBar seekBarEqualizer3;
    SeekBar seekBarEqualizer4;
    SeekBar seekBarEqualizer5;
    android.media.audiofx.Equalizer equalizer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equalizer);

        seekBarEqualizer1 = findViewById(R.id.seekBarEqualizer1);
        seekBarEqualizer2 = findViewById(R.id.seekBarEqualizer2);
        seekBarEqualizer3 = findViewById(R.id.seekBarEqualizer3);
        seekBarEqualizer4 = findViewById(R.id.seekBarEqualizer4);
        seekBarEqualizer5 = findViewById(R.id.seekBarEqualizer5);
        seekBarEqualizer1.setOnSeekBarChangeListener(this);
        seekBarEqualizer2.setOnSeekBarChangeListener(this);
        seekBarEqualizer3.setOnSeekBarChangeListener(this);
        seekBarEqualizer4.setOnSeekBarChangeListener(this);
        seekBarEqualizer5.setOnSeekBarChangeListener(this);

        equalizer1 = new android.media.audiofx.Equalizer(0, Player.mediaPlayer.getAudioSessionId());
        equalizer1.setEnabled(true);
        System.out.println("equalizer1.getNumberOfBands() = " + equalizer1.getNumberOfBands());
        System.out.println("EQ band 2 freqRange = " + Arrays.toString(equalizer1.getBandFreqRange(equalizer1.getBand(1000))));
        System.out.println("EQ band 2 levelRange = " + Arrays.toString(equalizer1.getBandLevelRange()));

        short [] levels = equalizer1.getBandLevelRange();
        short max = levels[1];

        seekBarEqualizer1.setMax((int) max * 2);
        seekBarEqualizer1.setProgress(0);

        seekBarEqualizer2.setMax((int) max * 2);
        seekBarEqualizer2.setProgress(0);

        seekBarEqualizer3.setMax((int) max * 2);
        seekBarEqualizer3.setProgress(0);

        seekBarEqualizer4.setMax((int) max * 2);
        seekBarEqualizer4.setProgress(0);

        seekBarEqualizer5.setMax((int) max * 2);
        seekBarEqualizer5.setProgress(0);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar.equals(seekBarEqualizer1)){
            System.out.println("EQ_1");
            equalizer1.setBandLevel(equalizer1.getBand(1000), (short) seekBarEqualizer1.getProgress());
        }
        if(seekBar.equals(seekBarEqualizer2)){
            System.out.println("EQ_2");
            equalizer1.setBandLevel(equalizer1.getBand(50000), (short) seekBarEqualizer2.getProgress());
        }
        if(seekBar.equals(seekBarEqualizer3)){
            System.out.println("EQ_3");
            equalizer1.setBandLevel(equalizer1.getBand(1000000), (short) seekBarEqualizer3.getProgress());
        }
        if(seekBar.equals(seekBarEqualizer4)){
            System.out.println("EQ_4");
            equalizer1.setBandLevel(equalizer1.getBand(5000000), (short) seekBarEqualizer4.getProgress());
        }
        if(seekBar.equals(seekBarEqualizer5)){
            System.out.println("EQ_5");
            equalizer1.setBandLevel(equalizer1.getBand(100000000), (short) seekBarEqualizer5.getProgress());
        }
    }
}