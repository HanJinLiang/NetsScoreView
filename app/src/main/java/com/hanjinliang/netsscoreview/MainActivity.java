package com.hanjinliang.netsscoreview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.hanjinliang.netsscore.NetsScoreView;

public class MainActivity extends AppCompatActivity {

    SeekBar mSeekBar;
    NetsScoreView mNetsScoreView;
    LinearLayout valueLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBar= (SeekBar) findViewById(R.id.seekBarCount);
        mNetsScoreView= (NetsScoreView) findViewById(R.id.NetsScoreView);
        valueLayout= (LinearLayout) findViewById(R.id.valueLayout);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=3&&fromUser){
                    mSeekBar.setProgress(3);
                    return;
                }
                addValueView(progress);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBar.setProgress(6);

        mNetsScoreView.setTxtFormat(new NetsScoreView.TxtFormat() {
            @Override
            public String originalDataFormat(int index, float value) {
                return value+"分数";//格式化显示数值
            }
        });
    }
    float[] scores;
    private void addValueView(int progress) {
        valueLayout.removeAllViews();
        scores=new float[progress];
        for(int i=0;i<progress;i++){
            SeekBar seekBar=new SeekBar(this);
            LinearLayout.LayoutParams  ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setMargins(0,20,0,20);
            seekBar.setLayoutParams(ll);
            seekBar.setMax(100);
            scores[i]=50;
            seekBar.setTag(i);
            seekBar.setOnSeekBarChangeListener(listener);
            seekBar.setProgress(50);


            valueLayout.addView(seekBar);
        }
    }

    private SeekBar.OnSeekBarChangeListener listener= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            scores[(int) seekBar.getTag()]=progress;
            mNetsScoreView.setData(mSeekBar.getProgress(),scores);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
