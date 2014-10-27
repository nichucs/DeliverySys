package com.example.delivervpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.delivervpi.dummy.BulkModel;
import com.example.delivervpi.dummy.Datas;
import com.example.delivervpi.utils.ConnectionDetector;
import com.example.delivervpi.utils.MyStringAsync;

public class NetworkReceiver extends BroadcastReceiver {
	Context c;
	@Override
	public void onReceive(Context context, Intent intent) {
		c=context;
		ConnectionDetector cnn=new ConnectionDetector(context);
		if(cnn.isConnectingToInternet())
		{
			Log.d("Nzm", "Net connected");
			JSONObject str=new JSONObject();
			JSONArray order_arr=new JSONArray();
			JSONArray loc_arr=new JSONArray();
			final BulkModel blk=new BulkModel(context);
			try{
			if(blk.getOrders()!=null && blk.getOrders().size()>0){
				for(HashMap<String, String> hm : blk.getOrders() ){
					JSONObject obj=new JSONObject();
					obj.put("routeId", hm.get("routeId"));
					obj.put("date", hm.get("date"));
					obj.put("status", hm.get("status"));
					order_arr.put(obj);
				}
			}
			if(blk.getLocations()!=null && blk.getLocations().size()>0){
				for(HashMap<String, String> hm : blk.getLocations() ){
					JSONObject obj=new JSONObject();
					obj.put("locationId", hm.get("locationId"));
					obj.put("date", hm.get("date"));
					obj.put("status", hm.get("status"));
					loc_arr.put(obj);
				}
			}
			str.put("orders", order_arr);
			str.put("locations", loc_arr);
			Log.d("Nzm", "BLK:"+str.toString());
			if(order_arr.length()>0 || loc_arr.length()>0){
				ArrayList<NameValuePair> nvp2=new ArrayList<NameValuePair>();
				nvp2.add(new BasicNameValuePair("request",str.toString()));
				new MyStringAsync(context, nvp2){
					protected void onPostExecute(String result) {
						try {
							JSONObject res=new JSONObject(result);
							if(res.getString("status").equals("OK")){
								Datas db=new Datas(c);
								db.open();
								db.clearBLK();
								db.close();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.execute("http://zappiertech.com/route/1.7/php/controller/?action=setOrdersAndRoutesStatus");
			}
			}catch(Exception e){
				
			}
		}else{
			Log.d("Nzm", "net disconnected");
		}
	}



}
