package com.abc.callrecorder;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.abc.callrecorder.base.BaseActivityUpEnableWithMenu;
import com.wang.avi.AVLoadingIndicatorView;

public class SettingsActivity2 extends BaseActivityUpEnableWithMenu implements View.OnClickListener {

    public SettingsActivity2() {
        super(R.string.action_settings, R.menu.menu_resourse_file);

    }

    @Override
    protected void init() {
        initData();
        initView();
        initEvent();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_settings);

        Toolbar toolbar=findViewById(R.id.action_bar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startAnim();
        admobbanner();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_settings, new SettingsActivity())
                .commit();

    }


    @Override
    protected void initEvent() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }


    }
    void startAnim(){
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.smoothToShow();
    }

    private void admobbanner(){

    }
}
