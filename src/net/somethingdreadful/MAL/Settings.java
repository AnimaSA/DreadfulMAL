package net.somethingdreadful.MAL;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.settings);

		PreferenceManager prefMgr = getPreferenceManager();
		prefMgr.setSharedPreferencesName("prefs");

		
		addPreferencesFromResource(R.xml.settings);
	}
}
