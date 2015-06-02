package com.finder.app;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ImagesPageFragment extends Fragment implements View.OnClickListener {
	
	static List<ImageList> imageList = new ArrayList<ImageList>();
	public final static int AMOUNT = 10;
	public final static int MOD = 4;
	int imgCount = 0;
	
	Button btn;
	ImageView image;
	TextView text;
	Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_image_screen, container, false);
        InitElements(v);
        if(checkConnection())
        {
        	InitDownload();
        }
        return v;
    }
    
    public void InitElements(View v)
    {
    	btn = (Button) v.findViewById(R.id.nextButton);
    	btn.setOnClickListener(this);
    	image = (ImageView) v.findViewById(R.id.imageDisplay);
    	text = (TextView) v.findViewById(R.id.imageLabel);
    }
    
    public void InitDownload()
    {
    	btn.setEnabled(false);
    	for(int i = 0; i < AMOUNT; i++)
    	{
	    	new Thread(new Runnable() {
		        public void run() {
		            try {
		            	getImage();
					} catch (Exception e) {
						e.printStackTrace();
						}
		            image.post(new Runnable() {
		            	public void run() {
		            		btn.setEnabled(true);
		            		image.setImageBitmap(imageList.get(0).image);
		            	}
		            });
			        }
			    }).start();
    	}
    }
    
    public boolean checkConnection()
    {
    	ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
    	return isConnected;
    }
    
	public void getImage() throws Exception
	{
		Bitmap bitmap = null;
        InputStream iStream = null;
        try{
            URL url = new URL("http://4-dot-finder-backend.appspot.com/serveimages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            String name = conn.getHeaderField("Content-Disposition");
            iStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(iStream);
            int responseCode = conn.getResponseCode();
            System.out.println(Integer.toString(responseCode));
            imageList.add(new ImageList(bitmap, name));
            System.out.println("List size: " + Integer.toString(imageList.size()));
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            //iStream.close();
        }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.nextButton :
				if(imgCount == MOD && checkConnection())
				{
					for(int i = 0; i < AMOUNT; i++)
					{
						new Thread(new Runnable() {
					        public void run() {
					            try {
					            	getImage();
								} catch (Exception e) {
									e.printStackTrace();
									}
						        }
						    }).start();
					}
					imgCount = 0;
				}
				if(checkConnection())
				{
					System.out.println("Before: " + Integer.toString(imageList.size()));
					imageList.remove(0);
					System.out.println("After: " + Integer.toString(imageList.size()));
					image.setImageBitmap(imageList.get(0).image);
					imgCount++;
				}
				while(imageList.size() > 15)
				{
					imageList.remove(imageList.size() - 1);
				}
				break;
		}
	}
}
