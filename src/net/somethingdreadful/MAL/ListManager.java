package net.somethingdreadful.MAL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


public class ListManager {
	
	private Context context;
	private PsManager psManager;
	
	public ListManager(Context c)
	{
		context = c;
		psManager = new PsManager(context);
	}
	
	public ArrayList<HashMap<String, String>> makeWatchListArray(JSONObject data, String whatList)
	{
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		String mList = whatList;
		String whatStatus = "";

		try 
		{
			// Get the element that holds all of the anime
			JSONArray anime = data.getJSONArray("anime");
			
			// Loop the Array
			for (int i = 0; i < anime.length(); i++) 
			{	
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject a = anime.getJSONObject(i);
				psManager.setAnimeID(a.getString("id"));
				whatStatus = a.getString("watched_status");
				String whatStatusString = whatStatus;
				String watchedEpisodesString = a.getString("watched_episodes");
				String totalEpsString = a.getString("episodes");
				
				int totalEps = Integer.parseInt(totalEpsString);
				
				//The following if statement are overrides for if we have a stored value (which would be more current than the list)
				
				if (totalEps == 0)
				{
					totalEpsString = "unknown";
				}
				if ("plan to watch".equals(whatStatusString))
				{
					whatStatusString = "planning to watch";
				}
				if ("on-hold".equals(whatStatusString))
				{
					whatStatusString = "on hold";
				}
				if (!psManager.getValue("episodesWatched").isEmpty())
				{
					watchedEpisodesString = psManager.getValue("episodesWatched");
				}
				if (!psManager.getValue("episodesTotal").isEmpty())
				{
					totalEpsString = psManager.getValue("episodesTotal");
				}

				if (mList.equalsIgnoreCase(whatStatus))
				{
					System.out.println("found one");
					map.put("id", String.valueOf(i));
					map.put("animeName", a.getString("title"));
					map.put("animeID", a.getString("id"));
					map.put("watch_status", whatStatusString);
					map.put("watched_episodes", watchedEpisodesString);
					map.put("total_episodes", totalEpsString);
					map.put("image_url", a.getString("image_url"));
					mylist.add(map);
				}
				if ("all".equals(mList))
				{
					System.out.println("found one");
					map.put("id", String.valueOf(i));
					map.put("animeName", a.getString("title"));
					map.put("animeID", a.getString("id"));
					map.put("watch_status", whatStatus);
					map.put("watched_episodes", a.getString("watched_episodes"));
					map.put("total_episodes", totalEpsString);
					map.put("image_url", a.getString("image_url"));
					mylist.add(map);
				}
			}
		} 
		catch (JSONException e) 
		{
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		catch (NullPointerException e)
		{
			
		}
		
		return mylist;
	}
	
	public String getDataFromJSON(JSONObject json, String get)
	{
		String sReturn = "";
		
		try 
		{
			sReturn = json.getString(get);
//			System.out.println(sReturn);
			
			if ("episodes".equals(get))
			{
				if ("null".equals(sReturn))
				{
					sReturn = "unknown";
				}
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
//			e.printStackTrace();
			
			sReturn = "unknown";
		}
		
		return sReturn;
	}
	
	public String spinnerPositionToString(int itemID)
	{
		String r = "";
		
		if (itemID == 0)
		{
			r = "all";
		}
		if (itemID == 1)
		{
			r = "watching";
		}
		if (itemID == 2)
		{
			r = "completed";
		}
		if (itemID == 3)
		{
			r = "on-hold";
		}
		if (itemID == 4)
		{
			r = "dropped";
		}
		if (itemID == 5)
		{
			r = "plan to watch";
		}
		
		return r;
	}
	public int spinnerStringToPosition(String itemString)
	{
		int l = 1;
		
		if ("all".equals(itemString))
		{
			l = 0;
		}
		if ("watching".equals(itemString))
		{
			l = 1;
		}
		if ("completed".equals(itemString))
		{
			l = 2;
		}
		if ("on-hold".equals(itemString))
		{
			l = 3;
		}
		if ("dropped".equals(itemString))
		{
			l = 4;
		}
		if ("plan to watch".equals(itemString))
		{
			l = 5;
		}
		
		return l;
	}
	
}
