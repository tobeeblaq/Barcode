package com.example.toyo.barcodereader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    String info = "hello;i;iam;here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(this);
    }

    public void onClick(View v){

        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
          //  formatTxt.setText("FORMAT: " + scanFormat);
          //  contentTxt.setText("CONTENT: " + scanContent);
            BackGround b = new BackGround();
            b.execute(scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    class BackGround extends AsyncTask<String, String, String> {
        int byGetOrPost = 1;

        @Override
        protected String doInBackground(String... arg0) {

                try {
                    String studBCode = (String) arg0[0];


                    String link = "http://truanthunter.azurewebsites.net/tester.php";
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

                  //  Toast.makeText(getBaseContext(), sb.toString(), Toast.LENGTH_LONG).show();

                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

        }
        protected void onPostExecute(String s){

            String err=null;

            String IMAGE="";
            String FNAME="";
            String LNAME="";
            String BCODE="";
            try {
                JSONObject root = new JSONObject(s);
                JSONObject user_data = root.getJSONObject("user");
                IMAGE = user_data.getString("image");
                FNAME = user_data.getString("firstname");
                LNAME = user_data.getString("lastname");
                BCODE = user_data.getString("student");
            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: "+e.getMessage();
            }

            Intent i = new Intent(getBaseContext(), Verify.class);
            i.putExtra("image", IMAGE);
            i.putExtra("fname", FNAME);
            i.putExtra("lname", LNAME);
            i.putExtra("bcode", BCODE);

            startActivity(i);
        }
    }



    }
