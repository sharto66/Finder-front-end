package com.finder.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Bitmap;

public class ImageList extends Application {
	
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
