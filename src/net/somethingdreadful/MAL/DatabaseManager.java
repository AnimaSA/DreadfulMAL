package net.somethingdreadful.MAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {
	
	public class DatabaseHelper extends SQLiteOpenHelper
	{
		private static final String DB_NAME = "MAL_DB.db";
		private static final int DB_VERSION = 1;
		
		private static final String ANIME_TABLE = "Anime";
		private static final String ANIME_ID = "anime_id";
		private static final String ANIME_NAME = "name";
		private static final String ANIME_SYNOPSIS = "synopsis";
		private static final String ANIME_WATCH_STATUS = "watch_status";
		private static final String ANIME_WATCHED_EPISODES = "episodes_watched";
		private static final String ANIME_STATUS = "status";
		private static final String ANIME_TOTAL_EPISODES = "episodes";
		
		private static final String DB_CREATE_SQL = "create table" + ANIME_TABLE + "( " + ANIME_ID + " integer primary key, " + ANIME_NAME + ", " + ANIME_SYNOPSIS + ", "
				+ ANIME_WATCH_STATUS + ", " + ANIME_WATCHED_EPISODES + ", " + ANIME_STATUS + ", " + ANIME_TOTAL_EPISODES  + ");";
		
		public DatabaseHelper(Context c)
		{
			super(c, DB_NAME, null, DB_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(DB_CREATE_SQL);
			
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
		{
			// TODO Auto-generated method stub
			
		}
	}
	
}
