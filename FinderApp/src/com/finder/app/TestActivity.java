package com.finder.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class TestActivity extends Activity implements View.OnClickListener {

	private final String USER_AGENT = "Mozilla/5.0";
	Button btn;
	EditText info;
	String t = "";
	String returnedData = "";
	TextView v1, v2;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
		      if (bundle != null) {
		          String string = bundle.getString(DownloadService.RESULT);
		          v2.setText(string);
		      }
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//locks screen to portrait view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity_layout);
		InitialiseElements();
	}
	
	public void InitialiseElements()
	{
		info = (EditText)findViewById(R.id.editText1);
		v1 = (TextView)findViewById(R.id.textView1);
		v2 = (TextView)findViewById(R.id.textView2);
		btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(this);
		Intent intent = new Intent(this, DownloadService.class);
		startService(intent);
	}
	
	public void GetData() throws Exception {
		
		new Thread(new Runnable() {
	        public void run() {
	            try {
					returnedData = GET();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            v2.post(new Runnable() {
	                public void run() {
	                    v2.setText(returnedData);
	                }
	            });
	        }
	    }).start();
	}
	
	public void POST() throws Exception {
		
		t = info.getText().toString();
		
            // Defined URL  where to send data
          URL url = new URL("http://3-dot-finder-backend.appspot.com/submit");
             
         // Send POST data request

          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("POST");
          conn.setRequestProperty("User-Agent", USER_AGENT);
  		  conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
  		  conn.setDoOutput(true);
          System.out.println("Test before");
          DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
          wr.writeBytes("text=" + t); 
          wr.flush();
          wr.close();
          
          BufferedReader in = new BufferedReader(
  		        new InputStreamReader(conn.getInputStream()));
  		String inputLine;
  		StringBuffer response = new StringBuffer();
   
  		while ((inputLine = in.readLine()) != null) {
  			response.append(inputLine);
  		}
  		in.close();
    }
	
	public String GET() throws Exception {
		
		URL url = new URL("http://3-dot-finder-backend.appspot.com/get");
		
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("User-Agent", USER_AGENT);
         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line;
         String resultSet = "";
         JSONObject json = new JSONObject(rd.readLine());

         for(int i = 0; i < json.length(); i++)
         {
        	 resultSet += json.get("text" + Integer.toString(i)) + "\n";
         }
         
         while ((line = rd.readLine()) != null) {
           System.out.println("json: " + line);
         }
         rd.close();
         
         return resultSet;
	}
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
	  }
	  @Override
	  protected void onPause() {
	    super.onPause();
	    unregisterReceiver(receiver);
	  }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public void onClick(View view)
	{
		new Thread(new Runnable() {
	        public void run() {
	            try {
					POST();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            v1.post(new Runnable() {
	                public void run() {
	                    v1.setText("Entered");
	                }
	            });
	        }
	    }).start();
	}
}