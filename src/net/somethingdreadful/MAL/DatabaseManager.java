package net.somethingdreadful.MAL;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {
	
	private DatabaseHelper dbh;
	private SQLiteDatabase database;
	
	public DatabaseManager(Context c) 
	{
		dbh = new DatabaseHelper(c);
	}
	
	public void open() throws SQLException 
	{
		database = dbh.getWritableDatabase();
	}
	
	public void close()
	{
		dbh.close();
	}
	
	
	
}
