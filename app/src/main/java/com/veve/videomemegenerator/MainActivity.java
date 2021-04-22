package com.veve.videomemegenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button mCreateNewMeme;
    public static final int PERMISSION_REQUEST = 1;
    public static final int MY_REQUEST_CODE = 200;
    private Uri mSelectedUri;
    private ArrayList<String> mVideoCursorArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //delete any extra files
        File folder = new File(Environment.getExternalStorageDirectory()+"/Veve");

        if(!folder.exists()){
            folder.mkdir();
        }

        if(new File(folder,"temp.mp4").exists()){
            new File(folder,"temp.mp4").delete();
        }

        if(new File(folder,"resizedtemp.mp4").exists()){
            new File(folder,"resizedtemp.mp4").delete();
        }

        if(new File(folder,"temp.png").exists()){
            new File(folder,"temp.png").delete();
        }

        mCreateNewMeme = findViewById(R.id.btnCreateMeme);
        //permission handling
        permissionHandling();

        mCreateNewMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open up the external storage
                filePickHandling();
            }
        });
    }



    private void filePickHandling() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent,MY_REQUEST_CODE);
    }

    private void permissionHandling() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //request permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }else{
                //request permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //request permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }else{
                //request permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK){
            mSelectedUri = data.getData();

            Intent intent = new Intent(this,VeveActivity.class);
            intent.putExtra("Uri",mSelectedUri.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this,"permission denied!",Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
}
