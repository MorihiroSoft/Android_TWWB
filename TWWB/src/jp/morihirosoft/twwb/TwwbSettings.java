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

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

public class TwwbSettings
{
	private static final boolean DEBUG = false;
	private static final String TAG = "TwwbSettings";

	//-------------------------------------------------------------------------
	// CONSTANT
	//-------------------------------------------------------------------------
	private static final boolean DEF_BLOCK_ON_FOOT    = true;
	private static final boolean DEF_BLOCK_ON_BICYCLE = true;
	private static final boolean DEF_BLOCK_IN_VEHICLE = false;
	private static final boolean DEF_BLOCK_TILTING    = false;
	private static final int     DEF_PENDING          = 0;
	private static final int     DEF_ALPHA            = 25;
	private static final boolean DEF_STATUSBAR        = false;

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	private final String KEY_SETTINGS_ONOFF;
	private final String KEY_SETTINGS_BLOCK_ON_FOOT;
	private final String KEY_SETTINGS_BLOCK_ON_BICYCLE;
	private final String KEY_SETTINGS_BLOCK_IN_VEHICLE;
	private final String KEY_SETTINGS_BLOCK_TILTING;
	private final String KEY_SETTINGS_PENDING;
	private final String KEY_SETTINGS_ALPHA;
	private final String KEY_SETTINGS_STATUSBAR;

	private final TwwbApplication   mApp;
	private final SharedPreferences mPrefs;

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	public TwwbSettings(TwwbApplication app) {
		if (DEBUG) Log.d(TAG, "TwwbSettings");
		Resources res = app.getResources();
		KEY_SETTINGS_ONOFF            = res.getString(R.string.key_settings_onoff);
		KEY_SETTINGS_BLOCK_ON_FOOT    = res.getString(R.string.key_settings_block_on_foot);
		KEY_SETTINGS_BLOCK_ON_BICYCLE = res.getString(R.string.key_settings_block_on_bicycle);
		KEY_SETTINGS_BLOCK_IN_VEHICLE = res.getString(R.string.key_settings_block_in_vehicle);
		KEY_SETTINGS_BLOCK_TILTING    = res.getString(R.string.key_settings_block_tilting);
		KEY_SETTINGS_PENDING          = res.getString(R.string.key_settings_pending);
		KEY_SETTINGS_ALPHA            = res.getString(R.string.key_settings_alpha);
		KEY_SETTINGS_STATUSBAR        = res.getString(R.string.key_settings_statusbar);

		mApp   = app;
		mPrefs = app.getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
	}

	//---------
	// Get key
	//---------
	public final String getKeyOnOff() {
		return KEY_SETTINGS_ONOFF;
	}
	public final String getKeyBlockOnFoot() {
		return KEY_SETTINGS_BLOCK_ON_FOOT;
	}
	public final String getKeyBlockOnBicycle() {
		return KEY_SETTINGS_BLOCK_ON_BICYCLE;
	}
	public final String getKeyBlockInVehicle() {
		return KEY_SETTINGS_BLOCK_IN_VEHICLE;
	}
	public final String getKeyBlockTilting() {
		return KEY_SETTINGS_BLOCK_TILTING;
	}
	public final String getKeyPending() {
		return KEY_SETTINGS_PENDING;
	}
	public final String getKeyAlpha() {
		return KEY_SETTINGS_ALPHA;
	}
	public final String getKeyStatusbar() {
		return KEY_SETTINGS_STATUSBAR;
	}

	//--------
	// Is key
	//--------
	public boolean isKeyOnOff(final String key) {
		return KEY_SETTINGS_ONOFF.equals(key);
	}
	public boolean isKeyBlockOnFoot(final String key) {
		return KEY_SETTINGS_BLOCK_ON_FOOT.equals(key);
	}
	public boolean isKeyBlockOnBicycle(final String key) {
		return KEY_SETTINGS_BLOCK_ON_BICYCLE.equals(key);
	}
	public boolean isKeyBlockInVehicle(final String key) {
		return KEY_SETTINGS_BLOCK_IN_VEHICLE.equals(key);
	}
	public boolean isKeyBlockTilting(final String key) {
		return KEY_SETTINGS_BLOCK_TILTING.equals(key);
	}
	public boolean isKeyPending(final String key) {
		return KEY_SETTINGS_PENDING.equals(key);
	}
	public boolean isKeyAlpha(final String key) {
		return KEY_SETTINGS_ALPHA.equals(key);
	}
	public boolean isKeyStatusbar(final String key) {
		return KEY_SETTINGS_STATUSBAR.equals(key);
	}

	//--------------
	// Get settings
	//--------------
	public boolean getOnOff() {
		return mPrefs.getBoolean(KEY_SETTINGS_ONOFF, mApp.isRunningService());
	}
	public boolean getBlockOnFoot() {
		return mPrefs.getBoolean(KEY_SETTINGS_BLOCK_ON_FOOT, DEF_BLOCK_ON_FOOT);
	}
	public boolean getBlockOnBicycle() {
		return mPrefs.getBoolean(KEY_SETTINGS_BLOCK_ON_BICYCLE, DEF_BLOCK_ON_BICYCLE);
	}
	public boolean getBlockInVehicle() {
		return mPrefs.getBoolean(KEY_SETTINGS_BLOCK_IN_VEHICLE, DEF_BLOCK_IN_VEHICLE);
	}
	public boolean getBlockTilting() {
		return mPrefs.getBoolean(KEY_SETTINGS_BLOCK_TILTING, DEF_BLOCK_TILTING);
	}
	public int getPending() {
		return mPrefs.getInt(KEY_SETTINGS_PENDING, DEF_PENDING);
	}
	public int getAlpha() {
		return mPrefs.getInt(KEY_SETTINGS_ALPHA, DEF_ALPHA);
	}
	public boolean getStatusbar() {
		return mPrefs.getBoolean(KEY_SETTINGS_STATUSBAR, DEF_STATUSBAR);
	}

	//--------------
	// Set settings
	//--------------
	public void setOnOff(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_ONOFF, val).commit();
	}
	public void setBlockOnFoot(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_BLOCK_ON_FOOT, val).commit();
	}
	public void setBlockOnBicycle(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_BLOCK_ON_BICYCLE, val).commit();
	}
	public void setBlockInVehicle(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_BLOCK_IN_VEHICLE, val).commit();
	}
	public void setBlockTilting(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_BLOCK_TILTING, val).commit();
	}
	public void setPending(int val) {
		mPrefs.edit().putInt(KEY_SETTINGS_PENDING, val).commit();
	}
	public void setAlpha(int val) {
		mPrefs.edit().putInt(KEY_SETTINGS_ALPHA, val).commit();
	}
	public void setStatusbar(boolean val) {
		mPrefs.edit().putBoolean(KEY_SETTINGS_STATUSBAR, val).commit();
	}
}
