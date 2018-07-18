package com.example.shivanikoul.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final int RC_PIX = 101;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );
//   authenticator

    private FirebaseAuth firebaseAuth;
    //    user
    private FirebaseUser user;

    //    auth state listener
    private FirebaseAuth.AuthStateListener authStateListener;

    //    Realtime database
    FirebaseDatabase database;
//    reference database

    DatabaseReference userRef, postRef;
    //    firebase storage
    FirebaseStorage storage;
    //    storage ref
    StorageReference storageReference, imageReference;

    RecyclerView recyclerView;



//    private Button postBtn;
    private ImageView postBtn;
    private ImageView logo,icon,film,beauty,news1;
    postAdapter adapter;
    ArrayList<PostModel>postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postBtn = findViewById(R.id.postBtn);
        icon =findViewById(R.id.icon);
        film =findViewById(R.id.film);
        beauty =findViewById(R.id.beauty);
        news1 =findViewById(R.id.news1);

        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter =new postAdapter(this);
        recyclerView.setAdapter(adapter);

        postList=new ArrayList<>();



//      user instance
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

//        user reference
        userRef = database.getReference("users");
        postRef = database.getReference("posts");
//        reference of storage
        storageReference = storage.getReference();

        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PostModel post =dataSnapshot.getValue(PostModel.class);
                postList.add(post);
                adapter.swap(postList);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();

                if (user != null) {
//                        email.setText(user.getEmail());

//                        /get user detail
                    String id = user.getUid();
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    String imgUrl = user.getPhotoUrl().toString();
//                        crete model

                    UserModel userModel = new UserModel(id, name, email, imgUrl);
//                        add firebase database
                    userRef.child(userModel.getId()).setValue(userModel);
//                        signed in

                } else {
//                        not signed in
                    startSignIn();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                Toast.makeText(MainActivity.this, response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                startSignIn();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == RC_PIX) {
            ArrayList<String> images = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            uploadPost(images.get(0));
        }
    }

    private void uploadPost(String imageLocation) {
        Toast.makeText(MainActivity.this, "Processing is finished", Toast.LENGTH_SHORT).show();

        Uri file = Uri.fromFile(new File(imageLocation));
        imageReference = storageReference.child(file.getLastPathSegment());
        UploadTask uploadTask = imageReference.putFile(file);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    Toast.makeText(MainActivity.this, "Processing Finished", Toast.LENGTH_SHORT).show();

                    String userName = user.getDisplayName();
                    String userImgUrl = user.getPhotoUrl().toString();
                    String userEmail = user.getEmail();
                    String postImgUrl = downloadUri.toString();
                    String postTime = String.valueOf(System.currentTimeMillis() / 1000);

                    PostModel postModel = new PostModel(userName,userImgUrl,userEmail,postImgUrl,postTime);
                    postRef.push().setValue(postModel);
                    Toast.makeText(MainActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                }

            }


        });
    }

    public void startstory(View view){
        switch (view.getId()){
            case R.id.logo:
                startIntent(0);
                break;
            case R.id.icon:
                startIntent(1);
                break;
            case R.id.film:
                startIntent(2);
                break;
            case R.id.beauty:
                startIntent(3);
                break;
            case R.id.news1:
                startIntent(4);
                break;

        }
    }

    private void startIntent(int i) {
        Intent intent = new Intent(MainActivity.this,Story.class);
        intent.putExtra("position",i);
        startActivity(intent);
    }


    private void startSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);


    }


    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                startSignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void startSignOut() {
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Successfully signout", Toast.LENGTH_SHORT).show();
                    }

                    public void post(View view) {
                        Pix.start(MainActivity.this, RC_PIX);
                    }
                });

    }

    public void post(View view) {
        Pix.start(MainActivity.this,RC_PIX,1);
    }

    public void logout(View view) {

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this,Splash.class);
                        startSignOut();
                        startActivity(intent);

                    }
                });
    }
}


