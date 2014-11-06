package com.example.delivervpi.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.delivervpi.utils.GeopointWaypoints;
import com.google.android.gms.maps.model.LatLng;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	public static final String STARTING_POINT="1.453649,103.832625";
	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	public static List<Route> ROUTES = new ArrayList<DummyContent.Route>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
	public static HashMap<Integer, Route> ROUTE_MAP = new HashMap<Integer, DummyContent.Route>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "Item 1"));
		addItem(new DummyItem("2", "Item 2"));
		addItem(new DummyItem("3", "Item 3"));
		
//		List<Order> lst1=new ArrayList<Order>();
//		lst1.add(new Order(1005, "9846464646", "41 Jalan Bahagia", 1.327151, 103.856262, false, "3-4-2014 6:30am", null));
//		lst1.add(new Order(1009, "9846464646", "22 Meyappa Chettiar Rd", 1.331270, 103.867935, false, "3-4-2014 6:30am", null));
//		addRoute(new Route(1,lst1, 0, "3-4-2014 6:30am", null, null));
//
//		List<Order> lst2=new ArrayList<Order>();
//		lst2.add(new Order(1006, "9846464646", "36 Surin Ave", 1.355296, 103.882355, false, "3-4-2014 6:00am", null));
//		lst2.add(new Order(1007, "9846464646", "694 Upper Serangoon Rd", 1.351863, 103.876861, true, "3-4-2014 6:00am", "3-4-2014 9:30am"));
//		lst2.add(new Order(1008, "9846464646", "38 Kim Chuan Rd", 1.337791, 103.889221, false, "3-4-2014 6:00am", null));
//		addRoute(new Route(2,lst2, 1, "3-4-2014 6:00am", "3-4-2014 9:00am", null));
//
//		List<Order> lst3=new ArrayList<Order>();
//		lst3.add(new Order(1002, "9846464646", "Bukit Timah", 1.343969, 103.771805, true, "3-4-2014 5:00am", "3-4-2014 8:30am"));
//		addRoute(new Route(3,lst3, 2, "3-4-2014 5:00am", "3-4-2014 8:00am", "3-4-2014 8:30am"));
		
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	public static boolean addRoute(Route route){
		for(Route r: ROUTES){
			if(r.route_id==route.route_id)
				return false;
		}
		ROUTES.add(route);
		ROUTE_MAP.put(route.route_id, route);
//		Log.d("Nzm", "map size: "+ROUTE_MAP.size());
		return true;
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
	/**
	 * Model for single order destination
	 */
	public static class Order{
		int order_id;int loc_id;
		String customer_id;
		String destination;
		double lat,lon;
		boolean delivered;
		String order_time;
		String delivered_time;
		
		public Order(int order_id, String customer_id, String destination,
				double lat, double lon, String order_time) {
			super();
			this.order_id = order_id;
			this.customer_id = customer_id;
			this.destination = destination;
			this.lat = lat;
			this.lon = lon;
			this.delivered=false;
			this.order_time = order_time;
		}

		public Order(int loc_id, int order_id, String customer_id, String destination,
				double lat, double lon, boolean delivered, String order_time,
				String delivered_time) {
			super();
			this.order_id = order_id;
			this.loc_id=loc_id;
			this.customer_id = customer_id;
			this.destination = destination;
			this.lat = lat;
			this.lon = lon;
			this.delivered = delivered;
			this.order_time = order_time;
			this.delivered_time = delivered_time;
		}

		public int getLoc_id() {
			return loc_id;
		}

		public void setLoc_id(int loc_id) {
			this.loc_id = loc_id;
		}

		public int getOrder_id() {
			return order_id;
		}

		public void setOrder_id(int order_id) {
			this.order_id = order_id;
		}

		public String getCustomer_id() {
			return customer_id;
		}

		public void setCustomer_id(String customer_id) {
			this.customer_id = customer_id;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public boolean isDelivered() {
			return delivered;
		}

		public void setDelivered(boolean delivered) {
			this.delivered = delivered;
		}

		public String getOrder_time() {
			return order_time;
		}

		public void setOrder_time(String order_time) {
			this.order_time = order_time;
		}

		public String getDelivered_time() {
			return delivered_time;
		}

		public void setDelivered_time(String delivered_time) {
			this.delivered_time = delivered_time;
		}
		
	}
	/**
	 * Model for Routes, including several orders
	 */
	public static class Route{
		int route_id;
		List<Order> orders;
		int status;// 1,2,3 for pending>on delivery>delivered
		String order_time;
		String Start_time;
		String end_time;
		String polylines;
		public Route(int route_id,List<Order> orders, int status, String order_time,
				String start_time, String end_time) {
			super();
			this.route_id=route_id;
			this.orders = orders;
			this.status = status;
			this.order_time = order_time;
			Start_time = start_time;
			this.end_time = end_time;
		}
		
		
		public Route(int route_id, List<Order> orders, int status,
				String order_time, String start_time, String end_time,
				String polylines) {
			super();
			this.route_id = route_id;
			this.orders = orders;
			this.status = status;
			this.order_time = order_time;
			Start_time = start_time;
			this.end_time = end_time;
			this.polylines = polylines;
		}


		public int getRoute_id() {
			return route_id;
		}

		public void setRoute_id(int route_id) {
			this.route_id = route_id;
		}

		public List<Order> getOrders() {
			return orders;
		}
		public void setOrders(List<Order> orders) {
			this.orders = orders;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getOrder_time() {
			return order_time;
		}
		public void setOrder_time(String order_time) {
			this.order_time = order_time;
		}
		public String getStart_time() {
			return Start_time;
		}
		public void setStart_time(String start_time) {
			Start_time = start_time;
		}
		public String getEnd_time() {
			return end_time;
		}
		public void setEnd_time(String end_time) {
			this.end_time = end_time;
		}


		public String getPolylines() {
			return polylines;
		}


		public void setPolylines(String polylines) {
			this.polylines = polylines;
		}
		public ArrayList<LatLng> getRoutePoints(){
			GeopointWaypoints converter=new GeopointWaypoints();
			return (ArrayList<LatLng>) converter.decodePoly(polylines);
		}
	}
	public static Route getRoute(int route_id){
		Route r=null;
		for(Route r1:ROUTES){
			if(r1.route_id==route_id){
				r=r1;
				break;
			}
		}
		return r;
	}
	public static boolean isOnDelivery(){
		for(Route r1:ROUTES){
			if(r1.status==2)
				return true;
		}
		return false;
	}
}
