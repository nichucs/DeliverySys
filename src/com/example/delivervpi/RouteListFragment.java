package com.example.delivervpi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.example.delivervpi.dummy.Datas;
import com.example.delivervpi.dummy.DummyContent;
import com.example.delivervpi.dummy.DummyContent.Order;
import com.example.delivervpi.dummy.DummyContent.Route;
import com.example.delivervpi.utils.ConnectionDetector;
import com.example.delivervpi.utils.MyDbAsync;
import com.example.delivervpi.utils.MyStringAsync;
import com.example.delivervpi.utils.RouteListAdapter;
import com.example.delivervpi.utils.SessionManager;

/**
 * A list fragment representing a list of Routes. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link RouteDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RouteListFragment extends ListFragment {

	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
//	public static boolean onDelivery=false;
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	Route selectedRoute;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int i);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int i) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RouteListFragment() {
		
    	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(((RouteListActivity)getActivity()).from_login){
//			loadListFromNet();
//			Toast.makeText(getActivity(), "You can select refresh from options", Toast.LENGTH_LONG).show();
//		}else{
			Datas db=new Datas(getActivity());
			db.open();
			if(db.isEmpty() || ((RouteListActivity)getActivity()).from_login)
				loadList();
			else
				loadListFromDB();
			db.close();
//		}
	}


	public void loadList() {
		ConnectionDetector net=new ConnectionDetector(getActivity());
		if( net.isConnectingToInternet() )
			loadListFromNet();
		else
			loadListFromDB();
	}
	public void refreshList(ArrayList<Route> routes){
		((RouteListAdapter)getListAdapter()).refreshContent(routes);;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
//		getView().setBackgroundResource(R.drawable.listbg);
		
	}
	@Override
	public void onResume() {
		super.onResume();
		
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Route r=(Route) listView.getAdapter().getItem(position);
		mCallbacks.onItemSelected(r.getRoute_id());
	}
	AdapterView.AdapterContextMenuInfo acmi;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	
	    ListView lv = (ListView) v;
	    acmi = (AdapterContextMenuInfo) menuInfo;
	    Route obj = (Route) lv.getItemAtPosition(acmi.position);
	    selectedRoute=obj;
	    Log.d("Nzm", "Selected route id:"+selectedRoute.getRoute_id());
	    Log.d("Nzm", "selected Route status:"+obj.getStatus());
	    menu.setHeaderTitle("Options");
	    switch(obj.getStatus()){
	    case 1:
	    	menu.add(0, 1, 1, "Start Delivery");
	    	break;
	    case 2:
	    	menu.add(0, 2, 2,"Mark as delivered");
	    	break;
	    case 3:
	    	menu.add(0, 3, 3,"Mark Pending");
	    	break;
	    default:
	    	Toast.makeText(getActivity(), "Closed", Toast.LENGTH_LONG).show();
	    }
	
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent i=new Intent(getActivity(), MonitorService.class);
		Datas db=new Datas(getActivity());
		final SimpleDateFormat date_format=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",Locale.getDefault());
		switch(item.getItemId()){
		case 1:
//			if(!onDelivery){
//				Toast.makeText(getActivity(), "TODO: Mark On Delivery", Toast.LENGTH_LONG).show();
				db.open();
				Log.d("Nzm", "selected route id:"+selectedRoute.getRoute_id()+" time:"+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
				db.markRouteOnDelivery(""+selectedRoute.getRoute_id(),date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
				db.close();
				ConnectionDetector connection=new ConnectionDetector(getActivity());
				if(connection.isConnectingToInternet())
				{
				ArrayList<NameValuePair> nvp2=new ArrayList<NameValuePair>();
				nvp2.add(new BasicNameValuePair("routeId",""+selectedRoute.getRoute_id()));
				nvp2.add(new BasicNameValuePair("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime())));
				nvp2.add(new BasicNameValuePair("status","2"));
				new MyStringAsync(getActivity(), nvp2){
					protected void onPostExecute(String result) {
						try {
							JSONObject res=new JSONObject(result);
							if(!res.getString("status").equals("OK")){
								ContentValues cv=new ContentValues();
								cv.put("routeId",""+selectedRoute.getRoute_id());
								cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
								cv.put("status","2");
								Datas db=new Datas(getActivity());
								db.open();
								db.insertOrderBLK(cv);
								db.close();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.execute("http://zappiertech.com/route/1.7/php/controller/?action=setOrderStatus");
				}else{
					ContentValues cv=new ContentValues();
					cv.put("routeId",""+selectedRoute.getRoute_id());
					cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
					cv.put("status","2");
					db.open();
					db.insertOrderBLK(cv);
					db.close();
				}
				getActivity().startService(i);
//				onDelivery=true;
				((RouteListAdapter)getListAdapter()).changeStatus(2, acmi.position);
//			}else{
//				Toast.makeText(getActivity(), "Please end the current delivery first", Toast.LENGTH_LONG).show();
//			}
			break;
		case 2:
//			Toast.makeText(getActivity(), "TODO: Mark as Delivered", Toast.LENGTH_LONG).show();
			markDelivered(getActivity(),selectedRoute.getRoute_id());
			((RouteListAdapter)getListAdapter()).changeStatus(3, acmi.position);
//			onDelivery=false;
			if(!DummyContent.isOnDelivery())
				getActivity().stopService(i);
			break;
		case 3:
//			Toast.makeText(getActivity(), "TODO: Mark as Pending", Toast.LENGTH_LONG).show();
			db.open();
			db.markRoutePending(""+selectedRoute.getRoute_id());
			db.close();
			ConnectionDetector connection2=new ConnectionDetector(getActivity());
			if(connection2.isConnectingToInternet())
			{
			ArrayList<NameValuePair> nvp1=new ArrayList<NameValuePair>();
			nvp1.add(new BasicNameValuePair("routeId",""+selectedRoute.getRoute_id()));
			nvp1.add(new BasicNameValuePair("date",null));
			nvp1.add(new BasicNameValuePair("status","1"));
			new MyStringAsync(getActivity(), nvp1){
				protected void onPostExecute(String result) {
					try {
						JSONObject res=new JSONObject(result);
						if(!res.getString("status").equals("OK")){
							ContentValues cv=new ContentValues();
							cv.put("routeId",""+selectedRoute.getRoute_id());
							cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
							cv.put("status","1");
							Datas db=new Datas(getActivity());
							db.open();
							db.insertOrderBLK(cv);
							db.close();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.execute("http://zappiertech.com/route/1.7/php/controller/?action=setOrderStatus");
			}else{
				ContentValues cv=new ContentValues();
				cv.put("routeId",""+selectedRoute.getRoute_id());
				cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
				cv.put("status","1");
				db.open();
				db.insertOrderBLK(cv);
				db.close();
			}
			((RouteListAdapter)getListAdapter()).changeStatus(1, acmi.position);
			break;
		default:
			Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_LONG).show();
		}
		return super.onContextItemSelected(item);
	}

	public static void markDelivered(final Context context,final int routeId) {
		final SimpleDateFormat date_format=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",Locale.getDefault());
		final Datas db=new Datas(context);
		db.open();
		db.markRouteDelivered(""+routeId,date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
		db.close();
		ConnectionDetector connection1=new ConnectionDetector(context);
		if(connection1.isConnectingToInternet())
		{
		ArrayList<NameValuePair> nvp3=new ArrayList<NameValuePair>();
		nvp3.add(new BasicNameValuePair("routeId",""+routeId));
		nvp3.add(new BasicNameValuePair("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime())));
		nvp3.add(new BasicNameValuePair("status","3"));
		new MyStringAsync(context, nvp3){
			protected void onPostExecute(String result) {
				try {
					JSONObject res=new JSONObject(result);
					if(!res.getString("status").equals("OK")){
						ContentValues cv=new ContentValues();
						cv.put("routeId",""+routeId);
						cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
						cv.put("status","3");
						Datas db=new Datas(context);
						db.open();
						db.insertOrderBLK(cv);
						db.close();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.execute("http://zappiertech.com/route/1.7/php/controller/?action=setOrderStatus");
		}else{
			ContentValues cv=new ContentValues();
			cv.put("routeId",""+routeId);
			cv.put("date",""+date_format.format( Calendar.getInstance(Locale.getDefault()).getTime()));
			cv.put("status","3");
			db.open();
			db.insertOrderBLK(cv);
			db.close();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	private void loadListFromNet() {
		ArrayList<NameValuePair> nvp=new ArrayList<NameValuePair>();
		nvp.add(new BasicNameValuePair("uid",new SessionManager(getActivity()).getUid()));
		MyStringAsync async=new MyStringAsync(getActivity(), nvp){
			final ProgressDialog dlg=new ProgressDialog(getActivity());
        	protected void onPreExecute() {
        		super.onPreExecute();
        		dlg.setTitle("Loading Orders");
        		dlg.setMessage("Please wait..");
        		dlg.show();
        	};
			@Override
			protected void onPostExecute(String result) {
				if(dlg!=null && dlg.isShowing())
        			dlg.dismiss();
				try {
					JSONObject obj=new JSONObject(result);
					if(obj.getString("status").equals("OK")){
						JSONArray orders= obj.getJSONArray("message");
						if(orders.length()>0)
						{
							DummyContent.ROUTES.clear();
							Datas db=new Datas(getActivity());
							db.open();
							db.clear();
							db.close();
							
							for(int i=0;i<orders.length();i++){
								JSONObject route=orders.getJSONObject(i);
								String routId=route.getString("routId");
								String sts=route.getString("status");
//								if(sts.equals("2")) onDelivery=true;
								Log.d("Nzm", "Routes:"+routId);
								 getOrders(routId,route.getString("dateAdded"),sts);
							}
						}else{
							Toast.makeText(getActivity(), "No new orders for now. Refresh after some time.", Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(getActivity(), "Wrong user id. Please logout and login again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}
		};
		async.execute("http://zappiertech.com/route/1.7/php/controller/?action=getOrders");	
	}

	protected void getOrders(String routId, String dateAdded, String sts) {
		final String routIdF=routId; final String dateAddedF=dateAdded, status=sts; 
		Log.d("Nzm", "final routid:"+routIdF);
		final ArrayList<Order> res=new ArrayList<DummyContent.Order>();
		ArrayList<NameValuePair> nvp=new ArrayList<NameValuePair>();
		nvp.add(new BasicNameValuePair("routeId",routId));
		MyStringAsync async=new MyStringAsync(getActivity(), nvp){
//			final ProgressDialog dlg=new ProgressDialog(getActivity());
        	
        	protected void onPreExecute() {
        		super.onPreExecute();
//        		dlg.setTitle("Loading");
//        		dlg.setMessage("Routes..");
//        		dlg.show();
        	};
        	protected void onPostExecute(String result) {
//				if(dlg!=null && dlg.isShowing())
//        			dlg.dismiss();
				try {
						JSONObject obj=new JSONObject(result);
						if(obj.getString("status").equals("OK")){
							JSONArray orders= obj.getJSONArray("message");
							if(orders.length()>0){
								for(int j=0;j<orders.length();j++){
									JSONObject o=orders.getJSONObject(j);
									Log.d("Nzm", "New order loc id:"+o.getInt("id"));
									res.add(new Order(o.getInt("id"),o.getInt("orderId"), o.getString("customerId"), o.getString("location"), o.getDouble("lat"), o.getDouble("lng"), (o.getInt("status")==1?false:true), o.getString("dateAdded"), (o.getInt("status")==1?null:o.getString("dateUpdate"))));
								}
								Log.d("Nzm", "on post nvp routeid:"+this.getNvp().get(0).getValue());
								Route r=new Route(Integer.parseInt(this.getNvp().get(0).getValue()), res, Integer.parseInt(status), dateAddedF, null, null);
								DummyContent.addRoute(r);
								Datas db=new Datas(getActivity());
								db.open();
								db.insert(r);
								db.close();
								setListAdapter(new RouteListAdapter((ArrayList<Route>) DummyContent.ROUTES, getActivity()));
							}
					} 
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		};
		async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://zappiertech.com/route/1.7/php/controller/?action=getRoutes");			
	}
	private void loadListFromDB() {

		new MyDbAsync(getActivity()){
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				setListAdapter(new RouteListAdapter((ArrayList<Route>) DummyContent.ROUTES, getActivity()));
			};
		}.execute();
	}
}
