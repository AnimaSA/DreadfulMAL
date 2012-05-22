package net.somethingdreadful.MAL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.util.Base64;
import android.widget.Toast;

public class ConnManager {
	static Context context;
	final static String APIProvider = "http://mal-api.com/";
	final static String VerifyAPI = "account/verify_credentials";
	final static String readAnimeListAPI = "animelist/";
	final static String readAnimeDetailsAPI = "anime/";
	final static String writeAnimeDetailsAPI = "animelist/anime/";
	final static String readAnimeMineParam = "?mine=1";
	String malUser;
	String malPass;
	static HttpClient client = new DefaultHttpClient();
	static HttpResponse response;
	static HttpGet request;
	static HttpPut writeRequest;
	static HttpEntity getResponseEntity;
	static StatusLine statusLine;
	static int statusCode;
	static PrefManager prefManager;
	
	public ConnManager(Context mContext)
	{
		
		context = mContext;
		prefManager = new PrefManager(context);
		
		malUser = prefManager.getUser();
		malPass = prefManager.getPass();
		
	}
	
	public void setTestAccount(String testUser, String testPass)
	{
		malUser = testUser;
		malPass = testPass;
	}
	
	public boolean verifyAccount()
	{
		request = new HttpGet(APIProvider + VerifyAPI);
		request.setHeader("Authorization", "basic " + Base64.encodeToString((malUser + ":" + malPass).getBytes(), Base64.NO_WRAP));
		try 
		{
			response = client.execute(request);
			
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		statusLine = response.getStatusLine();
		statusCode = statusLine.getStatusCode();
		
		if (statusCode == 200)
		{
			return true;
		}
		
		else
		{
//			System.out.println(statusCode);
			return false;
		}
	}
	
	public JSONObject getAnimeList()
	{
		String result = null;
		JSONObject jReturn = null;
		
		request = new HttpGet(APIProvider + readAnimeListAPI + malUser);
		
		
		try 
		{
			response = client.execute(request);
			
			HttpEntity getResponseEntity = response.getEntity();
			
			if (getResponseEntity != null) 
			{
				result = EntityUtils.toString(getResponseEntity);
				jReturn = new JSONObject(result);
				
			}
			
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		
		return jReturn;
	}
	
	public JSONObject getAnimeDetails(String anime)
	{
		String result = null;
		JSONObject jReturn = null;
		
		request = new HttpGet(APIProvider + readAnimeDetailsAPI + anime + readAnimeMineParam);
		request.setHeader("Authorization", "basic " + Base64.encodeToString((malUser + ":" + malPass).getBytes(), Base64.NO_WRAP));
		
		try 
		{
			response = client.execute(request);
			
			HttpEntity getResponseEntity = response.getEntity();
			
			if (getResponseEntity != null) 
			{
				result = EntityUtils.toString(getResponseEntity);
				jReturn = new JSONObject(result);
				
			}
			
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		
		return jReturn;
	}
	
	public Boolean writeAnimeDetails(String anime, String status, String episodes, String score)
	{
		Boolean success = false;
		
		writeRequest = new HttpPut(APIProvider + writeAnimeDetailsAPI + anime);
		writeRequest.setHeader("Authorization", "basic " + Base64.encodeToString((malUser + ":" + malPass).getBytes(), Base64.NO_WRAP));
		
		List<NameValuePair> putParams = new ArrayList<NameValuePair>();
		putParams.add(new BasicNameValuePair("status", status));
		putParams.add(new BasicNameValuePair("episodes", episodes));
		putParams.add(new BasicNameValuePair("score", score));
		
		try 
		{
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(putParams);
			writeRequest.setEntity(entity);
			
			response = client.execute(writeRequest);
			
			System.out.println(response.getStatusLine().toString());
			
			if (200 == response.getStatusLine().getStatusCode()) 
			{
				success = true;		
			}
			
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (NetworkOnMainThreadException bullshit)
		{
			Toast.makeText(context, "apparantly this shit is on the main thread but it's running from inside a thread wtf", 2000).show();
		}
		
		return success;
	}
	
	public Bitmap loadImage(String url)
	{
		Bitmap loadedImage = null;
		
		try 
		{
			loadedImage = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return loadedImage;
	}

}
