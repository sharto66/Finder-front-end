package com.finder.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements View.OnClickListener {

	private final String USER_AGENT = "Mozilla/5.0";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Uri fileUri;
	Button submitBtn;
	Button CameraBtn;
	ImageView img;
	EditText info;
	String t = "";
	String returnedData = "";
	TextView v1, v2;
	static String camImage;
	Bitmap bitmap = null;
	
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
		img = (ImageView) findViewById(R.id.imageDownload);
		submitBtn = (Button)findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
		CameraBtn = (Button)findViewById(R.id.internal_camera);
		CameraBtn.setOnClickListener(this);
//		Intent intent = new Intent(this, DownloadService.class);
//		startService(intent);
		new Thread(new Runnable() {
	        public void run() {
	            try {
					bitmap = GET();
				} catch (Exception e) {
					e.printStackTrace();
					}
	            img.post(new Runnable() {
	            	 public void run() {
	            	img.setImageBitmap(bitmap);
	            	Toast.makeText(getBaseContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
	            	}
	            	});
		        }
		    }).start();
	}
	
	public void POST() throws Exception {
		
		t = info.getText().toString();
        URL url = new URL("http://4-dot-finder-backend.appspot.com/submit");

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
          
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  		String inputLine;
  		StringBuffer response = new StringBuffer();
   
  		while ((inputLine = in.readLine()) != null) {
  			response.append(inputLine);
  		}
  		in.close();
    }
	
	public void postImage() throws Exception {
		
		  File image = new File(camImage.toString());
		  String crlf = "\r\n";
		  String twoHyphens = "--";
		  String boundary =  "********";
		  
		  URL url = new URL("http://4-dot-finder-backend.appspot.com/postimage");
		
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		  conn.setRequestMethod("POST");
		  conn.setDoOutput(true);
		  conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		  DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		  wr.writeBytes(twoHyphens + boundary + crlf);
		  wr.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + image.getName() + "\"" + crlf);
		  wr.writeBytes(crlf);
		  
		  FileInputStream fis = null;
		  try {
			  fis = new FileInputStream(image);
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		
		  Bitmap bm = BitmapFactory.decodeStream(fis);
		  ByteArrayOutputStream stream = new ByteArrayOutputStream();
		  bm.compress(CompressFormat.JPEG, 70, stream);
		  
		  wr.write(stream.toByteArray());
		  wr.writeBytes(crlf);
		  wr.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		  wr.flush();
		  wr.close();
		  
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	  		String inputLine;
	   
	  		while ((inputLine = in.readLine()) != null) {
	  			System.out.println(inputLine);
	  		}
	  		in.close();
			  
			int responseCode = ((HttpURLConnection) conn).getResponseCode();
			System.out.println(responseCode);
			
			System.out.println(image.delete());
    }
	
	public Bitmap GET() throws Exception {
		
		Bitmap bitmap = null;
        InputStream iStream = null;
        try{
            URL url = new URL("http://3-dot-finder-backend.appspot.com/serve?img-key=wut.jpg");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            iStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(iStream);
            System.out.println("Test here");
            int responseCode = conn.getResponseCode();
            System.out.println(Integer.toString(responseCode));
            System.out.println("After bitmap");
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
        }
        return bitmap;
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
	  
	  private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	  }

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "Finder App");
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    }
	    else {
	        return null;
	    }

	    camImage = mediaFile.getAbsolutePath();
	    System.out.println(camImage);
	    
	    return mediaFile;
	}
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, final Intent data){

	      new Thread(new Runnable() {
		        public void run() {
		            try {
						postImage();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    }).start();
	      
	  }
	  
	@Override
	public void onClick(View view)
	{
		if(view.getId() == R.id.internal_camera) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);  // create a file to save the video
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
		if(view.getId() == R.id.submit) {
			new Thread(new Runnable() {
		        public void run() {
		            try {
						POST();
					} catch (Exception e) {
						e.printStackTrace();
						}
			        }
			    }).start();
		}
	}
}