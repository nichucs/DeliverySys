package com.example.delivervpi.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.delivervpi.dummy.Datas;
import com.example.delivervpi.dummy.DummyContent;

public class MyDbAsync extends AsyncTask<Void, Integer, Void> {
	Context c;
	ProgressDialog dlg;
	public MyDbAsync(Context c) {
		super();
		this.c = c;
	}

	@Override
	protected void onPreExecute() {
		dlg=new ProgressDialog(c);
		dlg.setTitle("Loading Orders");
		dlg.setMessage("Please wait..");
		dlg.show();
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(Void... params) {
		Datas db=new Datas(c);
		db.open();
		DummyContent.ROUTES.clear();
		DummyContent.ROUTES=db.getAllRoutesList();
		db.close();
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		dlg.dismiss();
		super.onPostExecute(result);
	}

}
