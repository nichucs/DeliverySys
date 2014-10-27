package com.example.delivervpi.dummy;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.delivervpi.utils.GeopointWaypoints;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MyRoutes {
	HashMap<Integer, ArrayList<LatLng>>hm;
	ArrayList<Integer>dist;
	LatLngBounds mbounds;
	String points;


	public MyRoutes(HashMap<Integer, ArrayList<LatLng>> hm,
			ArrayList<Integer> dist) {
		super();
		this.hm = hm;
		this.dist = dist;
	}

	public MyRoutes(HashMap<Integer, ArrayList<LatLng>> hm,
			ArrayList<Integer> dist, LatLngBounds mbounds,String points) {
		super();
		this.hm = hm;
		this.dist = dist;
		this.mbounds = mbounds;
		this.points=points;
	
	}

	public HashMap<Integer, ArrayList<LatLng>> getHm() {
		return hm;
	}

	public void setHm(HashMap<Integer, ArrayList<LatLng>> hm) {
		this.hm = hm;
	}

	public ArrayList<Integer> getDist() {
		return dist;
	}

	public void setDist(ArrayList<Integer> dist) {
		this.dist = dist;
	}

	public LatLngBounds getMbounds() {
		return mbounds;
	}

	public void setMbounds(LatLngBounds mbounds) {
		this.mbounds = mbounds;
	}
	
	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public ArrayList<LatLng> getRoutePoints(){
		GeopointWaypoints converter=new GeopointWaypoints();
		return (ArrayList<LatLng>) converter.decodePoly(points);
	}
}

