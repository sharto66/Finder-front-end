package com.finder.app;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageList implements Parcelable {
	
	public Bitmap image;
	public String name;
	
	public ImageList()
	{
		this.image = null;
		this.name = "";
	}
	
	public ImageList(Bitmap image, String name)
	{
		this.image = image;
		this.name = name;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
