package com.example.root.asynctask;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockContentProvider;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    String [] permissions ={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean permissionCheck =true;
    static final int REQUEST_PERMISSION =1;

    Button downloadBtn;
    ImageView image;
    String url ="https://i.pinimg.com/736x/92/ee/e5/92eee5389e101397c2011fed3cf6f15d--samsung-wallpaper-android-wallpapers-phone.jpg";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) != MockPackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, permissions[1]) != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

                permissionCheck =true;
            }

            else{
                permissionCheck=false;
            }
        }

        catch(Exception e){

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSION){
            if(grantResults.length==2 && grantResults[0]==MockPackageManager.PERMISSION_GRANTED
                    && grantResults[1]== MockPackageManager.PERMISSION_GRANTED){
                permissionCheck =true;
            }
            else{
                permissionCheck=false;
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadBtn =(Button)findViewById(R.id.btnDownload);
        image =(ImageView)findViewById(R.id.image);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Downloading File");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MyDownloadClass().execute(url);
            }
        });
    }

    private class MyDownloadClass extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            Toast.makeText(MainActivity.this, "Download Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String myUrl =strings[0];
            try{
            URL imageUrl = new URL(myUrl);
            URLConnection connection = imageUrl.openConnection();
            connection.connect();

            //gettinf file size
            int filelength = connection.getContentLength();
            InputStream input = new BufferedInputStream(imageUrl.openStream(),8192);
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Download/myfile.jpg");

            byte[] data = new byte[1024];
            long downloadedSize=0;
            double count;
            while ((count=input.read(data))!=0){
                downloadedSize =(long)(downloadedSize+count);
                output.write(data,0,(int)count);
                publishProgress((int)downloadedSize*100/filelength);

            }
            output.close();
            input.close();
            }
            catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            String filepath =Environment.getExternalStorageDirectory()+"/Download/myfile.jpg";
            image.setImageDrawable(Drawable.createFromPath(filepath));
        }
    }
}
