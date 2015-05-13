package com.finder.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {
	
	private int result = Activity.RESULT_CANCELED;
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.finder.app.TestActivity";
	
	public DownloadService() {
	    super("DownloadService");
	  }
	
	  @Override
	  protected void onHandleIntent(Intent intent) {
			
		  try{
			  System.out.println("First line of download");
			URL url = new URL("http://3-dot-finder-backend.appspot.com/get");
			
	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         String resultSet = "";
	         JSONObject json = new JSONObject(rd.readLine());
	
	         for(int i = 0; i < json.length(); i++)
	         {
	        	 resultSet += json.get("text" + Integer.toString(i)) + "\n";
	         }
	         System.out.println("Last line of download");
	         publishResults(resultSet);
		  }
		  catch(Exception e)
		  {
			  System.out.println("Error in Service");
		  }
		}
	  
	  private void publishResults(String result)
	  {
			Intent intent = new Intent(NOTIFICATION);
			intent.putExtra(RESULT, result);
			sendBroadcast(intent);
	  }

}
