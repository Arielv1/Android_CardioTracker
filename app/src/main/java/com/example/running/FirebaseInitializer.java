package com.example.running;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInitializer {

    private static final String TAG = "MyFB";
    private static FirebaseInitializer instance;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    public static FirebaseInitializer getInstance() {
        return instance;
    }

    private FirebaseInitializer(final Context context, String email, String password) {

        FirebaseApp.initializeApp(context);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated...//
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase authentication Successful sign in");
                } else {
                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
        database = FirebaseDatabase.getInstance();
    }

    public static FirebaseInitializer init(Context context, String email, String password) {
        if (instance == null)
            instance = new FirebaseInitializer(context, email, password);
        return instance;
    }
}
