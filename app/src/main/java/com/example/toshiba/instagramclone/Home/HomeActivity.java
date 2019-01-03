package com.example.toshiba.instagramclone.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.toshiba.instagramclone.Login.LoginActivity;
import com.example.toshiba.instagramclone.Models.Photo;
import com.example.toshiba.instagramclone.Models.UserAccountSettings;
import com.example.toshiba.instagramclone.R;
import com.example.toshiba.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.toshiba.instagramclone.Utils.MainfeedListAdapter;
import com.example.toshiba.instagramclone.Utils.SectionsPagerAdapter;
import com.example.toshiba.instagramclone.Utils.UniversalImageLoader;

import com.example.toshiba.instagramclone.Utils.ViewCommentsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener {


    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;

    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG,"oncreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout= (FrameLayout) findViewById(R.id.container1);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rellayoutParent);

        setupFirebaseAuth();

        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();


    }
    public void onCommentThreadSelected(Photo photo,String callingActivity){
        Log.d(TAG, "OnCommentThreadSelected: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo );
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity) );
        fragment.setArguments(args);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container1,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragmnet));
        transaction.commit();
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }
    /*
        Responsible for adding 3 tabs Camera,Home and Messages
         */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addfragment(new CameraFragment());  //index 0
        adapter.addfragment(new HomeFragment());    //index 1
        adapter.addfragment(new MessagesFragment());  //index 2

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);



        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_name);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }
    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx );

        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);



    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    /**
     * *****************************************firebase*********************************************
     */
    /**
     * setup up firebase auth object
     */
    /*
    check if the 'user' in logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);

        }

    }
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //checked if the user is logged in
                checkCurrentUser(user);
                if(user != null){
                    //user is signed to
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
            //...
        };

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
