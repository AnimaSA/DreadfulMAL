package net.somethingdreadful.MAL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor prefeditor;
	private static boolean init;
	private static String user;
	private static String pass;
	private static Context context;
	private static String animelist;
	
	public PrefManager(Context mContext)
	{
		context = mContext;
		prefs = context.getSharedPreferences("prefs", 0);
        prefeditor = prefs.edit();
	}
	
	public boolean getInit()
	{
		init = prefs.getBoolean("init", false);
		
		return init;
	}
	
	public String getUser()
	{
		user = prefs.getString("user", "failed");
		
		return user;
	}
	
	public String getPass()
	{
		pass = prefs.getString("pass", "failed");
		
		return pass;
	}
	
	public void setUser(String newUser)
	{
		prefeditor.putString("user", newUser);
	}
	
	public void setPass(String newPass)
	{
		prefeditor.putString("pass", newPass);
	}
	
	public void setInit(boolean newInit)
	{
		prefeditor.putBoolean("init", newInit);
	}
	
	public static JSONObject getAnimelist() 
	{
		animelist = prefs.getString("al", "");
		JSONObject jsonAL = null;
		try 
		{
			jsonAL = new JSONObject(animelist);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return jsonAL;
	}

	public void setAnimelist(JSONObject animelist) 
	{
		try
		{
			String alString = animelist.toString();
			prefeditor.putString("al", alString);
		}
		catch (NullPointerException e)
		{
			
		}
	}
	
	public long getLastSyncTime()
	{
		long lastsync = 0;
		
		lastsync = prefs.getLong("lastSync", 0);
		
		return lastsync;
	}
	
	public void setLastSyncTime(long lastsync)
	{
		prefeditor.putLong("lastSync", lastsync);
	}
	
	public long getSyncFrequency()
	{
		long syncFrequency = 0;
		
		syncFrequency = Long.parseLong(prefs.getString("syncFrequency", "604800000"));
		
		return syncFrequency;
	}
	
	public String getDefaultList()
	{
		String l = "";
		
		l = prefs.getString("defaultList", "watching");
		
		return l;
	}
	
	public void commitChanges()
	{
		prefeditor.commit();
	}

}
