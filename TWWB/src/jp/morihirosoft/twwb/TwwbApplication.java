/*
 * Copyright (C) 2013 Morihiro Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.morihirosoft.twwb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.Log;

public class TwwbApplication extends Application implements
SharedPreferences.OnSharedPreferenceChangeListener
{
	private static final boolean DEBUG = false;
	private static final String TAG = "TwwbApplication";

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	private TwwbSettings mSettings = null;

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	@Override
	public void onCreate() {
		if (DEBUG) Log.d(TAG, "onCreate");
		super.onCreate();
		initLog();
		getSharedPreferences(Constants.SHARED_PREFS_NAME, 0).
		registerOnSharedPreferenceChangeListener(this);
		mSettings = new TwwbSettings(this);
		if (mSettings.getOnOff() && !isRunningService()) {
			Intent i = new Intent(this, TwwbService.class);
			startService(i);
		}
	}

	@Override
	public void onTerminate() {
		if (DEBUG) Log.d(TAG, "onTerminate");
		super.onTerminate();
		getSharedPreferences(Constants.SHARED_PREFS_NAME, 0).
		unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (DEBUG) Log.d(TAG, "onSharedPreferenceChanged: key="+key);
		if (mSettings.isKeyOnOff(key)) {
			Intent i = new Intent(this, TwwbService.class);
			if (mSettings.getOnOff()) {
				if (DEBUG) Log.e(TAG, "startService");
				startService(i);
			} else {
				if (DEBUG) Log.e(TAG, "stopService");
				stopService(i);
			}
		}
		else if (mSettings.isKeyStatusbar(key)) {
			updateStatusbarIcon();
		}
	}

	public boolean isRunningService() {
		if (DEBUG) Log.d(TAG, "isRunningService");
		final String service_name = TwwbService.class.getName();
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> rsis = am.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo rsi : rsis) {
			if (service_name.equals(rsi.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public TwwbSettings getSettings() {
		if (DEBUG) Log.d(TAG, "getSettings");
		return mSettings;
	}

	public void updateStatusbarIcon() {
		updateStatusbarIcon(mSettings.getStatusbar());
	}
	public void updateStatusbarIcon(boolean onoff) {
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		if (onoff && isRunningService()) {
			Resources res = getResources();
			Intent i = new Intent(this, TwwbPreferenceActivity.class);
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
			Notification n = new Notification.Builder(this)
			.setContentTitle(res.getString(R.string.app_name))
			.setContentText(res.getString(R.string.str_notify_text))
			.setSmallIcon(R.drawable.ic_statusbar)
			.setOngoing(true)
			.setContentIntent(pi)
			.setWhen(System.currentTimeMillis())
			.getNotification();
			nm.notify(Constants.NOTIFY_ID, n);
		} else {
			nm.cancel(Constants.NOTIFY_ID);
		}
	}

	public void log(final String msg) {
		if (DEBUG) Log.d(TAG, "log: msg="+msg);
		OutputStreamWriter osw = null;
		try {
			FileOutputStream fos = openFileOutput(Constants.LOG, MODE_PRIVATE|MODE_APPEND);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write((String)DateFormat.format("yyyy/MM/dd kk:mm:ss, ",
					Calendar.getInstance()));
			osw.write(msg);
			osw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (osw != null) {
				try {
					osw.flush();
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//-------------------------------------------------------------------------
	// PRIVATE...
	//-------------------------------------------------------------------------
	private void initLog() {
		if (DEBUG) Log.d(TAG, "initLog");
		FileInputStream fis = null;
		try {
			fis = openFileInput(Constants.LOG);
		} catch (FileNotFoundException e) {
			log(Constants.LOG_INIT);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
