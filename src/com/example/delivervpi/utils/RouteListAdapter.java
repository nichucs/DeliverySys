package com.example.delivervpi.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.delivervpi.R;
import com.example.delivervpi.dummy.DummyContent.Order;
import com.example.delivervpi.dummy.DummyContent.Route;

public class RouteListAdapter extends BaseAdapter {

	ArrayList<Route> routes;
	Context context;
	
	public RouteListAdapter(ArrayList<Route> routes,Context context) {
		super();
		this.routes = routes;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return routes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return routes.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi =convertView;
		if(convertView==null){
			vi=inflater.inflate(R.layout.routes_list_item, null);
		}
		TextView route=(TextView) vi.findViewById(R.id.txt_route);
		TextView pending=(TextView) vi.findViewById(R.id.txt_pending);
		TextView time=(TextView) vi.findViewById(R.id.txt_time);
		
		Route r=routes.get(position);
		ArrayList<Order> order= (ArrayList<Order>) r.getOrders();
		int status= r.getStatus();
		String ttime= r.getOrder_time();
		
		String troute="";
		for(int i=1;i<order.size();i++){
			Order o= order.get(i);
			troute+=o.getOrder_id()+" >";
		}
		if(troute.length()>0)
			troute=troute.substring(0, troute.length()-2);
		route.setText(troute);
		
		switch(status){
		case 1:
			pending.setText("Pending");
			pending.setTextColor(Color.RED);
			break;
		case 2:
			pending.setText("On Delivery");
			pending.setTextColor(Color.rgb(204, 204, 0));
			break;
		case 3:
			pending.setText("Delivered");
			pending.setTextColor(Color.GREEN);
			
			break;
		case 4:
			pending.setText("Closed");
			pending.setTextColor(Color.GREEN);
			break;
		default:
			pending.setText("Pending");
			pending.setTextColor(Color.RED);
		}
		
		time.setText(ttime);
		
		return vi;
	}
	
	public void changeStatus(int status,int position){
		routes.get(position).setStatus(status);
		notifyDataSetChanged();
	}
	public void refreshContent(ArrayList<Route> routes){
		this.routes=routes;
		notifyDataSetChanged();
	}

}
