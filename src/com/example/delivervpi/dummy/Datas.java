package com.example.delivervpi.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.delivervpi.dummy.DummyContent.Order;
import com.example.delivervpi.dummy.DummyContent.Route;

public class Datas {

	public static final String DATABASE_NAME="routedata";
	public static final int DATABASE_VERSION=2;
	public static final String ROUTES_TABLE="routes";
	public static final String ORDERS_TABLE="orders";
	public static final String ORDER_STATUS_TABLE="BLKorder";
	public static final String LOCATION_STATUS_TABLE="BLKlocations";
	
	DbHelper myHelper;
	Context context;
	SQLiteDatabase myDb;
	public Datas(Context context) {
		super();
		this.context = context;
	}
	
	public class DbHelper extends SQLiteOpenHelper{
		
		public DbHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+ROUTES_TABLE+"(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "route_id varchar(15) not null,"
					+ " order_time text,"
					+ " status int,"
					+ " deliver_time_start text,"
					+ " deliver_time_end text,"
					+ " polylines text)");
			db.execSQL("CREATE TABLE "+ORDERS_TABLE+"(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " order_id varchar(15) not null,"
					+ " customer_id varchar(15),"
					+ " route_id varchar(15) not null,"
					+ " destination text,"
					+ " latitude double,"
					+ " longitude double,"
					+ " order_time text,"
					+ " status int,"
					+ " delivery_time text,"
					+ "loc_id int)");
			db.execSQL("CREATE TABLE "+ORDER_STATUS_TABLE+"(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " routeId varchar(15) not null,"
					+ " date text,"
					+ " status int)");
			db.execSQL("CREATE TABLE "+LOCATION_STATUS_TABLE+"(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " locationId varchar(15) not null,"
					+ " date text,"
					+ " status int)");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public Datas open()
	{
		myHelper=new DbHelper(context);
		myDb=myHelper.getWritableDatabase();
		return this;
		
	}
	public void close(){
		myDb.close();
	}
	
	public void insert(List<Route> routes){
		for(Route route: routes){
			ContentValues vals=new ContentValues();
			vals.put("route_id", ""+route.getRoute_id());
			vals.put("order_time", route.getOrder_time());
			vals.put("status", route.getStatus());
			myDb.insert(ROUTES_TABLE, null, vals);
			
			List<Order> orders= route.getOrders();
			for(Order order: orders){
				ContentValues order_val=new ContentValues();
//				Log.d("Nzm", "insert loc id:"+order.getLoc_id());
				order_val.put("loc_id", ""+order.getLoc_id());
				order_val.put("order_id", ""+order.getOrder_id());
				order_val.put("customer_id", order.getCustomer_id());
				order_val.put("route_id", ""+route.getRoute_id());
				order_val.put("destination", order.getDestination());
				order_val.put("latitude", order.getLat());
				order_val.put("longitude", order.getLon());
				order_val.put("order_time", order.getOrder_time());
				order_val.put("status", (order.delivered?3:1));
				order_val.put("delivery_time", order.getDelivered_time());
				myDb.insert(ORDERS_TABLE, null, order_val);
//				Log.d("Nzm", "order "+order.getOrder_id()+" inserted for route "+route.getRoute_id());
			}
		}
	}
	public void insert(Route route){

		ContentValues vals=new ContentValues();
		vals.put("route_id", ""+route.getRoute_id());
		vals.put("order_time", route.getOrder_time());
		vals.put("status", route.getStatus());
		myDb.insert(ROUTES_TABLE, null, vals);
		
		List<Order> orders= route.getOrders();
		for(Order order: orders){
			ContentValues order_val=new ContentValues();
//			Log.d("Nzm", "insert loc id:"+order.getLoc_id());
			order_val.put("loc_id", ""+order.getLoc_id());
			order_val.put("order_id", ""+order.getOrder_id());
			order_val.put("customer_id", order.getCustomer_id());
			order_val.put("route_id", ""+route.getRoute_id());
			order_val.put("destination", order.getDestination());
			order_val.put("latitude", order.getLat());
			order_val.put("longitude", order.getLon());
			order_val.put("order_time", order.getOrder_time());
			order_val.put("status", (order.delivered?3:1));
			order_val.put("delivery_time", order.getDelivered_time());
			myDb.insert(ORDERS_TABLE, null, order_val);
//			Log.d("Nzm", "order "+order.getOrder_id()+" inserted for route "+route.getRoute_id());
		}
	
	}
	
