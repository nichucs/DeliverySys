package com.example.delivervpi.dummy;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class BulkModel {

	ArrayList<HashMap<String, String>> orders;
	ArrayList<HashMap<String, String>> locations;
	Context context;
	Datas db;
	
	public BulkModel(Context context) {
		this.context=context;
		db=new Datas(context);
		db.open();
		orders=db.getOrdersBLK();
		locations=db.getLocationsBLK();
		db.close();
	}
	public ArrayList<HashMap<String, String>> getOrders() {
		return orders;
	}
	public void setOrders(ArrayList<HashMap<String, String>> orders) {
		this.orders = orders;
	}
	public ArrayList<HashMap<String, String>> getLocations() {
		return locations;
	}
	public void setLocations(ArrayList<HashMap<String, String>> locations) {
		this.locations = locations;
	}
	
	public void clear(){
		db.clearBLK();
	}
	
}
