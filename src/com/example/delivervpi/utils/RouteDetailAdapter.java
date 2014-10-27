package com.example.delivervpi.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.delivervpi.R;
import com.example.delivervpi.dummy.DummyContent.Order;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class RouteDetailAdapter implements GoogleMap.InfoWindowAdapter {

	View v;
	Context c;
	int order_id;
	String customer_id;
	String destination;
	String delivered_time;
	ArrayList<Order> orders;
	
	public RouteDetailAdapter(Context c, int order_id, String customer_id,
			String destination, String delivered_time) {
		super();
		this.c = c;
		this.order_id = order_id;
		this.customer_id = customer_id;
		this.destination = destination;
		this.delivered_time = delivered_time;
	}

	public RouteDetailAdapter(Context c, ArrayList<Order> orders) {
		this.c=c;
		this.orders=orders;
	}

	@Override
	public View getInfoContents(Marker marker) {
		
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		if(!marker.getTitle().equals("Start")){
		LayoutInflater inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v=inflater.inflate(R.layout.order_info_item, null);

			TextView order_id=(TextView) v.findViewById(R.id.textView1);
			TextView customer_id=(TextView) v.findViewById(R.id.textView2);
			TextView pending=(TextView) v.findViewById(R.id.textView3);
			TextView destination=(TextView) v.findViewById(R.id.textView4);
			TextView deliv_time=(TextView) v.findViewById(R.id.textView5);
			
			for(Order order: orders){
				if(marker.getTitle().substring(marker.getTitle().indexOf("<")+1, marker.getTitle().indexOf(">")).equals(order.getDestination())){
					this.order_id=order.getOrder_id();
					this.customer_id=order.getCustomer_id();
					this.destination=order.getDestination();
					if(order.isDelivered()){
						this.delivered_time=order.getDelivered_time();
					}else
						this.delivered_time=null;
					break;
				}
			}
			
			order_id.setText(this.order_id+"");
			customer_id.setText(this.customer_id+"");
			destination.setText(this.destination);
			pending.setTextSize(14);
			if(delivered_time!=null)
			{
				pending.setText("Delivered");
				pending.setTextColor(Color.GREEN);
				deliv_time.setVisibility(View.VISIBLE);
				deliv_time.setText(this.delivered_time);
				}
			else{
				pending.setText("Pending");
				pending.setTextColor(Color.RED);
				deliv_time.setVisibility(View.GONE);
			}
		return v;
		}else{
			return null;
		}
	}



}
