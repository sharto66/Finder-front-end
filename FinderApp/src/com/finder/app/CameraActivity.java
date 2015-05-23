package com.finder.app;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public class CameraActivity extends Fragment implements View.OnClickListener {

	public static final int MEDIA_TYPE_IMAGE = 1;
	Button btn;
	private Camera mCamera;
    private CameraPreview mPreview;
    
    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
        	
        	new Thread(new Runnable() {
		        public void run() {
		            try {
		            	postImage(data);
					} catch (Exception e) {
						e.printStackTrace();
						}
			        }
			}).start();
        	mCamera.startPreview();
        }
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.main, container, false);
        
        btn = (Button) rootView.findViewById(R.id.button_capture);
        btn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        );
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this.getActivity(), mCamera);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return rootView;
    }
    
    public void postImage(byte[] data) throws Exception {
		
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
		wr.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "\"" + crlf);
		wr.writeBytes(crlf);
		wr.write(data);
		wr.writeBytes(crlf);
		wr.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		wr.flush();
		wr.close();
		  
		int responseCode = ((HttpURLConnection) conn).getResponseCode();
		System.out.println(responseCode);
    }
    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

	@Override
	public void onClick(View v) {
		System.out.println("test camera");
		switch(v.getId())
		{
			case R.id.button_capture :
				mCamera.takePicture(null, null, mPicture);
				break;
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    mCamera.startPreview();
	  }
	  @Override
	public void onPause() {
	    super.onPause();
	    mPreview.stopPreviewAndFreeCamera();
	  }
    
}
