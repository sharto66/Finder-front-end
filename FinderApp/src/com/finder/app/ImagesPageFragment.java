package com.finder.app;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class ImagesPageFragment extends Fragment implements View.OnClickListener {
	
	List<ImageList> imageList = new ArrayList<ImageList>();
	
	public final static int TOTAL = 10;
	public final static int AMOUNT = 6;
	public final static int MOD = 1;
	
	boolean fullScreen = false;
	
	Button btn;
	ImageView image1;
	ImageView image2;
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
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    public void InitElements(View v)
    {
    	btn = (Button) v.findViewById(R.id.nextButton);
    	btn.setOnClickListener(this);
    	image1 = (ImageView) v.findViewById(R.id.imageDisplay1);
    	image2 = (ImageView) v.findViewById(R.id.imageDisplay2);
    	image1.setOnClickListener(this);
    	image2.setOnClickListener(this);
    }
    
    public void InitDownload()
    {
    	btn.setEnabled(false);
    	for(int i = 0; i < AMOUNT; i++)
    	{
	    	new Thread(new Runnable() {
		        public void run() {
		            try {
		            	imageList.add(getImage());
					} catch (Exception e) {
						e.printStackTrace();
						}
		            image1.post(new Runnable() {
		            	public void run() {
		            		btn.setEnabled(true);
		            		try{
		            			if(imageList.get(0) != null && imageList.get(1) != null) {
		            				image1.setImageBitmap(imageList.get(0).image);
		            				image2.setImageBitmap(imageList.get(1).image);
		            			}
		            		}
		            		catch(Exception e) {
		            			e.printStackTrace();
		            		}
		            	}
		            });
			        }
			    }).start();
    	}
    }
    
    public boolean checkConnection()
    {
    	boolean isConnected = false;
    	try
    	{
    		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        	isConnected = activeNetwork != null && activeNetwork.isConnected();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return isConnected;
    }
    
	public ImageList getImage() throws Exception
	{
		Bitmap bitmap = null;
        InputStream iStream = null;
        ImageList i = null;
        try{
            URL url = new URL("http://4-dot-finder-backend.appspot.com/serveimages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            String name = conn.getHeaderField("Content-Disposition");
            iStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(iStream);
            int responseCode = conn.getResponseCode();
            System.out.println(Integer.toString(responseCode));
            i = new ImageList(bitmap, name);
            System.out.println("List size: " + Integer.toString(imageList.size()));
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            //iStream.close();
        }
        return i;
	}
	
	public void Download()
	{
		for(int i = 0; i < AMOUNT; i++)
		{
			new Thread(new Runnable() {
		        public void run() {
		        	ImageList im = new ImageList();
		            try {
		            	if(imageList.size() < TOTAL) {
		            		im = getImage();
		            		if(imageList.size() < TOTAL) {
		            			imageList.add(im);
		            		}
		            	}
					} catch (Exception e) {
						e.printStackTrace();
						}
			        }
			    }).start();
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
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) 
//	{
//	  savedInstanceState.putParcelableArrayList(key, value);
//
//	  super.onSaveInstanceState(savedInstanceState);
//	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.nextButton :
				if(checkConnection())
				{
					Download();
				}
				if(checkConnection() && imageList.size() > 2)
				{
					System.out.println("test");
					try
					{
						System.out.println("Before: " + Integer.toString(imageList.size()));
						imageList.remove(0);
						imageList.remove(1);
						System.out.println("After: " + Integer.toString(imageList.size()));
						image1.setImageBitmap(imageList.get(0).image);
						image2.setImageBitmap(imageList.get(1).image);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				break;
			case R.id.imageDisplay1 :
				
				break;
			case R.id.imageDisplay2 :
				
				break;
		}
	}
}
