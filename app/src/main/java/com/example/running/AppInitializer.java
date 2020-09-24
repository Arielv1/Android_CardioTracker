package com.example.running;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AppInitializer extends Application {

    private static final String TAG = "AppInitializer";

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        Toaster.init(this);
        MySP.init(this);

        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated...//
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase authentication Successful sign in");
                } else {
                    //If sign in fails, then log the error//
                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
    }
}
