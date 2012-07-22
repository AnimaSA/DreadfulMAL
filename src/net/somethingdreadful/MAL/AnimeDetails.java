package net.somethingdreadful.MAL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AnimeDetails extends Activity {
	String animeName = "";
	String animeID = "";
	private static Context context;
	private static ConnManager cManager;
    private static PrefManager pManager;
    private static ListManager lManager;
    private PsManager psManager;
    private SyncManager sManager;
    private static Handler messenger;
    private Message msg = new Message();
    private Message msg2 = new Message();
    private boolean loadSucceeded = false;
    private Thread infoThread;
    private Thread picThread;
    private JSONObject detailsJSON;
    private String synopsis;
    private String watchStatus;
    private String watchStatusCode;
    private String episodesTotal;
    private String episodesWatched;
    private String seriesType;
    private String seriesStatus;
    private String myScoreValue;
    private String malScoreValue;
    private String malRankValue;
    private String imageUrl;
    private Bitmap loadedImage;
    private TextView watchStatusView;
    private TextView synopsisView;
    private TextView episodesTotalView;
    private TextView episodesWatchedView;
    private TextView seriesTypeView;
    private TextView seriesStatusView;
    private TextView myScoreValueView;
    private TextView malScoreValueView;
    private TextView malRankValueView;
    private ImageView animePicView;
    private Boolean threadRunning = false;
    private int oldEpisodesWatchedInt;
    private int newEpisodesWatchedInt;
	private int episodesTotalInt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animedetails);
		
		context = getApplicationContext();
        cManager = new ConnManager(context);
        pManager = new PrefManager(context);
        lManager = new ListManager(context);
        infoThread = new workThread("info");
        picThread = new workThread("pic");
		
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null)
		{
			animeName = extras.getString("animeName");
			animeID = extras.getString("animeID");
			watchStatus = extras.getString("watchStatus");
			episodesTotal = extras.getString("episodesTotal");
			episodesWatched = extras.getString("episodesWatched");
			imageUrl = extras.getString("image_url");
		}
		
		psManager = new PsManager(context, animeID);
		sManager = new SyncManager(context, animeID);
		
		setTitle(animeName);
		watchStatusView = (TextView) findViewById(R.id.watchStatus);
		episodesWatchedView = (TextView) findViewById(R.id.episodesWatched);
		episodesTotalView = (TextView) findViewById(R.id.episodesTotal);
		synopsisView = (TextView) findViewById(R.id.synopsis);
		seriesTypeView = (TextView) findViewById(R.id.seriesType);
		seriesStatusView = (TextView) findViewById(R.id.seriesStatus);
		myScoreValueView = (TextView) findViewById(R.id.myScoreValue);
		malScoreValueView = (TextView) findViewById(R.id.malScoreValue);
		malRankValueView = (TextView) findViewById(R.id.malRankValue);
		animePicView = (ImageView) findViewById(R.id.animePic);
		
		watchStatusView.setText(watchStatus);
		episodesWatchedView.setText(episodesWatched);
		episodesTotalView.setText(episodesTotal);
		
		messenger = new Handler() {
            public void handleMessage(Message msg) 
            {
            	if (msg.what == 10)
            	{ 
            		if(msg.obj.toString().equals(animeID))
            		{
            			synopsisView.setText(synopsis);
            			episodesWatchedView.setText(episodesWatched);
            			seriesTypeView.setText(seriesType);
            			seriesStatusView.setText(seriesStatus);
            			myScoreValueView.setText(myScoreValue);
            			malScoreValueView.setText(malScoreValue);
            			malRankValueView.setText(malRankValue);
            			
            		    oldEpisodesWatchedInt = Integer.parseInt(episodesWatched);
         			    newEpisodesWatchedInt = oldEpisodesWatchedInt + 1;
         			    
         			    if ("unknown".equals(episodesTotal))
         			    {
         			    	episodesTotalInt = 0;
         			    }
         			    else
         			    {
         			    	episodesTotalInt = Integer.parseInt(episodesTotal);
         			   
         			    }
         			   if ((oldEpisodesWatchedInt == episodesTotalInt) && (episodesTotalInt > 0))
         			   {
         				   sManager.prepareValueForSync("watchStatus", "completed");
         				   watchStatusView.setText("completed");
         			   }
            		}
            	}
            	if (msg.what == 11)
            	{
            		if(msg2.obj.toString().equals(animeID))
            		{
            			animePicView.setImageBitmap(loadedImage);
            	
            		}
            	}
            	
            super.handleMessage(msg);
            }
        };
        
        threadRunning = true;
        infoThread.start();
        picThread.start();
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		if ("watching".equals(watchStatus))
		{
			//TODO invalidate menu and loading logic depending on anime status passed to activity
			inflater.inflate(R.menu.ad_menu_watching, menu);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId())
	   {
	   		case R.id.watchedAnEpisodeButton:
	   			plusOneEpisode();
	   			return true;
	   		case R.id.setLastWatchedButton:
	   			Toast.makeText(context, "Not implemented yet!", 1000).show();
	   			return true;
	   		case R.id.setScoreButton:
	   			Toast.makeText(context, "Not implemented yet!", 1000).show();
	   			return true;
	   		case R.id.forceRefreshButton:
	   			Toast.makeText(context, "Not implemented yet!", 1000).show();
	   			return true;
	   		case R.id.removeFromListButton:
	   			Toast.makeText(context, "Not implemented yet!", 1000).show();
	   			return true;
	   		default:
	   			return super.onOptionsItemSelected(item);
	   }
	}
	
	private void plusOneEpisode()
	   {
		   if (!threadRunning && loadSucceeded)
		   {
			   String newEpisodesWatchedString = Integer.toString(newEpisodesWatchedInt);
//			   Toast.makeText(context, "New value: " + newEpisodesWatched, 2000).show();
			   
			   if (newEpisodesWatchedInt == episodesTotalInt)
			   {
				   sManager.prepareValueForSync("watchStatus", "completed");
				   watchStatusView.setText("completed");
			   }
			   
			   if ((newEpisodesWatchedInt <= episodesTotalInt) || (episodesTotalInt == 0))
			   {
				   sManager.prepareValueForSync("episodesWatched", newEpisodesWatchedString);
				   episodesWatchedView.setText(newEpisodesWatchedString);
				   
				   newEpisodesWatchedInt += 1;
			   }
		
		   }
		   else
		   {
			   Toast.makeText(context, "Still loading, try again in a moment.", 2000).show();
		   }
		   
	   }
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		if (sManager.checkSyncNeeded())
		{
			sManager.doSync();
		}
		
