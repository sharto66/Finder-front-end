package com.finder.app;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImagesPageFragment extends Fragment implements View.OnClickListener {
	
	List<Bitmap> imageList = new ArrayList<Bitmap>();
	int count;
	int imgCount = 0;
	
	Button btn;
	ImageView image;
	TextView text;
	Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.fragment_image_screen, container, false);
        InitElements(v);
	    InitDownload();
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
    	for(int i = 0; i < 5; i++)
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
		            		//image.setImageBitmap(imageList.get(0));
		            	}
		            });
			        }
			    }).start();
    	}
    }
    
	public void getImage() throws Exception
	{
		Bitmap bitmap = null;
        InputStream iStream = null;
        try{
            URL url = new URL("http://4-dot-finder-backend.appspot.com/serveimages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            iStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(iStream);
            int responseCode = conn.getResponseCode();
            System.out.println(Integer.toString(responseCode));
            imageList.add(bitmap);
            System.out.println("List size: " + Integer.toString(imageList.size()));
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
        }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.nextButton :
				//btn.setEnabled(false);
				if(imgCount % 3 == 0)
				{
					for(int i = 0; i < 5; i++)
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
				}
				imgCount++;
				image.setImageBitmap(imageList.get(imgCount));
				break;
		}
	}

}
