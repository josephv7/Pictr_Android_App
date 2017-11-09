package rm.com.microproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class Upload extends AppCompatActivity {


    Button btnLoad,btnCamera,btnSubmit;
    ImageView img;

    private int LOAD_IMAGE = 1;
    private int TAKE_CAMERA = 2;

    private Bitmap bitmap;
    private Uri uriPhoto;


    private int STORAGE_PERMISSION_CODE = 23;
    private int CAMERA_PERMISSION_CODE = 24;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageMetadata metadata;



    Uri uploadUri;


    String userName,imageId;
    //collect with intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btnLoad = findViewById(R.id.btnLoad);
        btnCamera = findViewById(R.id.btnCamera);
        btnSubmit = findViewById(R.id.btnSubmit);
        img = findViewById(R.id.img);




        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userName);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //the problem is caused for android N+
        //another solution is to use file provider.....change file:// to content://



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .setCustomMetadata("User",userName)
                        .setCustomMetadata("ImageId",imageId)
                        .build();


                StorageReference sRef = mStorageRef.child(userName).child(imageId + "__" + uploadUri.getLastPathSegment());

                sRef.putFile(uploadUri,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(Upload.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload.this,"Uh-oh, an error occurred!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });





        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
        });




        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(requestCode == TAKE_CAMERA && resultCode == RESULT_OK) {
            Uri selectedImage = uriPhoto;
            uploadUri = uriPhoto;
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();

            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);

                img.setImageBitmap(bitmap);
                // Toast.makeText(this, selectedImage.toString(),
                // Toast.LENGTH_LONG).show();
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




}
