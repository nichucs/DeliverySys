package com.example.delivervpi.utils;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import android.os.AsyncTask;
import android.util.Log;

import com.example.delivervpi.BuildConfig;

public 	class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
    Mail m = new Mail("deliveryrouting@gmail.com", "deliver123");

    public SendEmailAsyncTask(String body) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");
        String[] toArr = { "nichucs@gmail.com"};
        m.setTo(toArr);
        m.setFrom("deliveryrouting@gmail.com");
        m.setSubject("Email from Delivery routing Android App");
        m.setBody(body);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
        try {
            m.send();
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(),  "failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
