package rm.com.microproject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {


    Button upload,view,logout;

    DBHandler dbHandler;
    ImageDetails imageDetails;
    List<ImageDetails> imageList;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    MaterialDialog.Builder builder1,builder2;
    MaterialDialog dialog,cancelDialog;

    int flag = 0;

    String userName,imageId;


    private Uri uriPhoto;
    Uri uploadUri;
    private Bitmap bitmap;



    private int STORAGE_PERMISSION_CODE = 23;
    private int CAMERA_PERMISSION_CODE = 24;

    private int LOAD_IMAGE = 1;
    private int TAKE_CAMERA = 2;
    private int CHECK_IMAGE = 3;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageMetadata metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = findViewById(R.id.upload);
        view = findViewById(R.id.view);
        logout = findViewById(R.id.logout);




        dbHandler = new DBHandler(this);
        imageList = new ArrayList<ImageDetails>();
        imageList = dbHandler.getAllImages();

        Log.d("tirgger value",Integer.toString(dbHandler.getImageCountFromTable()));

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName",null);
        editor = sharedPreferences.edit();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userName);



        StrictMode.VmPolicy.Builder builderStrict = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderStrict.build());


        builder2 = new MaterialDialog.Builder(Home.this)
                .title("Logout")
                .content("Are You Sure You Want To Logout?")
                .positiveText("Logout")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dbHandler.onDrop();
                        editor.putBoolean("loggedIn",false);
                        cancelDialog.dismiss();
                        Intent goToLogin = new Intent(Home.this,LoginActivity.class);
                        startActivity(goToLogin);
                        finish();
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancelDialog.dismiss();
                    }
                });

        cancelDialog = builder2.build();



        builder1 = new MaterialDialog.Builder(Home.this)
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


                            Log.d("inside","checking duplicate");

                            imageId = input.toString();

                            flag = 0;
                            //check need here
                            for (int i = 0;i < imageList.size();i++) {
                                if (input.toString().equals(imageList.get(i).getImageId())){

                                    flag = 1;
                                    Log.d("Found","image in db");
                                    break;
                                }

                            }


//                                metadata = new StorageMetadata.Builder()
//                                        .setContentType("image/jpg")
//                                        .setCustomMetadata("User",userName)
//                                        .setCustomMetadata("ImageId",imageId)
//                                        .build();
//


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

                        if (flag == 1){
                            dialog.dismiss();
                            showSnackBar();
                        }else{
                            Log.d("positive else",".......");




                            metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg")
                                    .setCustomMetadata("User",userName)
                                    .setCustomMetadata("ImageId",imageId)
                                    .build();








                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                if (isReadStorageAllowed()) {
                                    showFileChooser();
                                    return;

                                }

                                requestStoragePermission();

                            }else{
                                showFileChooser();
                            }



                        }







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
                        if(flag == 1){
                            dialog.dismiss();
                            showSnackBar();
                        }else {


                            metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg")
                                    .setCustomMetadata("User",userName)
                                    .setCustomMetadata("ImageId",imageId)
                                    .build();


                            Log.d("Negative else",".........");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if (isCameraAllowed()){
                                    openCamera();
                                    return;
                                }
                                requestCamera();
                            }else {
                                openCamera();
                            }
                        }
                    }
                });


        dialog = builder1.build();





        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent uploadIntent = new Intent(Home.this,Upload.class);
//                startActivity(uploadIntent);

                flag = 0;


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
//                dbHandler.onDrop();
//                editor.putBoolean("loggedIn",false);
//                Intent goToLogin = new Intent(Home.this,LoginActivity.class);
//                startActivity(goToLogin);
//                finish();

                cancelDialog.show();

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();
    }


    void showSnackBar(){
        Snackbar.make(findViewById(R.id.rootView),"Id Already Exists",Snackbar.LENGTH_SHORT).show();
    }



    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),LOAD_IMAGE);
    }




    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        uriPhoto = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_CAMERA);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                writeToFile(BitMapToString(bitmap),Home.this);
                Intent showImage = new Intent(Home.this,ShowSelected.class);
                //showImage.putExtra("imageBitmap",BitMapToString(bitmap));
                startActivityForResult(showImage,CHECK_IMAGE);
//                img.setImageBitmap(bitmap);
                //dont show here ...show in a different activity


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == CHECK_IMAGE){
            if (resultCode == RESULT_OK){
                uploadImage();

            }else if(resultCode == RESULT_CANCELED){
                /////
            }
        }


        if(requestCode == TAKE_CAMERA && resultCode == RESULT_OK) {
            Uri selectedImage = uriPhoto;
            uploadUri = uriPhoto;
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();

            uploadImage();

            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);

                //img.setImageBitmap(bitmap);
                //not showing image as already shown in camera view
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("Camera", e.toString());
            }
        }

    }





    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }



    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted.Click Selfie Again.",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }



        if(requestCode == CAMERA_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted.Click Selfie Again.",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }



    //////////




    //We are calling this method to check the permission status
    private boolean isCameraAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }



    //Requesting permission
    private void requestCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
    }


    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public void uploadImage(){


        StorageReference sRef = mStorageRef.child(userName).child(imageId);

        sRef.putFile(uploadUri,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(Home.this, "Image Uploaded", Toast.LENGTH_SHORT).show();


//                                if (flag == 0) {
                imageDetails = new ImageDetails(imageId,taskSnapshot.getDownloadUrl().toString());
                dbHandler.addImage(imageDetails, userName);

                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(imageDetails);

//                                }
                //extra safety ? :p

//                startActivity(backIntent);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this,"Uh-oh, an error occurred!",Toast.LENGTH_LONG).show();
                Log.d("onFaliure",e.toString());
            }
        });



    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



}
