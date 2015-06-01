package com.finder.app;

import android.graphics.Bitmap;

public class ImageList {
	
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
}
