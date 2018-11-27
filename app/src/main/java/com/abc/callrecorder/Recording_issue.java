package com.abc.callrecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jaredrummler.android.device.DeviceName;
import com.wang.avi.AVLoadingIndicatorView;

public class Recording_issue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_issue);
        Toolbar toolbar=findViewById(R.id.action_bar);
        toolbar.setTitle("Recording Issue");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String deviceName = DeviceName.getDeviceName();
        TextView tv=(TextView)findViewById(R.id.textView);
        tv.setText(deviceName);
        startAnim();
        admobbanner();
    }

    void startAnim(){
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.smoothToShow();
    }

    private void admobbanner() {
    }
}
