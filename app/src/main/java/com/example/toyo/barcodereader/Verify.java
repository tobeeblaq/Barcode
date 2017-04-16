package com.example.toyo.barcodereader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;



public class Verify extends AppCompatActivity implements View.OnClickListener {
    private Button declineBtn;
    private Button verifyBtn;
    private TextView firstName,  barCode;
    private String bcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify);

        verifyBtn = (Button) findViewById(R.id.verify);
        declineBtn = (Button) findViewById(R.id.decline);
        firstName = (TextView) findViewById(R.id.fname);

        String path = "";

firstName.setText(getIntent().getStringExtra("fname")+" "+getIntent().getStringExtra("lname")+" "+getIntent().getStringExtra("bcode"));

        String pathx = getIntent().getStringExtra("image");
        new DownloadImageTask((ImageView) findViewById(R.id.picture))

                //  https://static.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg
               .execute("https://static.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg");
        //   .execute("http://truanthunter.azurewebsites.net/"+path);


        verifyBtn.setOnClickListener(this);
        declineBtn.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.verify) {
            BackGround b  = new BackGround();
            b.execute(bcode);
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "Student Registered", Toast.LENGTH_SHORT).show();
            finish();
        }else if(v.getId() == R.id.decline){
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "Declined", Toast.LENGTH_SHORT).show();
            finish();
        }
    }




    class BackGround extends AsyncTask<String, String, String> {
        int byGetOrPost = 1;

        @Override
        protected String doInBackground(String... arg0) {

                try {
                    String studBCode = (String) arg0[0];


                    String link = "http://truanthunter.azurewebsites.net/marker.php";
                    String data = URLEncoder.encode("studBCode", "UTF-8") + "=" +
                            URLEncoder.encode(studBCode, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

        }

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}



