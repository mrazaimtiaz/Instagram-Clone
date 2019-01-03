package com.example.toshiba.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.toshiba.instagramclone.R;

import com.example.toshiba.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.toshiba.instagramclone.Utils.FirebaseMethods;
import com.example.toshiba.instagramclone.Utils.SectionsStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";

    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewpager;
    private RelativeLayout mRelativelayout;
    private static final int ACTIVITY_NUM = 4;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activitysettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started.");
        mViewpager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativelayout = (RelativeLayout) findViewById(R.id.rellayout1);

        setupSettingsList();
        setupBottomNavigationView();
        setupFragments();
        getincomingIntent();

        //setup the backarrow for navigation back to profile activity
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to profile activity");
                finish();
            }
        });

    }
    private void getincomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {

            //if there is an imageurl attached as an extra, then it was choosen from the gallery/photo fragment
            Log.d(TAG, "getincomingIntent: new incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile photo
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile photo
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0, null,
                            (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }
            }


            if (intent.hasExtra(getString(R.string.calling_activity))) {
                Log.d(TAG, "getincomingIntent: recieved incoming intent from " + getString(R.string.profile_activity));
                setViewpager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
            }
        }

    }
    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.edit_profile_fragment)); //Fragment 0
        pagerAdapter.addFragment(new SignOutFragment(),getString(R.string.sign_out_fragment));   //Fragment 1


    }
    public void setViewpager(int fragmnetNumbers){
        mRelativelayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewpager: navigating to fragment #, " + fragmnetNumbers);
        mViewpager.setAdapter(pagerAdapter);
        mViewpager.setCurrentItem(fragmnetNumbers);
    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: intializing account settings list");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));   //Fragment 0
        options.add(getString(R.string.sign_out_fragment));    //Fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,options);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragemnt#, " + position);
                setViewpager(position);

            }
        });
    }
    private void setupBottomNavigationView(){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx );

        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);



    }

}
