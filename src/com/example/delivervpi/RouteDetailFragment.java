package com.example.delivervpi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.delivervpi.dummy.Datas;
import com.example.delivervpi.dummy.DummyContent;
import com.example.delivervpi.dummy.DummyContent.Order;
import com.example.delivervpi.dummy.MyRoutes;
import com.example.delivervpi.utils.ConnectionDetector;
import com.example.delivervpi.utils.MyAsync;
import com.example.delivervpi.utils.MyStringAsync;
import com.example.delivervpi.utils.RouteDetailAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A fragment representing a single Route detail screen. This fragment is either
 * contained in a {@link RouteListActivity} in two-pane mode (on tablets) or a
 * {@link RouteDetailActivity} on handsets.
 */
public class RouteDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ROUTE_ID = "route_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
//	private DummyContent.DummyItem mItem;
	private DummyContent.Route route;
	ArrayList<Order> orders;
	/**
	 * Google map controller
	 */
	 private GoogleMap googleMap;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RouteDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ROUTE_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
//			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
//					ARG_ROUTE_ID));
			route=DummyContent.getRoute(getArguments().getInt(ARG_ROUTE_ID));
//			Log.d("Nzm", ""+route);
		}else{
//			Log.d("Nzm", "failed to get arguments");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_route_detail,
				container, false);
		initilizeMap();
//		Log.d("Nzm", "onCreateView");
		// Show the dummy content as text in a TextView.
