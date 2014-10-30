package com.example.delivervpi;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.delivervpi.dummy.Datas;
import com.example.delivervpi.utils.MyStringAsync;
import com.example.delivervpi.utils.SendEmailAsyncTask;
import com.example.delivervpi.utils.SessionManager;
import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends Activity {

	EditText txtUsername, txtPassword;
	SessionManager session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		 session = new SessionManager(getApplicationContext());                
         
	        // Email, Password input text
	        txtUsername = (EditText) findViewById(R.id.uid);
	        txtPassword = (EditText) findViewById(R.id.upassword); 
	        
		 if(getCurrentFocus()!=null) {
		        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		    }
		 
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 // Get username, password from EditText
               final String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                 
                // Check if username, password is filled                
                if(username.trim().length() > 0 && password.trim().length() > 0){
                	ArrayList<NameValuePair> nvp=new ArrayList<NameValuePair>();
                	nvp.add(new BasicNameValuePair("username", username.trim()));
                	nvp.add(new BasicNameValuePair("password", password.trim()));
                	nvp.add(new BasicNameValuePair("regId", session.getGCMkey()));


                    MyStringAsync async=new MyStringAsync(LoginActivity.this, nvp){
                    	ProgressDialog dlg=new ProgressDialog(LoginActivity.this);
                    	
                    	protected void onPreExecute() {
                    		super.onPreExecute();
                    		dlg.setTitle("Signing in");
                    		dlg.setMessage("Please wait..");
                    		dlg.show();
                    	};
                    	protected void onPostExecute(String result) {
                    		if(dlg!=null && dlg.isShowing())
                    			dlg.dismiss();
                    		try {
								JSONObject obj=new JSONObject(result);
								if(obj.getString("status").equals("OK")){

	                                 GCMRegistrar.setRegisteredOnServer(getApplicationContext(), true);
	                                 // Creating user login session
	                                 session.createLoginSession(username, obj.getJSONObject("message").getString("id"));
	                                  Datas db=new Datas(getBaseContext());
	                                  db.open();
	                                  db.clear();
	                                  db.close();
	                                 // Staring MainActivity
	                                Intent i=new Intent(getBaseContext(), RouteListActivity.class);
	                                i.putExtra("login", true);
	                 				startActivity(i);
	                 				finish();
								}else{
	                                 // username / password doesn't match
	                                 Toast.makeText(LoginActivity.this, "Login failed.. Username/Password is incorrect", Toast.LENGTH_LONG).show();
	                             }
							} catch (JSONException e) {
								SendEmailAsyncTask err=new SendEmailAsyncTask(""+result);
								err.execute();
                                Toast.makeText(LoginActivity.this, "Something went wrong. Report sent to developer mail", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
							}
                    		      
                    	};
                    };
                    async.execute("http://zappiertech.com/route/1.7/php/controller/?action=login");
                	}else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                	Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_LONG).show();
                }
                 
				
			}
		});
	}
	@Override
	protected void onResume() {
		if(session.checkLogin()){
			finish();
		}
		else{
			if(session.getGCMkey()==null){
				GCMRegistrar.checkDevice(this);
				String regId = GCMRegistrar.getRegistrationId(this);
				if (regId.equals("")) {
					// Registration is not present, register now with GCM			
					GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
				} else {
					session.storeGCMkey(regId);
				}
			}
		}
		super.onResume();
	}
	
	
	
}
