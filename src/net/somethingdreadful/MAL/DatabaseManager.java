package net.somethingdreadful.MAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "MAL_DB.db";
	private static final int DB_VERSION = 1;
	
	private static class dataStructrure
	{
		private static final String ANIME_ID = "anime_id";
		private static final String ANIME_NAME = "name";
		private static final String ANIME_SYNOPSIS = "synopsis";
		private static final String WATCH_STATUS = "watch_status";
		private static final String WATCHED_EPISODES = "episodes_watched";
		private static final String ANIME_STATUS = "status";
		private static final String TOTAL_EPISODES = "episodes";
		
	}
	
	public DatabaseManager(Context c)
	{
		super(c, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
