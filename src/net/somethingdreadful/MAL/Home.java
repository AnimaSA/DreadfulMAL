package net.somethingdreadful.MAL;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.R.menu;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Home extends ListActivity {
    private static Context context;
	private static ConnManager cManager;
    private static PrefManager pManager;
    private static ListManager lManager;
	private static boolean initialized;
	private static boolean dataLoaded = false;
	private static boolean dataExists = false;
	private static boolean threadRunning = false;
	static private Handler messenger;
	private Message msg = new Message();
	static private JSONObject watchListData = null;
	static private ArrayList<HashMap<String, String>> watchList;
	static private long lastSyncTime;
	static private long syncFrequency;
	static private String defaultList;
	static private String whatList;
	static private int selectedItemID;
	static private ActionBar bar;
	ListView lv;
	static private boolean spinnerSet = false;
	static private Parcelable state;
	
	
    @SuppressWarnings("static-access")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        cManager = new ConnManager(context);
        pManager = new PrefManager(context);
        lManager = new ListManager(context);
        lv = getListView();
        
        initialized = pManager.getInit();
        
        if (initialized == false)
        {
        	Intent firstRunInit = new Intent(this, FirstTimeInit.class);
        	firstRunInit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(firstRunInit);
        	
        }
        else
        {
	 
	        lastSyncTime = pManager.getLastSyncTime();
	        syncFrequency = pManager.getSyncFrequency();
	        
	       
	        if (lastSyncTime != 0)
	        {
	        	dataExists = true;
	        }
	        
	        if (dataExists == true)
	        {
	        	if(lastSyncTime < (lastSyncTime + syncFrequency))
	        	{
	        		watchListData = pManager.getAnimelist();
	        		dataLoaded = true;
	        	}
	        	else
	        	{
	        		 dataLoaded = false;
	        	}
	        }
	        else
	        {
	        	if (lastSyncTime == 0)
	        	{
	                dataLoaded = false;
	        	}
	        	else
	        	{
	        		if(lastSyncTime < (lastSyncTime + syncFrequency))
	            	{
	            		watchListData = pManager.getAnimelist();
	            		dataLoaded = true;
	            	}
	            	else
	            	{
	            		 dataLoaded = false;
	            	}
	        	}
	        }
	        
	        if (spinnerSet == false)
	        {
	        	defaultList = pManager.getDefaultList();
	        	whatList = defaultList;
	        	selectedItemID = 1;
	        	spinnerSet = true;
	        }
	        else
	        {
	        	whatList = lManager.spinnerPositionToString(selectedItemID);
	        }
	       
	        messenger = new Handler() {
	            public void handleMessage(Message msg) 
	            {
	            	if (msg.what == 10)
	            	{ 
						ListAdapter adapter = new SimpleAdapter(context, watchList,
								R.layout.anime_layout,
								new String[] { "animeName", "watch_status", "watched_episodes", "total_episodes" }, new int[] {
										R.id.animeName, R.id.status, R.id.watchCount, R.id.totalEpisodes});
	
						setListAdapter(adapter);
						
						Toast.makeText(context, "Sync Done!", 2000).show();
	            		
	            	}
	            	
	            super.handleMessage(msg);
	            }
	        };
	        
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) 
				{
					@SuppressWarnings("unchecked")
					HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);
	//				Toast.makeText(context, "'" + o.get("animeID") + "' was clicked.", Toast.LENGTH_SHORT).show();
					Intent viewAnime = new Intent(context, AnimeDetails.class);
		        	viewAnime.putExtra("animeID", o.get("animeID"));
		        	viewAnime.putExtra("animeName", o.get("animeName"));
		        	viewAnime.putExtra("watchStatus", o.get("watch_status"));
		        	viewAnime.putExtra("episodesTotal", o.get("total_episodes"));
		        	viewAnime.putExtra("episodesWatched", o.get("watched_episodes"));
		        	viewAnime.putExtra("image_url", o.get("image_url"));
		        	startActivity(viewAnime);
				}
			});
			
			bar = getActionBar();
	        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        
	        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.listTypesTitles,
	                R.xml.sortspinner);
	        
	        OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
	        	  // Get the same strings provided for the drop-down's ArrayAdapter