//		Toast.makeText(context, "onPause", 1000).show();
		
		
		
	}
	
	public class workThread extends Thread
	{
		
		String whatToGet = "";
		public workThread(String get)
		{
			whatToGet = get;
		}
    	
    	@Override
		public void run()
		{
    		String lAnimeID = animeID;
    		
    		if ("info".equals(whatToGet))
			{	
				msg.obj = lAnimeID;
				
				if(psManager.getValue("seriesType").equals(""))
				{
					detailsJSON = cManager.getAnimeDetails(animeID);
					synopsis = lManager.getDataFromJSON(detailsJSON, "synopsis");
					watchStatus = lManager.getDataFromJSON(detailsJSON, "watched_status");
					episodesWatched = lManager.getDataFromJSON(detailsJSON, "watched_episodes");
					episodesTotal = lManager.getDataFromJSON(detailsJSON, "episodes");
					seriesType = lManager.getDataFromJSON(detailsJSON, "type");
					seriesStatus = lManager.getDataFromJSON(detailsJSON, "status");
					myScoreValue = lManager.getDataFromJSON(detailsJSON, "score");
					malScoreValue = lManager.getDataFromJSON(detailsJSON, "members_score");
					malRankValue = lManager.getDataFromJSON(detailsJSON, "rank");
					
					if ("TV".equals(seriesType))
					{
						seriesType = "tv series";
					}
					synopsis = synopsis.replace("<br>", "\n");
					
					if (seriesType != null | seriesType != "")
					{
						psManager.setValue("synopsis", synopsis);
						psManager.setValue("watchStatus", watchStatus);
						psManager.setValue("episodesWatched", episodesWatched);
						psManager.setValue("episodesTotal", episodesTotal);
						psManager.setValue("seriesType", seriesType);
						psManager.setValue("seriesStatus", seriesStatus);
						psManager.setValue("myScoreValue", myScoreValue);
						psManager.setValue("malScoreValue", malScoreValue);
						psManager.setValue("malRankValue", malRankValue);
						
						loadSucceeded = true;
					}
				}
				else
				{
					synopsis = psManager.getValue("synopsis");
					watchStatus = psManager.getValue("watchStatus");
					episodesWatched = psManager.getValue("episodesWatched");
					episodesTotal = psManager.getValue("episodesTotal");
					seriesType = psManager.getValue("seriesType");
					seriesStatus = psManager.getValue("seriesStatus");
					myScoreValue = psManager.getValue("myScoreValue");
					malScoreValue = psManager.getValue("malScoreValue");
					malRankValue = psManager.getValue("malRankValue");
					
					loadSucceeded = true;
				}
				
				threadRunning = false;
				msg.what = 10;
				messenger.sendMessage(msg);
			}
			if ("pic".equals(whatToGet))
			{
				msg2.obj = lAnimeID;
				loadedImage = psManager.getPic();
				
				if (loadedImage == null)
				{
					loadedImage = cManager.loadImage(imageUrl);
					psManager.setPic(loadedImage);
				}
					
				msg2.what = 11;
				messenger.sendMessage(msg2);
			}
	    };
	}
}
