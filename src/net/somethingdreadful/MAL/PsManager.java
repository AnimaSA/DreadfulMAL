package net.somethingdreadful.MAL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PsManager {
	private Context context = null;
	private String animeID = null;
	
	public PsManager(Context c, String id)
	{
		context = c;
		animeID = id;
	}
	
	public PsManager(Context c)
	{
		context = c;
	}
	
	public void setAnimeID(String a)
	{
		animeID = a;
	}
	
	public String getAnime()
	{
		return animeID;
	}
	
	public void setValue(String key, String value )
	{

		FileOutputStream fos;
		try
		{
			fos = context.openFileOutput(animeID + "+" + key, Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setValue(String key, String value, boolean s)
	{
		if (s)
		{
			FileOutputStream fos;
			try
			{
				fos = context.openFileOutput("sync" + animeID + "+" + key, Context.MODE_PRIVATE);
				fos.write(value.getBytes());
				fos.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getValue(String key)
	{
		String value = "";
		
		try 
		{
			FileInputStream fis = context.openFileInput(animeID + "+" + key);
			byte[] input = new byte[fis.available()];
			while (fis.read(input) != -1)
			{
				value += new String(input);
			}
			
			fis.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			
		}
		
		return value;
	}
	
	public String getValue(String key, boolean notExistReturnMode)
	{
		String value = "";
		
		try 
		{
			FileInputStream fis = context.openFileInput("sync" + animeID + "+" + key);
			byte[] input = new byte[fis.available()];
			while (fis.read(input) != -1) 
			{
				value += new String(input);
			}

			fis.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			if (notExistReturnMode)
			{
				value = "not-exist";
			}
			else
			{
				value = getValue(key);
			}
		} 
		catch (IOException e) 
		{

		}

		return value;
	}
	
	public void setPic(Bitmap b)
	{

		FileOutputStream fos;
		try
		{
			fos = context.openFileOutput(animeID + "+pic", Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	public Bitmap getPic()
	{
		Bitmap b = null;
		
		try 
		{
			FileInputStream fis = context.openFileInput(animeID + "+pic");
			b = BitmapFactory.decodeStream(fis);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		return b;
	}
	
	public void removeSyncFile(String key)
	{
		Boolean fileDeleted = context.deleteFile("sync" + animeID + "+" + key);
	}
	
}
