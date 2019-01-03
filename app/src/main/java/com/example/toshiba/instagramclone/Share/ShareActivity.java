package com.example.toshiba.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import com.example.toshiba.instagramclone.R;
import com.example.toshiba.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.toshiba.instagramclone.Utils.Permissions;
import com.example.toshiba.instagramclone.Utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity{
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSION_REQUEST = 1;

    private ViewPager mViewPager;

    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG,"onCreate: started,");

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();

        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    /**
     * return the current tab numbers
     * 0 = galleryfragment
     * 1 = photofragment
     * @return
     */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for managing the tabs
     */
     private  void setupViewPager(){
         SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
         adapter.addfragment(new GalleryFragment());
         adapter.addfragment(new PhotoFragment());

      mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
      mViewPager.setAdapter(adapter);

         TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
         tabLayout.setupWithViewPager(mViewPager);

         tabLayout.getTabAt(0).setText("gallery");
         tabLayout.getTabAt(1).setText("photo");


     }
    /**
     * verifiy all the permissions denied passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifiying permissions");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSION_REQUEST

        );
    }
    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }
    /**
     * Check the array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array");

        for(int i = 0 ; i < permissions.length; i++){
            String check = (permissions[i]);
            if(!checkPermissions(check)){
                return false;
            }
        }
        return  true;
    }

    /**
     * Check a single permission is it has been verifeid
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this,permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: PERMISSION IS NOT GRANTED FOR " + permission);
            return false;
        }
        else {
            Log.d(TAG, "checkPermissions: PERMISSION IS WAS GRANTED FOR " + permission);

            return true;
        }
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
}