	public List<Route> getAllRoutesList(){
		List<Route> routes=null;
		
		Cursor c1= myDb.query(ROUTES_TABLE, null, null, null, null, null, null);
		if(c1!=null && c1.getCount()>0){
			c1.moveToFirst();
			routes=new ArrayList<DummyContent.Route>();
			do{
				Cursor c=myDb.query(ORDERS_TABLE, null, "route_id='"+c1.getInt(1)+"'", null, null, null, null);
				if(c!=null && c.getCount()>0){
					c.moveToFirst();
					List<Order> orders=new ArrayList<DummyContent.Order>();
					do{
//						Log.d("Nzm", "build new loc id:"+c.getInt(c.getColumnIndex("loc_id")));
						orders.add(new Order(c.getInt(10),c.getInt(1), c.getString(2), c.getString(4), c.getDouble(5), c.getDouble(6), (c.getInt(8)==1?false:true), c.getString(7), c.getString(9)));
					}while(c.moveToNext());
					c.moveToPrevious();
//					if(c1.getInt(3)==2) RouteListFragment.onDelivery=true;
					routes.add(new Route(c1.getInt(1), orders, c1.getInt(3), c1.getString(2), c1.getString(4), c1.getString(5), c1.getString(6)));
				}
			}while(c1.moveToNext());
		}
		return routes;
	}
	public void clear(){
		myDb.delete(ROUTES_TABLE, null, null);
		myDb.delete(ORDERS_TABLE, null, null);
	}
	public void clearBLK(){
		myDb.delete(ORDER_STATUS_TABLE, null, null);
		myDb.delete(LOCATION_STATUS_TABLE, null, null);
	}
	
	public boolean isEmpty(){
		Cursor c=myDb.query(ORDERS_TABLE, null, null, null, null, null, null);
		if(c.getCount()>0)
			return false;
		else
			return true;
	}
	
	public int storePolylines(String route_id,String polylines){
		ContentValues vals=new ContentValues();
		vals.put("polylines", polylines);
		return myDb.update(ROUTES_TABLE, vals, "route_id='"+route_id+"'", null);
	}
	public String getPolylines(String route_id){
		Cursor cr=myDb.query(ROUTES_TABLE,new String[] {"polylines"}, "route_id='"+route_id+"'", null, null, null, null);
		cr.moveToFirst();
		String polylines=cr.getString(0);
		return polylines;
	}
	
	public void markRouteOnDelivery(String route_id, String s_time){
		ContentValues vals=new ContentValues();
		vals.put("status", "2");
		vals.put("deliver_time_start", s_time);
		int r=myDb.update(ROUTES_TABLE, vals, "route_id='"+route_id+"'", null);
//		Log.d("Nzm", "update:"+route_id+":"+r);
	}
	public void markRouteDelivered(String route_id, String e_time){
		ContentValues vals=new ContentValues();
		vals.put("status", "3");
		vals.put("deliver_time_end", e_time);
		int r=myDb.update(ROUTES_TABLE, vals, "route_id='"+route_id+"'", null);
//		Log.d("Nzm", "update:"+r);
	}
	public void markRoutePending(String route_id){
		ContentValues vals=new ContentValues();
		vals.put("status", "1");
		vals.putNull("deliver_time_end");
		int r=myDb.update(ROUTES_TABLE, vals, "route_id='"+route_id+"'", null);
//		Log.d("Nzm", "update:"+r);
	}
	public void markOrderDelivered(String loc_id,String time){
		ContentValues vals=new ContentValues();
		vals.put("status", "2");
		vals.put("delivery_time", time);
		int r=myDb.update(ORDERS_TABLE, vals, "loc_id='"+loc_id+"'", null);
//		Log.d("Nzm", "update:"+r);	
	}
	
	public void insertOrderBLK(ContentValues cv){
		myDb.insert(ORDER_STATUS_TABLE, null, cv);
	}
	public void insertLocationBLK(ContentValues cv){
		myDb.insert(LOCATION_STATUS_TABLE, null, cv);
	}
	public ArrayList<HashMap<String, String>> getOrdersBLK(){
		ArrayList<HashMap<String, String>> orders=null;
		Cursor cr=myDb.query(ORDER_STATUS_TABLE, null, null, null, null, null, null);
		if(cr!=null && cr.getCount()>0){
			cr.moveToFirst();
			orders=new ArrayList<HashMap<String,String>>();
			do{
				HashMap<String, String> hm=new HashMap<String, String>();
				hm.put("routeId", ""+cr.getString(1));
				hm.put("date", cr.getString(2));
				hm.put("status", ""+cr.getInt(3));
				orders.add(hm);
			}while(cr.moveToNext());
		}
		return orders;
	}
	public ArrayList<HashMap<String, String>> getLocationsBLK(){
		ArrayList<HashMap<String, String>> locs=null;
		Cursor cr=myDb.query(LOCATION_STATUS_TABLE, null, null, null, null, null, null);
		if(cr!=null && cr.getCount()>0){
			cr.moveToFirst();
			locs=new ArrayList<HashMap<String,String>>();
			do{
				HashMap<String, String> hm=new HashMap<String, String>();
				hm.put("locationId", ""+cr.getString(1));
				hm.put("date", cr.getString(2));
				hm.put("status", ""+cr.getInt(3));
//				Log.d("Nzm", "db query loc id:"+hm);
				locs.add(hm);
			}while(cr.moveToNext());
		}
		return locs;
	}
}