//		if (mItem != null) {
//			((TextView) rootView.findViewById(R.id.route_detail))
//					.setText(mItem.content);
//		}

		return rootView;
	}
	   private void initilizeMap() {
	        if (googleMap == null) {
	            googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
	                    R.id.map)).getMap();
	            googleMap.setMyLocationEnabled(true);
	            // check if map is created successfully or not
	            if (googleMap == null) {
//	                Log.d("Nzm", "Create map failed");
	            }
	            else{

	            	if(route!=null){
	            		orders=(ArrayList<Order>) route.getOrders();
	            		ArrayList<String> paramlist=new ArrayList<String>();
	            		for(int i=0;i<orders.size();i++){
	            			final Order order= orders.get(i);
	            			paramlist.add(order.getLat()+","+order.getLon());
	            			LatLng lt=new LatLng(order.getLat(),order.getLon());

	            			// create marker
	            			BitmapFactory.Options opt = new BitmapFactory.Options();
	            			opt.inMutable=true;
	            			final Bitmap bmRed = BitmapFactory.decodeResource(getResources(), R.drawable.mapview_pin_small,opt);
	            			final Bitmap bmGreen = BitmapFactory.decodeResource(getResources(), R.drawable.mapview_pin_green,opt);
	            			if(i==0){
		            			Bitmap bmHome = BitmapFactory.decodeResource(getResources(), R.drawable.home,opt);
		            			MarkerOptions marker = new MarkerOptions().position(lt).title("Start");
		            			marker.icon(BitmapDescriptorFactory.fromBitmap(bmHome));
		            			googleMap.addMarker(marker);
	            				continue;
	            			}
	            			MarkerOptions marker = new MarkerOptions().position(lt).title(order.getCustomer_id()+"\n<"+order.getDestination()+">"+(order.getDelivered_time()!=null?"\nDelivered at "+order.getDelivered_time():""));
            				marker.snippet(""+order.getLoc_id());
//            				Log.d("Nzm", "Snippet:"+marker.getSnippet());
	            			if(order.isDelivered())
	            				marker.icon(BitmapDescriptorFactory.fromBitmap(bmGreen));
	            			else
		            			{
//	            				Paint paint=new Paint();
//	            				paint.setColor(Color.BLUE);
//	            				paint.setTextSize(40);
//	            				paint.setStyle(Style.FILL);
//	            				Canvas canvas=new Canvas(bmRed);
//	            				canvas.drawText("1", 0, 30, paint);
	            				marker.icon(BitmapDescriptorFactory.fromBitmap(bmRed));
		            			}
	            			// adding marker
	            			googleMap.addMarker(marker);
	            		}
            			googleMap.setInfoWindowAdapter(new RouteDetailAdapter(getActivity(), orders));
            			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
							
							@Override
							public void onInfoWindowClick(Marker marker) {
								final Marker mkr=marker;
								if(!marker.getTitle().contains("Delivered")){
									AlertDialog dlg=new AlertDialog.Builder(getActivity())
									.setMessage(marker.getTitle())
									.setTitle("Delivery Status")
									.setPositiveButton("Mark Delivered", new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
//											Toast.makeText(getActivity(), "TODO: mark as delivered", Toast.LENGTH_LONG).show();
											final SimpleDateFormat date_format=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",Locale.getDefault());
											Datas db=new Datas(getActivity());
											db.open();
											db.markOrderDelivered(mkr.getSnippet(), date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
											db.close();
											ConnectionDetector connection=new ConnectionDetector(getActivity());
											if(connection.isConnectingToInternet())
											{
												ArrayList<NameValuePair> nvp2=new ArrayList<NameValuePair>();
												nvp2.add(new BasicNameValuePair("locationId",""+mkr.getSnippet()));
												nvp2.add(new BasicNameValuePair("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime())));
												nvp2.add(new BasicNameValuePair("status","3"));
												new MyStringAsync(getActivity(), nvp2){
													protected void onPostExecute(String result) {
														try {
															JSONObject res=new JSONObject(result);
															if(!res.getString("status").equals("OK")){
																ContentValues cv=new ContentValues();
																cv.put("locationId",mkr.getSnippet());
																cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
																cv.put("status","3");
																Datas db=new Datas(getActivity());
																db.open();
																db.insertLocationBLK(cv);
																db.close();
															}
														} catch (JSONException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
													};
												}.execute("http://zappiertech.com/route/1.7/php/controller/?action=setRouteStatus");
											}else{
												ContentValues cv=new ContentValues();
												cv.put("locationId",mkr.getSnippet());
												cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
												cv.put("status","3");
//												Log.d("Nzm", "loc status update local save:"+cv.toString());
												db.open();
												db.insertLocationBLK(cv);
												db.close();
											}
											for(int j=1;j<orders.size();j++){
												Order r=orders.get(j);
												if(mkr.getSnippet().equals(""+r.getLoc_id())){
													r.setDelivered(true);
													r.setDelivered_time( date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
													
													mkr.remove();
													LatLng lt=new LatLng(r.getLat(),r.getLon());
													BitmapFactory.Options opt = new BitmapFactory.Options();
							            			opt.inMutable=true;
							            			final Bitmap bmGreen = BitmapFactory.decodeResource(getResources(), R.drawable.mapview_pin_green,opt);
							            			MarkerOptions marker = new MarkerOptions().position(lt).title(r.getCustomer_id()+"\n<"+r.getDestination()+">"+(r.getDelivered_time()!=null?"\nDelivered at "+r.getDelivered_time():""));
							            			marker.icon(BitmapDescriptorFactory.fromBitmap(bmGreen));
						            				marker.snippet(""+r.getLoc_id());
							            			googleMap.addMarker(marker);
							            			orders.remove(j);
													orders.add(j, r);
							            			googleMap.setInfoWindowAdapter(new RouteDetailAdapter(getActivity(), orders));
							            			break;
												}
											}
											int delivered_orders=1;
											for(int j=1;j<orders.size();j++){
												Order r=orders.get(j);
												if(r.isDelivered()) delivered_orders++;
											}
											if(delivered_orders==orders.size()){
												RouteListFragment.markDelivered(getActivity(), route.getRoute_id());
											}
										}
									})
									.setNegativeButton("Cancel", new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
//											for(Order r: orders){
//												Log.d("Nzm", "Order:"+r.getOrder_id()+":"+r.isDelivered());
//											}
//											
										}
									})
									.create();
									dlg.show();
									}else{
									AlertDialog dlv=new AlertDialog.Builder(getActivity())
									.setMessage(marker.getTitle())
									.setTitle("Delivery Status")
									.setPositiveButton("OK", new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.create();
									dlv.show();
									}
							}
						});
	            		if(orders.size()>1){
	            			if(route.getPolylines()==null){
	            		MyAsync async=new MyAsync(){
		            		ProgressDialog dlg;
		            		@Override
		            		protected void onPreExecute() {
		            			if(dlg==null)
		            				dlg=ProgressDialog.show(getActivity(), "Processing", "Please wait...");
		            			super.onPreExecute();
		            		}
		            		@Override
		            		protected void onPostExecute(MyRoutes result) {
		            			super.onPostExecute(result);
		            			if(dlg!=null)
									dlg.dismiss();
		            			if(result!=null){
//		            			Log.d("Nzm", "Hm:"+result.getHm());
//		            			Log.d("Nzm", "Bounds:"+result.getMbounds());
//		            			Log.d("Nzm", "Dist:"+result.getDist());
		            			Datas db=new Datas(getActivity());
		            			db.open();
//		            			Log.d("Nzm", "polylines in db:"+route.getRoute_id()+":"+db.storePolylines(""+route.getRoute_id(), result.getPoints()));
		            			db.close();
		            			googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(result.getMbounds(), 10));
		            			for(int i=0;i<result.getHm().size();i++)
		            				googleMap.addPolyline(new PolylineOptions().geodesic(true).addAll(result.getHm().get(i)).width(5).color(Color.GREEN));		            			
		            			}else{
		            				Toast.makeText(getActivity(), "Directions service not available. Check your connection", Toast.LENGTH_LONG).show();
		            				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(orders.get(0).getLat(), orders.get(0).getLon()),11));
		            			}
		            		}
		            	};
	            		String[] params=new String[paramlist.size()];
//	            		params[0]=DummyContent.STARTING_POINT;
	            		for(int i=0;i<paramlist.size();i++){
	            			params[i]=paramlist.get(i);
	            		}
		            	async.execute(params);
	            			}else{
	            				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(orders.get(0).getLat(), orders.get(0).getLon()),12));
		            			googleMap.addPolyline(new PolylineOptions().geodesic(true).addAll(route.getRoutePoints()).width(5).color(Color.GREEN));
	            			}
	            			
	            		}else{
	            			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(orders.get(0).getLat(), orders.get(0).getLon()), 13));
	            		}
		            }else{
//	            		Log.d("Nzm", "Route null");
	            	}
	            	
//	            	googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//						
//						@Override
//						public boolean onMarkerClick(Marker marker) {
//							if(!marker.getTitle().contains("Delivered")){
//							AlertDialog dlg=new AlertDialog.Builder(getActivity())
//							.setMessage(marker.getTitle())
//							.setTitle("Delivery Status")
//							.setPositiveButton("Mark Delivered", new OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									Toast.makeText(getActivity(), "TODO: mark as delivered", Toast.LENGTH_LONG).show();
//								}
//							})
//							.setNegativeButton("Cancel", new OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									// TODO Auto-generated method stub
//									
//								}
//							})
//							.create();
//							dlg.show();
//							}else{
//							AlertDialog dlv=new AlertDialog.Builder(getActivity())
//							.setMessage(marker.getTitle())
//							.setTitle("Delivery Status")
//							.setPositiveButton("OK", new OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//								}
//							})
//							.create();
//							dlv.show();
//							}
//							return false;
//						}
//					});
	            }
	        }
	   }
	   @Override
	public void onResume() {
		
		super.onResume();
	}

}