//	        	  String[] strings = getResources().getStringArray(R.array.listTypesTitles);

				public boolean onNavigationItemSelected(int position, long itemID) {
					
					selectedItemID = position;
					whatList = lManager.spinnerPositionToString(position);
//					Toast.makeText(context, "Position: " + position + "\nitemID: " + itemID + "\nList: " + whatList, 2000).show();
					watchList = lManager.makeWatchListArray(watchListData, whatList);
					ListAdapter adapter = new SimpleAdapter(context, watchList,
							R.layout.anime_layout,
							new String[] { "animeName", "watch_status", "watched_episodes", "total_episodes" }, new int[] {
									R.id.animeName, R.id.status, R.id.watchCount, R.id.totalEpisodes});

					setListAdapter(adapter);
					if (state != null)
					{
						lv.onRestoreInstanceState(state);
						state = null;
					}
					
					return true;
				}
	        };
	        
	        bar.setDisplayShowTitleEnabled(false);
	        bar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
			
			Thread workThread = new workThread(whatList, dataLoaded);
	        if (threadRunning == false)
	        {
	        	workThread.start();
	        }
        }

    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	if (initialized == true)
    	{
    	
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
    	super.onSaveInstanceState(bundle);
    	
    	lv.getFirstVisiblePosition();
    	state = lv.onSaveInstanceState();
    	bundle.putParcelable("state", state);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle bundle)
    {
    	super.onRestoreInstanceState(bundle);
    	
    	state = bundle.getParcelable("state");
    }
    
    @Override 
    protected void onResume()
    {
    	super.onResume();
    	
    	if (initialized == true)
    	{
    		bar.setSelectedNavigationItem(selectedItemID);
    	}
    	
    	//TODO After returning from detail view, update the appropriate anime's status in the list
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.home_menu, menu);
	    return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId())
	   {
	   		case R.id.settingsItem:
	   			Intent goToSettings = new Intent(this, Settings.class);
	        	startActivity(goToSettings);
	   			return true;
	   		case R.id.forceSyncItem:
	   			Thread forceSyncThread = new workThread(whatList, false);
	   			if (threadRunning == false)
	   			{
	   				forceSyncThread.start();
	   			}
	   			return true;
	   		default:
	   			return super.onOptionsItemSelected(item);
	   }
	}
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	super.onPrepareOptionsMenu(menu);
    	
    	if (threadRunning == true)
    	{
    		menu.findItem(R.id.forceSyncItem).setEnabled(false);
    	}
    	else
    	{
    		menu.findItem(R.id.forceSyncItem).setEnabled(true);
    	}
    	
    	return true;
    }
    
    public class workThread extends Thread
	{
		String whatToGet = "watching";
		Boolean loaded = false;
		public workThread()
		{
			
		}
		public workThread(String get)
		{
			whatToGet = get;
		}
		
		public workThread(boolean b)
		{
			loaded = b;
		}
		public workThread(String get, boolean b)
		{
			whatToGet = get;
			loaded = b;
		}
    	
    	@Override
		public void run()
		{
			threadRunning = true;
    		
    		if (loaded == false)
			{
				watchListData = cManager.getAnimeList();
				pManager.setAnimelist(watchListData);
				pManager.setLastSyncTime(System.currentTimeMillis());
				pManager.commitChanges();
				dataExists = true;
			}
			
			watchList = lManager.makeWatchListArray(watchListData, whatToGet);
			
			if (loaded == false)
			{
				msg.what = 10;
				messenger.sendMessage(msg);
			}
			
			threadRunning = false;
	         
	    };
	}
}