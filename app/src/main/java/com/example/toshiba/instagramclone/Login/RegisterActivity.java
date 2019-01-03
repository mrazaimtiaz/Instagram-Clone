package com.example.toshiba.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.instagramclone.Models.User;
import com.example.toshiba.instagramclone.R;
import com.example.toshiba.instagramclone.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String append = "";


    private Context mContext;
    private String email,username,password;
    private EditText mEmail,mPassword,mUsername;
    private TextView loadingPleaseWait;
    private Button btnRegister;
    private ProgressBar mProgressBar;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: Started.");

        initWidgets();
        setupFirebaseAuth();
        init();

    }
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               email = mEmail.getText().toString();
               password = mPassword.getText().toString();
               username = mUsername.getText().toString();

               if(checkInputs(email,username,password)){
                   mProgressBar.setVisibility(View.VISIBLE);
                   loadingPleaseWait.setVisibility(View.VISIBLE);

                   mFirebaseMethods.registerNewEmail(email,password,username);

               }
            }
        });
    }
    private boolean checkInputs(String email, String username, String password ){
        Log.d(TAG, "checkInputs: checking inputs for null values");
        if(email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    /*
    intialize the widgets
     */
    private void initWidgets(){
        Log.d(TAG, "initWidgets: intilizing widgets");
        mEmail = (EditText) findViewById(R.id.input_email);
        mUsername = (EditText) findViewById(R.id.input_username);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingPleaseWait = (TextView) findViewById(R.id.loadingPleaseWait);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);
    }
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking if string is null");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * *****************************************firebase*********************************************
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");

        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapShot: dataSnapshot.getChildren()){
                    if(singleSnapShot.exists()){
                        Log.d(TAG, "onDataChange: FOUND A MATCH " + singleSnapShot.getValue(User.class).getUsername());

                        append = myRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists. Apending random string to the name:" + append);

                    }
                }
                String mUsername = "";
                mUsername = username + append;

                //add new user to the database
                mFirebaseMethods.addNewUser(email,username,"","","");


                Toast.makeText(mContext, "Signup sucessfull. Sending verification email", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
                Log.d(TAG, "onDataChange: Signup sucessfull. Sending verification email");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

            /**
     * setup up firebase auth object
     */
    /*
    check if the 'user' in logged in
     */



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //user is signed to
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
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

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
