package net.somethingdreadful.MAL;

import android.content.Context;
import android.widget.Toast;

public class SyncManager {
	private Context context;
	private ConnManager cManager;
	private PsManager psManager;
	private String animeID = "";
	
	public SyncManager(Context c, String a)
	{
		context = c;
		animeID = a;
		cManager = new ConnManager(context);
		psManager = new PsManager(context, animeID);
		
//		boolean needSync = checkSyncNeeded();
		
//		if (needSync)
//		{
//			Toast.makeText(context, "syncing", 1000).show();
//			Thread syncThread = new syncThread(psManager, cManager);
//			syncThread.run();
//		}
//		else
//		{
//			Toast.makeText(context, "no sync needed", 1000).show();
//		}
	}
	
	public void prepareValueForSync(String key, String val)
	{
		psManager.setValue(key, val, true);
		psManager.setValue(key, val);
		
//		Toast.makeText(context, "Value prepped for sync", 1000).show();
	}
	
	public boolean checkSyncNeeded()
	{
		boolean r = false;
		
		String test = psManager.getValue("watchStatus", true);
		String test2 = psManager.getValue("episodesWatched", true);
		String test3 = psManager.getValue("myScoreValue", true);
		
		if (!"not-exist".equals(test) || !"not-exist".equals(test2) || !"not-exist".equals(test3))
		{
			r = true;
		}

		
		return r;
	}
	
	public void doSync()
	{
		syncThread sThread = new syncThread(psManager, cManager);
		sThread.start();
	}
	
	public class syncThread extends Thread
	{
		PsManager mpsManager;
		ConnManager mcManager;
		
		public syncThread(PsManager p, ConnManager c)
		{
			mpsManager = p;
			mcManager = c;
		}
		
		@Override
		public void run()
		{
			Boolean succeeded = false;
			String anime = mpsManager.getAnime();
			String watching = mpsManager.getValue("episodesWatched", false);
			String status = mpsManager.getValue("watchStatus", false);
			String score = mpsManager.getValue("myScoreValue", false);
			
			if (mcManager.writeAnimeDetails(anime, status, watching, score))
			{
				mpsManager.removeSyncFile("episodesWatched");
				mpsManager.removeSyncFile("watchStatus");
				mpsManager.removeSyncFile("myScoreValue");
			}
			
			
		}
	}
}
