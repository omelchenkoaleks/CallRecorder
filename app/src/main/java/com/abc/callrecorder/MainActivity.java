package com.abc.callrecorder;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.abc.callrecorder.utils.NotificationUtils;
import com.abc.callrecorder.SqliteDatabase.ContactsDatabase;
import com.abc.callrecorder.Transformer.ZoomOutPageTransformer;
import com.abc.callrecorder.adapter.ScreenSlidePagerAdapter;
import com.abc.callrecorder.contacts.ContactProvider;
import com.abc.callrecorder.fragments.Incomming;
import com.abc.callrecorder.fragments.Outgoing;
import com.abc.callrecorder.pojo_classes.Contacts;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private  ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout bg;
    private ScreenSlidePagerAdapter adapter;
    static querySearch queylistener;
    static querySearch2 queylistener2;
    static querySearch3 queylistener3;
    ArrayList<Contacts> phoneContacts=new ArrayList<>();
    ArrayList<String> recordinglist=new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2001;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            checkAndRequestPermissions();
        }

        final String PREFS_NAME = "notification";

        SharedPreferences noti = getSharedPreferences(PREFS_NAME, 0);

        if (noti.getBoolean("first_notificationn", true)) {
            workwithfirebase();
            //finish();
        } else {


        }

        boolean Auth=getIntent().getBooleanExtra("AUTH",false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SharedPreferences SP1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean b1=SP1.getBoolean("LOCK",false);
        if(b1&&!Auth){
            if (ManageLockType.getLockType().equals("pin")){
                Intent i = new Intent(getApplicationContext(),EnterNormalPIN.class);
                i.putExtra("main","true");
                finish();
                startActivity(i);
            }
            if (ManageLockType.getLockType().equals("pattern")){
                Intent i = new Intent(getApplicationContext(),EnterPatternLock.class);
                i.putExtra("main","true");
                finish();
                startActivity(i);
            }
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startAnim();
        viewPager=findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        adapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout=findViewById(R.id.tabs);
        bg=findViewById(R.id.bg);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    changeColorOfStatusAndActionBar();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        showlistfile();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {

            tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_002_incoming_phone_call_symbol));
            tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_001_outgoing_call));

        } else {

            tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_recvied));
            tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_outgoing));
        }

        admobbanner();

       // final Handler handler = new Handler();
       // handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {

                admobbanner();
        //    }

       // }, 1000);

       // final Handler handler2 = new Handler();
       // handler2.postDelayed(new Runnable() {
          //  @Override
          //  public void run() {

           //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
           //         requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//
            //    } else {
            //        phoneContacts=ContactProvider.getContacts(getApplicationContext());
            //        storeToDatabase(phoneContacts);
            //    }

           // }
        //}, 8000);



    }

    private void storeToDatabase(ArrayList<Contacts> phoneContacts) {
        ContactsDatabase datbaseObj=new ContactsDatabase(this);
        for (Contacts con:phoneContacts){
            if(datbaseObj.isContact(con.getNumber()).getNumber()!=null){
               datbaseObj.updateContact(con);
            }else{
                datbaseObj.addContact(con);
            }
        }
    }

    private void showlistfile() {
        Bundle bundles=new Bundle();
        String path=ContactProvider.getFolderPath(this);
        File file=new File(path);
        if(!file.exists()){

            file.mkdirs();
        }
        File listfiles[]=file.listFiles();
        if(listfiles!=null){
            for(File list:listfiles){
                recordinglist.add(list.getName());
            }
        }
        bundles.putStringArrayList("RECORDING",recordinglist);

        Incomming fr=new Incomming();
        fr.setArguments(bundles);
        Outgoing outgoing=new Outgoing();
        outgoing.setArguments(bundles);

        adapter.addFrag(fr,"Incomming");
        adapter.addFrag(outgoing,"Outgoing");
        adapter.notifyDataSetChanged();
    }

    private void changeColorOfStatusAndActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
             switch (viewPager.getCurrentItem()) {
                case 0:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.smooth_red));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.smooth_red));
                    break;
                case 1:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.smooth_red));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.smooth_red));

                    break;
                default:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.smooth_red));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.smooth_red));
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_resourse_file,menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
                searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    queylistener2.Search_name2(newText+"");
                try {
                   queylistener3.Search_name3(newText+"");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                if(!newText.isEmpty()){
                    tabLayout.setVisibility(View.GONE);
                }else{
                    tabLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.setting){
            Intent intent= new Intent(MainActivity.this,SettingsActivity2.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0){
           super.onBackPressed();
        }else{
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent intent= new Intent(MainActivity.this,SettingsActivity2.class);
            startActivity(intent);
        } else if (id == R.id.fav) {
            Intent intent= new Intent(MainActivity.this,Favourite.class);
            intent.putStringArrayListExtra("RECORD",recordinglist);
            startActivity(intent);
        } else if (id == R.id.updatecontact) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                phoneContacts=ContactProvider.getContacts(getApplicationContext());
                storeToDatabase(phoneContacts);
                Toast.makeText(MainActivity.this, "Contact Database Updated.", Toast.LENGTH_LONG).show();

            }

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Best Call recorder app download now.https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }else if (id == R.id.rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
        }else if(id==R.id.recording_issue){
            Intent intent= new Intent(MainActivity.this,Recording_issue.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static  void setQueylistener(querySearch quey){
        queylistener=quey;
    }
    public interface querySearch{
        public void Search_name(String name1);
    }
    public static  void setQueylistener2(querySearch2 quey1){
        queylistener2=quey1;
    }
    public interface querySearch2{
        public void Search_name2(String name1);
    }
    public static  void setQueylistener3(querySearch3 quey3){
        queylistener3=quey3;
    }
    public interface querySearch3{
        public void Search_name3(String name1);
    }

    private  boolean checkAndRequestPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.clear();
        int recordaudio=ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);//
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);//
        int call= ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);//
        int read_phonestate= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);//
        int Capture_audio_output= ContextCompat.checkSelfPermission(this, Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        int process_outgoing_call= ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);//
        int modify_audio_setting= ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS);//
        int read_contacts= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);//

        if (read_contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (modify_audio_setting != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }
        if (process_outgoing_call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }

        if (read_phonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (recordaudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Capture_audio_output!=PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0){

                } else {
                    Toast.makeText(this, "Please Allow All Permission To Continue..", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                    phoneContacts=ContactProvider.getContacts(getApplicationContext());
                    storeToDatabase(phoneContacts);
        }
    }

    void startAnim(){
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avi.smoothToShow();
    }

    private void admobbanner(){


    }

    private void workwithfirebase()
    {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constant.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC_GLOBAL);

                    final String PREFS_NAME = "notification";
                    SharedPreferences noti = getSharedPreferences(PREFS_NAME, 0);
                    noti.edit().putBoolean("first_notificationn", false).apply();
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Constant.PUSH_NOTIFICATION)) {

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        displayFirebaseRegId();
    }


    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constant.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(Constant.TAG, "Firebase reg id: " + regId);

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constant.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constant.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications(this);


    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
