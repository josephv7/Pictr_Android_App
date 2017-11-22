package rm.com.microproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    String n, p, url;
    private List<ImageDetails> imageArray;
    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    MaterialDialog dialog;

    Boolean checkTriggerCreation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();







        checkTriggerCreation = sharedPreferences.getBoolean("firstTimeInLogin",true);
        editor.putBoolean("firstTimeInLogin",false);



        dbHandler = new DBHandler(this);
        dbHandler.createTable();
//        if(checkTriggerCreation){
//
//            Log.d("making trigger","trigger creation");
//            dbHandler.makeTrigger();
//        }



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("UserCredential");
        final DatabaseReference rootRef = database.getReference();




        imageArray = new ArrayList<ImageDetails>();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n = username.getText().toString().toLowerCase();
                p = password.getText().toString();


                dialog = new MaterialDialog.Builder(LoginActivity.this)
                        .title("Logging You In")
                        .content("Please Wait")
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .cancelable(false)
                        .show();

                if ((!n.equals("") && !p.equals(""))) {


                    ref.child(n).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Username not found!", Toast.LENGTH_SHORT).show();

                            } else {
                                if (dataSnapshot.getValue().toString().equals(p)) {


                                    //adding shared preferences
                                    editor.putString("userName", n);
                                    editor.putBoolean("loggedIn", true);


                                    ///////

                                    //creating trigger here
                                    Log.d("making trigger","trigger creation");
                                    dbHandler.makeTrigger();

                                    readData(rootRef.child(n), new OnGetDataListener() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {


                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                Log.d("here", "...........");

                                                ImageDetails imageDetails = snapshot.getValue(ImageDetails.class);
                                                imageArray.add(imageDetails);


                                            }


                                            gotImageUrl(n);


                                        }

                                        @Override
                                        public void onStart() {

                                            Log.d("ONSTART", "Started");
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.d("onFailure", "Failed");
                                        }
                                    });


                                    /////////


                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Password incorrect!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please enter all credentials!", Toast.LENGTH_SHORT).show();
//                    username.setError("Required");
//                    password.setError("Required");


                }

            }
        });


    }


    private void gotImageUrl(String username) {


        for (int j = 0; j < imageArray.size(); j++) {
            dbHandler.addImage(imageArray.get(j), username);
            Log.d("Sql added", Integer.toString(j));
        }
        int count = dbHandler.getImagesCount();
        Log.d("count", Integer.toString(count));


        //dbHandler.onDrop();
        //Log.d("dropped","..........");


        dialog.dismiss();


        Intent homeIntent = new Intent(LoginActivity.this, Home.class);
        startActivity(homeIntent);
        finish();


    }


    public interface OnGetDataListener {
        //make new interface for call back
        void onSuccess(DataSnapshot dataSnapshot);

        void onStart();

        void onFailure();
    }


    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }


    //remove if any unnecessary

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        editor.commit();
    }
}

