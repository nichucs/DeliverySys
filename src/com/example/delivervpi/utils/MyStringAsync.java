package com.example.delivervpi.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class MyStringAsync extends AsyncTask<String, Integer, String> {

	HttpClient client;
	HttpPost post;
	HttpResponse response;
	HttpEntity entity;
	ArrayList<NameValuePair> nvp;
	String res;
	Context context;
	public MyStringAsync() {
		client=new DefaultHttpClient();
		nvp=new ArrayList<NameValuePair>();
	}

	public MyStringAsync(Context context,ArrayList<NameValuePair> nvp) {
		super();
		client=new DefaultHttpClient();
		this.nvp = nvp;
		this.context=context;
	}
	

	public ArrayList<NameValuePair> getNvp() {
		return nvp;
	}

	@Override
	protected String doInBackground(String... arg) {
		post=new HttpPost(arg[0]);
		try {
			HttpContext localContext = new BasicHttpContext();
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(nvp));
			response=client.execute(post,localContext);
			entity=response.getEntity();
			 res=EntityUtils.toString(entity);
			Log.d("Nzm", "Response:"+res);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
