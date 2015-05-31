package com.finder.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadService extends IntentService {
	
	private int result = Activity.RESULT_CANCELED;
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.finder.app.TestActivity";
	
	public DownloadService() {
	    super("DownloadService");
	  }
	
	  @Override
	  protected void onHandleIntent(Intent intent){
			
		    Bitmap bitmap = null;
	        InputStream iStream = null;
	        try
	        {
	            URL url = new URL("http://4-dot-finder-backend.appspot.com/serveimages");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoInput(true);
	            iStream = conn.getInputStream();
	            bitmap = BitmapFactory.decodeStream(iStream);
	            System.out.println("Test before");
	            //((ImageList) this.getApplication()).imageList.add(bitmap);
	            System.out.println("Test after");
	            int responseCode = conn.getResponseCode();
	            System.out.println(Integer.toString(responseCode));
	            publishResults();
	        }
	        catch(Exception e)
	        {
	            Log.d("Exception while downloading url", e.toString());
	        }
	        finally
	        {
	            try {
					iStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
	  
	  private void publishResults()
	  {
			Intent intent = new Intent(NOTIFICATION);
			intent.putExtra(RESULT, "OK");
			sendBroadcast(intent);
	  }

}
