package com.veve.videomemegenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.veve.videomemegenerator.fragments.edittext_fragment;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.io.FileOutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;


public class VeveActivity extends AppCompatActivity {

    private RangeSeekBar mRangeSeekBar;
    private Button mBtnSaveMeme;
    private ImageView mImageView;
    private VideoView mVideoView;
    private Uri mSelectedUri;
    private boolean mIsPlaying = false;
    private int mDuration;
    private TextView mTextViewRight;
    private TextView mTextViewLeft;
    private File mDestination;
    private String mOriginalPath;
    private String[] mCommand;
    public FFmpeg mFFmpeg;
    private String[] mCommand2;
    private String[] mCommand3;
    private String[] mCommand4;
    public TextView mTextViewMain;
    public EditText mMemeEditText;
    public Button mBtnColorPicker;
    private boolean mViewMainClicked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_veve);

        //get views
        mVideoView = findViewById(R.id.videoView);
        mImageView = findViewById(R.id.imageView);
        mBtnSaveMeme = findViewById(R.id.btn_save);
        mRangeSeekBar = findViewById(R.id.rangeSeekBar);
        mTextViewLeft = findViewById(R.id.textViewLeft);
        mTextViewRight = findViewById(R.id.textViewRight);
        mImageView.setImageResource(R.drawable.ic_pause_black_24dp);
        mTextViewMain = findViewById(R.id.textView);


        //handle which fragments is viewed when
        fragmentHandler();

        //get uri from mainActivity
        getUri();

        //handle on click events
        onClickListeners();


     }

    private void fragmentHandler() {

        //on textView click switch from rangeseekbar fragment to edittext fragment
        mViewMainClicked = false;
        mTextViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_default,new edittext_fragment())
                        .commit();


                //Remove all the views to avoid overlapping
                ConstraintLayout rangeSeekBarView = findViewById(R.id.fragment_default);
                rangeSeekBarView.removeAllViews();

                VeveActivity.this.onPause();

                mMemeEditText = findViewById(R.id.editText);
                mBtnColorPicker = findViewById(R.id.btnColorPicker);

                //set text while typing
                mMemeEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.length() > 0){
                            if((s.length() == 1) || (s.length() == 156)
                            ){
                                Toast.makeText(getApplicationContext(), "You are limited to 4 lines.", Toast.LENGTH_SHORT).show();
                            }

                            if(s.length() < 156){
                                mTextViewMain.setText(s.toString());
                            }

                        }

                        if (s.length() == 0 ){

                            mTextViewMain.setText("");
                        }
                    }
                });

                //color picker
                //set the text color using uor color picker library
                mBtnColorPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ColorPicker colorPicker = new ColorPicker(VeveActivity.this);
                        colorPicker.show();
                        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                mTextViewMain.setTextColor(color);
                            }

                            @Override
                            public void onCancel() {

                            }


                        });
                    }
                });
            }
        });



    }


    private void trimVideo() {

        createMemeTextImage();
        myAsyncClass2 as = new myAsyncClass2();
        as.execute();

    }

    private void createMemeTextImage() {

        View view = findViewById(R.id.memeText);
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);


        //save the bitmap as a png file in my directory
        String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Veve";
        File directory = new File(directoryPath);
        if (!directory.exists()){
            directory.mkdir();
        }

        File file = new File(directoryPath,"temp.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getRealPathFromUri(Context Context, Uri selectedUri) {

        try (Cursor cursor = Context.getContentResolver().query(
                selectedUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void onClickListeners() {
        //on play or pause button click
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsPlaying){
                    mImageView.setImageResource(R.drawable.ic_play);
                    mVideoView.pause();
                    mIsPlaying = false;
                }else{
                    mImageView.setImageResource(R.drawable.ic_pause_black_24dp);
                    mVideoView.start();
                    mIsPlaying = true;
                }
            }
        });

        //on video prepared listener
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mDuration = mp.getDuration()/1000;

                mTextViewLeft.setText("00:00:00");
                mTextViewRight.setText(getTime(mDuration));
                mp.setLooping(true);
                mRangeSeekBar.setRangeValues(0,mDuration);
                mRangeSeekBar.setSelectedMaxValue(mDuration);
                mRangeSeekBar.setSelectedMinValue(0);
                mRangeSeekBar.setEnabled(true);

                mRangeSeekBar.setOnRangeSeekBarChangeListener(
                        new RangeSeekBar.OnRangeSeekBarChangeListener() {
                            @Override
                            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                                int min = Integer.parseInt( minValue.toString())*1000;
                                mVideoView.seekTo(min);

                                mTextViewLeft.setText(getTime((int) bar.getSelectedMinValue()));
                                mTextViewRight.setText(getTime((int) bar.getSelectedMaxValue()));
                            }
                        }
                );
            }
        });


        //trimming the video
        mBtnSaveMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trimVideo();


            }
        });




    }

    @SuppressLint("DefaultLocale")
    private String getTime(int seconds) {

        int hr = seconds/3600;

        int rem = seconds - hr * 3600;

        int mn = rem / 60;

        int sec = rem - mn *60;

        return String.format("%02d",hr) + ":" + String.format("%02d",mn) + ":" + String.format("%02d",sec);
    }

    private String getFirstTime(int seconds){

        seconds = (int) (seconds * 0.001);
        int hr = seconds/3600;

        int rem = seconds - hr * 3600;

        int mn = rem / 60;

        int sec = rem - mn *60;

        return String.format("%02d",hr) + "hr : " + String.format("%02d",mn) + "min : " + String.format("%02d",sec)+"sec";
    }

    private void getUri() {

        Intent intent = getIntent();

        String VideoPath = intent.getStringExtra("Uri");
        mSelectedUri = Uri.parse(VideoPath);
        mIsPlaying = true;

        myAsyncClass as = new myAsyncClass();
        as.execute();

    }


    @SuppressLint("StaticFieldLeak")
   private class myAsyncClass extends AsyncTask<String, String, Void> {

       public custom_progressDialog mProgressDialog;

       @Override
       protected void onPreExecute() {
           super.onPreExecute();

           mProgressDialog = new custom_progressDialog(VeveActivity.this);
           mProgressDialog.show();


           //showing the amount of time left
           int videoLength = MediaPlayer.create(VeveActivity.this, mSelectedUri).getDuration();

           Toast.makeText(VeveActivity.this,"Please wait for : "+getFirstTime(videoLength),Toast.LENGTH_LONG).show();






       }


       @SuppressLint("WrongThread")
       @Override
       protected Void doInBackground(String... strings) {



           File folder = new File(Environment.getExternalStorageDirectory()+"/Veve");

           if(!folder.exists()){
               folder.mkdir();
           }
           //add padding around video
           mCommand3 = new String[]{"-i",Environment.getExternalStorageDirectory()+"/Veve/"+"resizedtemp.mp4",
                   "-filter_complex","[0]pad=w=40+iw:h=300+ih:x=20:y=280:color=white",new File(folder,"temp.mp4").getAbsolutePath()
           };


           //setting the video to a certain size
           mCommand4 = new String[]{"-i",getRealPathFromUri(getApplicationContext(),mSelectedUri),
                   "-vf","scale=720:480",new File(folder,"resizedtemp.mp4").getAbsolutePath()
           };

           FFmpeg.execute(mCommand4);

           if(FFmpeg.getLastReturnCode() == FFmpeg.RETURN_CODE_SUCCESS){
               FFmpeg.execute(mCommand3);
               new File(folder,"resizedtemp.mp4").delete();
           }


           return null;
       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);

           mProgressDialog.cancel();
           mVideoView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory()+"/Veve/temp.mp4"));
           mVideoView.start();

       }
   }


    @SuppressLint("StaticFieldLeak")
    private class myAsyncClass2 extends AsyncTask<String, String, Void> {

        custom_progressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new custom_progressDialog(VeveActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            //showing the amount of time left
            int videoLength = MediaPlayer.create(VeveActivity.this, mSelectedUri).getDuration();

            Toast.makeText(VeveActivity.this,"Please wait for : "+getFirstTime(videoLength),Toast.LENGTH_LONG).show();

        }


        @Override
        protected Void doInBackground(String... strings) {

            @SuppressLint("WrongThread") int startMs = mRangeSeekBar.getSelectedMinValue().intValue()*1000;
            @SuppressLint("WrongThread") int endMs = mRangeSeekBar.getSelectedMaxValue().intValue()*1000;
            String fileName = "veve_" + System.currentTimeMillis();


            File folder = new File(Environment.getExternalStorageDirectory()+"/Veve");

            if(!folder.exists()){
                folder.mkdir();
            }




            mDestination = new File(folder,fileName+".mp4");
            mOriginalPath = getRealPathFromUri(getApplicationContext(),Uri.parse(Environment.getExternalStorageDirectory()+"/Veve/temp.mp4"));
            mDuration = (endMs-startMs)/1000;



            mCommand2 = new String[]{"-i",Environment.getExternalStorageDirectory()+"/Veve/temp.mp4","-i",folder.toString()+"/temp.png",
                    "-filter_complex","overlay=20:main_h-overlay_h-500",mDestination.getAbsolutePath()};

            File newFileName =  new File(folder, "veve"+System.currentTimeMillis()+".mp4");
            mCommand = new String[]{"-ss",""+startMs/1000,"-y","-i",Environment.getExternalStorageDirectory()+"/Veve/"+fileName+".mp4",
                    "-t",""+mDuration,"-vcodec","mpeg4","-b:v","2097152","-b:a",
                    "48000","-ac","2","-ar","22050",newFileName.getAbsolutePath()};


            FFmpeg.execute(mCommand2);

            if(FFmpeg.getLastReturnCode() == FFmpeg.RETURN_CODE_SUCCESS){
                FFmpeg.execute(mCommand);
                //delete the files we were working on
                new File(folder, fileName + ".mp4").delete();
                new File(folder,"temp.mp4").delete();
                new File(folder,"temp.png").delete();
            }else if(FFmpeg.getLastReturnCode() == FFmpeg.RETURN_CODE_CANCEL){
                Toast.makeText(getApplicationContext(),"cancelled",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
            }





            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.cancel();
           Toast.makeText(VeveActivity.this,"saved to veve folder!",Toast.LENGTH_LONG).show();


        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        File folder = new File(Environment.getExternalStorageDirectory()+"/Veve");

        if(!folder.exists()){
            folder.mkdir();
        }

        if(new File(folder,"temp.mp4").exists()){
            new File(folder,"temp.mp4").delete();
            new File(folder,"temp.png").delete();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();


        File folder = new File(Environment.getExternalStorageDirectory() + "/Veve");

        if (!folder.exists()) {
            folder.mkdir();
        }

        if (!new File(folder, "temp.mp4").exists()) {
            finish();
        }
    }
}
