package com.example.delivervpi.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.delivervpi.dummy.MyRoutes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.os.AsyncTask;
import android.util.Log;


public class MyAsync extends AsyncTask<String, Integer, MyRoutes> {
 
	 HttpClient client;
		HttpPost post;
		HttpResponse response;
		String result;

		
		ArrayList<LatLng> list;
		HashMap<Integer, ArrayList<LatLng>>hm;
		ArrayList<Integer>dist;
		MyRoutes obj;
		
		@Override
		protected void onPreExecute() {
			client=new DefaultHttpClient();
			list=new ArrayList<LatLng>();
			result=null;
			hm=new HashMap<Integer, ArrayList<LatLng>>();
			dist=new ArrayList<Integer>();
			super.onPreExecute();
		}
		@SuppressWarnings("unchecked")
		@Override
		protected MyRoutes doInBackground(String... params) {
			String url="http://maps.googleapis.com/maps/api/directions/json?origin="+params[0]+"&destination="+params[1]+"&sensor=true";
			if(params.length>2){
				url+="&waypoints=optimize:true%7C";
				for(int i=2;i<params.length;i++){
					String s= params[i];
					url+=s+"%7C";
				}
				url=url.substring(0, url.length()-3);
			}
			Log.d("Nzm", "URL:"+url);
//http://maps.googleapis.com/maps/api/directions/json?origin=1.355296,103.882355&destination=1.351863,103.876861&sensor=true&waypoints=1.355296,103.882355|1.351863,103.876861|1.337791,103.889221

			post=new HttpPost(url);
			try {
				response=client.execute(post);
				HttpEntity entity=response.getEntity();
				result=EntityUtils.toString(entity);
				Log.d("Nzm", "api result:"+result);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(result!=null){
				LatLng north = null;
				LatLng south = null;
//				MainActivity.json_result=result;
				try {
					JSONObject all=new JSONObject(result);
					if(all.getString("status").equals("OK")){
						JSONArray routs=all.getJSONArray("routes");
						
							JSONObject routobj=routs.getJSONObject(0);
							JSONObject bounds=routobj.getJSONObject("bounds");
							JSONObject northeast=bounds.getJSONObject("northeast");
							JSONObject southwest=bounds.getJSONObject("southwest");
							north=new LatLng(northeast.getDouble("lat"), northeast.getDouble("lng"));
							south=new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng"));
							
							JSONObject polylines=routobj.getJSONObject("overview_polyline");
							String points=polylines.getString("points");
							GeopointWaypoints converter=new GeopointWaypoints();
							hm.put(0, (ArrayList<LatLng>) converter.decodePoly(points));

							JSONArray legs=routobj.getJSONArray("legs");
							for(int j=0;j<legs.length();j++)
							{
							JSONObject legsobj=legs.getJSONObject(j);
							
							JSONObject dist=legsobj.getJSONObject("distance");
							int d=dist.getInt("value");
							Log.d("Nzm", "distance of leg "+j+":"+d);
							
//							JSONArray steps=legsobj.getJSONArray("steps");
//							list=new ArrayList<LatLng>();
//							for(int i=0;i<steps.length();i++){
//								JSONObject stepobj=steps.getJSONObject(i);
//								JSONObject start=stepobj.getJSONObject("start_location");
//								JSONObject end=stepobj.getJSONObject("end_location");
//								LatLng one=new LatLng(start.getDouble("lat"), start.getDouble("lng"));
//								LatLng two=new LatLng(end.getDouble("lat"), end.getDouble("lng"));
//								list.add(one);
//								list.add(two);
//							}
//							hm.put(j, (ArrayList<LatLng>) list.clone());
							this.dist.add(d);
						}
						obj=new MyRoutes(hm,dist,new LatLngBounds(south, north), points);
						
					}else{
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				
			}
			return obj;
		}

	}

