package net.somethingdreadful.MAL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FirstTimeInit extends Activity {
	static EditText malUser;
	static EditText malPass;
	static String testMalUser;
	static String testMalPass;
	static Button connectButton;
	static ProgressDialog pd;
	static Thread netThread;
	static Context context;
	static private Handler messenger;
	static PrefManager prefManager;
	ConnManager connManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstrun);
        
        malUser = (EditText) findViewById(R.id.edittext_malUser);
        malPass = (EditText) findViewById(R.id.edittext_malPass);
        Button connectButton = (Button) findViewById(R.id.button_connectToMal);
        context = getApplicationContext();
        
        prefManager = new PrefManager(context);
        connManager = new ConnManager(context);
        
        connectButton.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		testMalUser = malUser.getText().toString();
        		testMalPass = malPass.getText().toString();
        		connManager.setTestAccount(testMalUser, testMalPass);
        		tryConnection();
        	}
        	
        });
        
        messenger = new Handler() {
            public void handleMessage(Message msg) 
            {
            	if (msg.what == 2)
            	{ 
            		pd.dismiss();
            		
            		Toast.makeText(context, msg.obj.toString(), 5000).show();
            	}
            	if (msg.what == 3)
            	{
            		pd.dismiss();
            		
            		prefManager.setUser(testMalUser);
    				prefManager.setPass(testMalPass);
    				prefManager.setInit(true);
    				prefManager.commitChanges();
            		
            		Intent goHome = new Intent(context, Home.class);
                	goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                	startActivity(goHome);
            	}
            	
            super.handleMessage(msg);
            }
        };
    }
	
	
	private void tryConnection()
	{
		pd = ProgressDialog.show(this, "Verifying", "Checking if your account details are valid with MAL");
		netThread = new networkThread();
		netThread.start();
		
	}
	
	public class networkThread extends Thread
	{
		@Override
		public void run()
		{
			boolean valid = connManager.verifyAccount();
			
			String words = "";
			Message msg = new Message();
			
			if (valid == false)
			{
				msg.what = 2;
				words = "Bad username/password, try again.";
				msg.obj = words;
				messenger.sendMessage(msg);
			}
			else
			{	
				msg.what = 3;
				messenger.sendMessage(msg);
			}
		}
	}
	

}
