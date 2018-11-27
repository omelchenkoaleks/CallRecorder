package com.abc.callrecorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import com.abc.callrecorder.contacts.ContactProvider;
import com.wang.avi.AVLoadingIndicatorView;

public class ListenActivity extends AppCompatActivity {
    String number;
    ArrayList<String> recordinglist=new ArrayList<>();
    ArrayAdapter adapter;
    ArrayList<String> listen=new ArrayList<>();
    ArrayList<String> tracks=new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        admobbanner();
        startAnim();

        number=getIntent().getStringExtra("NUMBER");
        Toolbar toolbar=findViewById(R.id.action_bar);
        toolbar.setTitle(number);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String path= ContactProvider.getFolderPath(this);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,tracks);
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        listView=findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        File listfiles[]=file.listFiles();
        if(listfiles!=null){
            for(File list:listfiles){
                recordinglist.add(list.getName());
            }
        }
        if(!recordinglist.isEmpty()){
            int temp=0;
            for(String s:recordinglist){
                String numb[]=s.split("__");
                if(number.equals(numb[0])){
                    ++temp;
                    listen.add(s);
                    tracks.add("Recording"+temp);
                }
            }
            adapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactProvider.playmusic(getApplicationContext(),ContactProvider.getFolderPath(getApplicationContext())+"/" +listen.get(i));
            }
        });
    }

    void startAnim(){
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avi.smoothToShow();
    }

    private void admobbanner(){

        if (Internetconnection.checkConnection(this)) {
    }}
}
