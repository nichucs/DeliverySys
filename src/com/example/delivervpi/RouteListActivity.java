package com.example.delivervpi;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.delivervpi.dummy.DummyContent;
import com.example.delivervpi.dummy.DummyContent.Route;
import com.example.delivervpi.utils.SessionManager;

/**
 * An activity representing a list of Routes. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link RouteDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link RouteListFragment} and the item details (if present) is a
 * {@link RouteDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link RouteListFragment.Callbacks} interface to listen for item selections.
 */
public class RouteListActivity extends FragmentActivity implements
		RouteListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	boolean from_login;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session=new SessionManager(this);
		setContentView(R.layout.activity_route_list);
		from_login=getIntent().getBooleanExtra("login", false);
		if (findViewById(R.id.route_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((RouteListFragment) getSupportFragmentManager().findFragmentById(
					R.id.route_list)).setActivateOnItemClick(true);
		}else{
			if(from_login){
				RouteListFragment list_frag1=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);
				list_frag1.loadList();
			}
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link RouteListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int i) {
//		Log.d("Nzm", "inside onItemSelected arg:"+i);
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(RouteDetailFragment.ARG_ROUTE_ID, i);
			RouteDetailFragment fragment = new RouteDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.route_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, RouteDetailActivity.class);
			detailIntent.putExtra(RouteDetailFragment.ARG_ROUTE_ID, i);
			startActivity(detailIntent);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Refresh");
		menu.add(0, 0, 2, "Logout");
		Menu sub=menu.addSubMenu(0, 2, 3, "Filter");
		sub.add(1, 3, 1, "Pending");
		sub.add(1, 4, 2, "On delivery");
		sub.add(1, 5, 3, "Delivered");
		sub.add(1, 6, 4, "All");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			session.logoutUser();
			finish();
			break;
		case 1:
			RouteListFragment list_frag1=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);
			list_frag1.loadList();
			break;
		case 3:
			RouteListFragment list_frag3=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);
			ArrayList<Route> routes=new ArrayList<DummyContent.Route>();
			for(Route r:DummyContent.ROUTES){
				if(r.getStatus()==1)
					routes.add(r);
			}
			list_frag3.refreshList(routes);
			break;
		case 4:
			RouteListFragment list_frag4=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);
			ArrayList<Route> routes1=new ArrayList<DummyContent.Route>();
			for(Route r:DummyContent.ROUTES){
				if(r.getStatus()==2)
					routes1.add(r);
			}
			list_frag4.refreshList(routes1);
			break;
		case 5:
			RouteListFragment list_frag5=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);
			ArrayList<Route> routes2=new ArrayList<DummyContent.Route>();
			for(Route r:DummyContent.ROUTES){
				if(r.getStatus()==3)
					routes2.add(r);
			}
			list_frag5.refreshList(routes2);
			break;
		case 6:
			RouteListFragment list_frag6=(RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list);			
			list_frag6.refreshList((ArrayList<Route>) DummyContent.ROUTES);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
