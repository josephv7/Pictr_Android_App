package rm.com.microproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button login;
    String n,p,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("UserCredentials");
        final DatabaseReference rootRef = database.getReference();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n = username.getText().toString().toLowerCase();
                p = password.getText().toString();

                if (!(username.equals("") || password.equals(""))) {




                ref.child(n).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "Username not found!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (dataSnapshot.getValue(String.class).equals(p)) {
                                ///////
                                readData(rootRef.child(n), new OnGetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {



                                        url = dataSnapshot.getValue(String.class);
                                        gotImageUrl();


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
                                Toast.makeText(LoginActivity.this, "Password incorrect!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }else {
                    Toast.makeText(LoginActivity.this, "Please enter all credentials!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void gotImageUrl(){


        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
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



}

