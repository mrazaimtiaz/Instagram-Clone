package com.example.toshiba.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.toshiba.instagramclone.Models.Photo;
import com.example.toshiba.instagramclone.Models.User;
import com.example.toshiba.instagramclone.R;
import com.example.toshiba.instagramclone.Utils.ViewCommentsFragment;
import com.example.toshiba.instagramclone.Utils.ViewPostFragment;
import com.example.toshiba.instagramclone.Utils.ViewProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener ,
         ViewPostFragment.OnCommentThreadSelectedListener,
            ViewProfileFragment.OnGridImageSelectedListener{
    private static final String TAG = "ProfileActivity";

    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container1,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragmnet));
        transaction.commit();
    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_number),activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container1,fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressBar;
    private static final int ACTIVITY_NUM = 4;
    private ImageView profilePhoto;
    private static final int NUM_GRID_COLUMNS = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG,"onCreate: started,");

        init();
      /*  setupActivityWidgets();
        setupBottomNavigationView();
        setupToolbar();
        setProfileImage();

        tempGridSetup();*/
    }

    private void init(){
        Log.d(TAG, "init: inflating" + getString(R.string.profile_fragment));
        
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            
            if(intent.hasExtra(getString(R.string.intent_user))){
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: searching for user object attached as intent extra");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container1,fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();
                }
                else{
                    Log.d(TAG, "init: inflating profile");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container1, fragment);
                    transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();
                }

            }else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }
            
        }else{
            Log.d(TAG, "init: inflating profile");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container1, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();    
        }

        
    }


}
