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

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

public class TwwbService extends Service implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
View.OnClickListener
{
	private static final boolean DEBUG = true;
	private static final String TAG = "TwwbService";

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	private TwwbSettings              mSettings          = null;
	private View                      mBlockScreen       = null;
	private ActivityRecognitionClient mARClient          = null;
	private PendingIntent             mIntentRecognition = null;
	private boolean                   mIsRegistered      = false;
	private boolean                   mIsCalling         = false;
	private long                      mStartTime         = -1;

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	@Override
	public void onCreate() {
		if (DEBUG) Log.d(TAG, "onCreate");
		super.onCreate();
		TwwbApplication app = (TwwbApplication)getApplicationContext();
		mSettings = app.getSettings();
		app.log(Constants.LOG_START);
		app.updateStatusbarIcon();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG) Log.d(TAG, "onStartCommand: intent="+intent);
		registerReceivers();
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			startRecognition();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (DEBUG) Log.d(TAG, "onDestroy");
		stopRecognition();
		unregisterReceivers();
		TwwbApplication app = (TwwbApplication)getApplicationContext();
		app.log(Constants.LOG_STOP);
		app.updateStatusbarIcon(false);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		if (DEBUG) Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (DEBUG) Log.d(TAG, "onConnected");
		Intent i = new Intent(this, TwwbRecognition.class);
		mIntentRecognition = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mARClient.requestActivityUpdates(1000, mIntentRecognition);
	}

	@Override
	public void onDisconnected() {
		if (DEBUG) Log.d(TAG, "onDisconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		if (DEBUG) Log.d(TAG, "onConnectionFailed");
	}

	@Override
	public void onClick(View v) {
		if (DEBUG) Log.d(TAG, "onClick: v="+v);
		switch(v.getId()) {
		case R.id.btn_unblock:
			temporaryUnblockScreen();
			break;
		}
	}

	//-------------------------------------------------------------------------
	// PRIVATE...
	//-------------------------------------------------------------------------
	private final BroadcastReceiver mReceiverScreenOn = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG) Log.d(TAG, "onReceive: ACTION_SCREEN_ON: intent="+intent);
			startRecognition();
		}
	};

	private final BroadcastReceiver mReceiverScreenOff = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG) Log.d(TAG, "onReceive: ACTION_SCREEN_OFF: intent="+intent);
			stopRecognition();
		}
	};

	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String number) {
			if (DEBUG) Log.d(TAG, "onCallStateChanged: state="+state);
			switch(state) {
			case TelephonyManager.CALL_STATE_IDLE:
				mIsCalling = false;
				break;
			case TelephonyManager.CALL_STATE_RINGING:
			case TelephonyManager.CALL_STATE_OFFHOOK:
				mIsCalling = true;
				break;
			}
			if (mIsCalling) {
				unblockScreen();
			}
		}
	};

	private final BroadcastReceiver mReceiverRecognition = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG) Log.d(TAG, "onReceive: ACTION_RECOGNITION: intent="+intent);
			boolean block = false;
			switch(intent.getIntExtra(Constants.EXTRA_TYPE, 0)) {
			case DetectedActivity.IN_VEHICLE:
				block = mSettings.getBlockInVehicle();
				break;
			case DetectedActivity.ON_BICYCLE:
				block = mSettings.getBlockOnBicycle();
				break;
			case DetectedActivity.ON_FOOT:
				block = mSettings.getBlockOnFoot();
				break;
			case DetectedActivity.STILL:
				break;
			case DetectedActivity.UNKNOWN:
				break;
			case DetectedActivity.TILTING:
				block = mSettings.getBlockTilting();
				break;
			}
			if (block) {
				blockScreen();
			} else {
				unblockScreen();
			}
		}
	};

	private void registerReceivers() {
		if (DEBUG) Log.d(TAG, "registerReceivers");
		if (!mIsRegistered) {
			mIsRegistered = true;
			registerReceiver(mReceiverScreenOn,
					new IntentFilter(Intent.ACTION_SCREEN_ON));
			registerReceiver(mReceiverScreenOff,
					new IntentFilter(Intent.ACTION_SCREEN_OFF));
			TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(mPhoneStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			lbm.registerReceiver(mReceiverRecognition,
					new IntentFilter(Constants.ACTION_RECOGNITION));
		}
	}

	private void unregisterReceivers() {
		if (DEBUG) Log.d(TAG, "unregisterReceivers");
		if (mIsRegistered) {
			mIsRegistered = false;
			unregisterReceiver(mReceiverScreenOn);
			unregisterReceiver(mReceiverScreenOff);
			TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(mPhoneStateListener,
					PhoneStateListener.LISTEN_NONE);
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			lbm.unregisterReceiver(mReceiverRecognition);
		}
	}

	private void startRecognition() {
		if (DEBUG) Log.d(TAG, "startRecognition");
		if (mARClient == null) {
			mARClient = new ActivityRecognitionClient(this, this, this);
			mARClient.connect();
		}
		mStartTime = SystemClock.uptimeMillis();
	}

	private void stopRecognition() {
		if (DEBUG) Log.d(TAG, "stopRecognition");
		if (mARClient != null && mARClient.isConnected()) {
			mARClient.removeActivityUpdates(mIntentRecognition);
			mARClient.disconnect();
			mARClient = null;
		}
		unblockScreen();
	}

	private void blockScreen() {
		if (DEBUG) Log.d(TAG, "blockScreen");
		if (mBlockScreen == null) {
			if (mIsCalling) {
				return;
			}
			if (SystemClock.uptimeMillis() - mStartTime
					< mSettings.getPending() * 1000) {
				return;
			}
			mBlockScreen = LayoutInflater.from(this).inflate(R.layout.view_block, null);
			int alpha = 255 * mSettings.getAlpha() / 100;
			mBlockScreen.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
			((ImageView)mBlockScreen.findViewById(R.id.img_logo)).setAlpha(alpha);
			((Button)mBlockScreen.findViewById(R.id.btn_unblock)).setOnClickListener(this);

			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.width  = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
			params.type   = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags  = 0;
			params.format = PixelFormat.TRANSLUCENT;

			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			wm.addView(mBlockScreen, params);
		}
	}

	private void unblockScreen() {
		if (DEBUG) Log.d(TAG, "unblockScreen");
		if (mBlockScreen != null) {
			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mBlockScreen);
			mBlockScreen = null;
		}
	}

	private void temporaryUnblockScreen() {
		if (DEBUG) Log.d(TAG, "temporaryUnblockScreen");
		unblockScreen();
		mStartTime = SystemClock.uptimeMillis();
	}
}
