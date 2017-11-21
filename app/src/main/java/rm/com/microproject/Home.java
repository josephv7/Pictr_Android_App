package rm.com.microproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class Home extends AppCompatActivity {


    Button upload,view,logout;

    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    int inputFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = findViewById(R.id.upload);
        view = findViewById(R.id.view);
        logout = findViewById(R.id.logout);


        dbHandler = new DBHandler(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        builder = new MaterialDialog.Builder(Home.this)
                .title("Upload Image")
                .content("Enter Your Choice")
                .positiveText("Storage")
                .negativeText("Cancel")
                .neutralText("Camera")
                .cancelable(false)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRangeRes(1,20,R.color.errorColour)
                .input("Image Id", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            if(!input.toString().equals("")){
                                Log.d("input",input.toString());
                                dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            }else if (input.toString().equals("")){
                                dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("positive clicked","............");
//                        Log.d("which",which.toString());
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("negative clicked","............");
                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("negative neutral","............");
                    }
                });


        dialog = builder.build();





        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent uploadIntent = new Intent(Home.this,Upload.class);
//                startActivity(uploadIntent);


                dialog.show();

                dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);



            }
        });



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(Home.this, DisplayImages.class);
                startActivity(viewIntent);
                finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.onDrop();
                editor.putBoolean("loggedIn",false);
                Intent goToLogin = new Intent(Home.this,LoginActivity.class);
                startActivity(goToLogin);
                finish();

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();
    }
}
