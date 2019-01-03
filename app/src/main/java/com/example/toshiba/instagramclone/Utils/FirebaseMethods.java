package com.example.toshiba.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.toshiba.instagramclone.Home.HomeActivity;
import com.example.toshiba.instagramclone.Models.Photo;
import com.example.toshiba.instagramclone.Models.User;
import com.example.toshiba.instagramclone.Models.UserAccountSettings;
import com.example.toshiba.instagramclone.Models.UserSettings;
import com.example.toshiba.instagramclone.Profile.AccountSettingsActivity;
import com.example.toshiba.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
       mFirebaseDatabase = FirebaseDatabase.getInstance();
       mStorageReference = FirebaseStorage.getInstance().getReference();
        myRef = mFirebaseDatabase.getReference();
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }
    public void uploadNewPhoto(String photoType, final String caption,final int count, final String imgUrl,Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        final FilePaths filePaths = new FilePaths();
        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if(bm == null)
            {
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getByteFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  // Uri firebaseUrl = taskSnapshot.g;



                    mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            addPhotoToDatabase(caption, task.getResult().toString());
                        }
                    });
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();




                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");



            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if(bm == null)
            {
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getByteFromBitmap(bm,100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(  ) {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            setProfilePhoto(task.getResult().toString());


                        }
                    });

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    //setProfilePhoto(firebaseUrl.toString());

                    ((AccountSettingsActivity)mContext).setViewpager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }


    }


    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile photo " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kuwait"));
        return sdf.format(new Date());
    }
    private void addPhotoToDatabase(String caption,String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to the database.");

        String tags =
                StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();

        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert data into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey)
                .setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }
    public  int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds:
                dataSnapshot
                        .child(mContext.getString(R.string.dbname_user_photos))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .getChildren()){
            count++;
        }
        return count;
    }
    /**
     * update the 'useraccountsettings'  node for the current user
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName,String website,String description,long phoneNumber){

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if(displayName != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if(website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }
        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }
        if(phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
}
    /**
     * update username in the 'user' and 'user_account_settings' node
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username to " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

    }

    /**
     * update email in the 'user's'' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateUsername: updating email to " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);


    }
   /* public boolean checkIfUserNameExist(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkIfUserNameExist: if " + username + "already exists.");

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUserNameExist: datasnapshots: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());

            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfUserNameExist: Found a match" + user.getUsername());
                return true;
            }
        }
       return false;
    }*/
    /**
     * register a new email and password to firebase Authenication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password,final String username){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //send verification email
                            sendVerificationEmail();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success" + userID);
                            userID =  mAuth.getCurrentUser().getUid();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }
     public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){

                          }else {
                              Toast.makeText(mContext, "couldn't send verification email", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
        }
     }
    /**
     * Add the user information to the user
     * Add the user_accont_settings node
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void addNewUser(String email,String username,String description,String website,String profile_photo){

        User user = new User(userID, 1, email, StringManipulation.condenseUsername(username));

        myRef.child(mContext.getString(R.string.dbname_users))
        .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website,
                userID
        );
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);
    }

    /**
     * Retrieves the account settings for the user currently logged in
     * Database: user_acccount_settings null
     * @param dataSnapshot
     * @return
     */

    public UserSettings getUserAccountSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retreiving user account settings from database");

        UserAccountSettings settings = new UserAccountSettings();

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            //user account_settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))){

                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds);

                try {

                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()

                    );

                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()

                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()

                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()

                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()

                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()

                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()

                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()

                    );
                    Log.d(TAG, "getUserAccountSettings: retrieving user account settings" + settings.toString());
                }

                catch (NullPointerException e){
                    Log.e(TAG, "getUserAccountSettings: Null pointer exception , " + e.getMessage() );
                }

            }
            //user account_settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))) {

                try {

                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds);

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()

                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()

                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()

                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()

                );
                Log.d(TAG, "user: retrieving user" + user.toString());

            }

                catch (NullPointerException e){
                Log.e(TAG, "user: retrieving users: Null pointer exception , " + e.getMessage() );
            }


        }
        }
        return  new UserSettings(user,settings);
    }
}





















