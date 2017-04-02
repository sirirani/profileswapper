package com.mode;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.util.Log;

public class Broadcast extends BroadcastReceiver {
	// private ITelephony telephonyService;
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "SMSBroadcastReceiver";
	Dbhandler myDbHelper;
	private AudioManager maudio;
	SQLiteDatabase Mydatabase;
	MediaPlayer mPlay ;
	SQLiteDatabase db;
	Context context = null;
	

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context=context;
		Log.i(TAG, "Intent recieved: " + intent.getAction());
		maudio = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
		if (intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Log.i(TAG, "Message recieved: ");
				Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					System.out.println("message is.." + messages[i]);
					Log.i(TAG,
							"Message recieved: " + messages[0].getMessageBody());
					String mode = messages[0].getMessageBody().toLowerCase();
					System.out.println("message body is.." + mode);
					String m_mode = getMode(mode, context);
					

					
					System.out.println("mode is...." + m_mode);
					changemode(m_mode);

				}
				if (messages.length > -1) {
					Log.i(TAG,
							"Message recieved: " + messages[0].getMessageBody());
				}
			}
		} else {
			Log.i(TAG, "in else... ");

		}
	}

	private void changemode(String m_mode) {
		// TODO Auto-generated method stub

		if (m_mode == null) {
		} else if (m_mode.contains("silent")) {

			System.out.println("The phone state is changing to silent mode");
			int n = maudio.getRingerMode();

			System.out.println("The phone state is changing to silent mode "
					+ n);
			maudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Log.i(TAG, "Change to Silent");
		} else if (m_mode.contains("ring")) {
			System.out.println("The phone state is changing to ring mode");

			maudio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

			// Log.i(TAG, "Changed to Ring ");

			maudio.adjustVolume(AudioManager.ADJUST_RAISE,
					AudioManager.FLAG_PLAY_SOUND);
			System.out.println("volume increased..");
			
			/*MediaPlayer mPlay = MediaPlayer.create(context, R.raw.youraudiofile);
			mPlay.start();*/
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();         
			notification.sound = Uri.parse("android.resource://com.your.package/raw/youraudiofile.mp3");
			nm.notify(0, notification);
		}

		else if (m_mode.contains("vibrate")) {
			System.out.println("The phone state is changing to vibrate mode");
			maudio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			Log.i(TAG, "Changed to Vibrate");
		}
		else if(m_mode.contains("flight")){
			
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON,  1);
		}

	}

	private String getMode(String mode, Context context) {
		// TODO Auto-generated method stub
		String m = null;
		this.myDbHelper = new Dbhandler(context);
		FetchingData();
		System.out.println("inside getmodde");
		Mydatabase = myDbHelper.getReadableDatabase();
		Cursor c = Mydatabase.rawQuery("SELECT * FROM mode", null);
	/*	Cursor c = Mydatabase.rawQuery("SELECT Mode FROM mode where text='"
				+ mode + "' ", null);*/
		// System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$ fetchdata completed @@@@@@@@@@@@@@@@@@@@@");
		c.moveToFirst();

		if (c != null) {
			System.out.println("column index is..");
			int i = c.getColumnIndex("Mode");
			
			if (c.getCount() > 0)

				while(c.moveToNext()){
					if(mode.contains(c.getString(c.getColumnIndex("text"))))
				m = c.getString(i).toString();
				}
			System.out.println(" mode in getmode.. profile swaper  " + m);
		}
		return m;
	}

	private void FetchingData() {
		// TODO Auto-generated method stub
		try {

			myDbHelper.onCreateDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}
		try {

			myDbHelper.openDataBase();
			db = myDbHelper.getReadableDatabase();
			System.out.println("executed");

		} catch (SQLException sqle) {

			throw sqle;

		}
		// TODO Auto-generated method stu

	}
	
	
}
