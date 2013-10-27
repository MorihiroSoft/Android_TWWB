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

import jp.morihirosoft.twwb.preference.SeekBarPreference;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

public class TwwbPreferenceActivity extends PreferenceActivity
{
	private static final boolean DEBUG = true;
	private static final String TAG = "TwwbPreferenceActivity";

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	private TwwbPreferenceFragment mFragment = null;

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		if (DEBUG) Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mFragment = new TwwbPreferenceFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (DEBUG) Log.d(TAG, "onWindowFocusChanged");
		super.onWindowFocusChanged(hasFocus);
		mFragment.update();
	}

	//-------------------------------------------------------------------------
	// INNER CLASS
	//-------------------------------------------------------------------------
	public static class TwwbPreferenceFragment extends PreferenceFragment
	{
		private static final boolean DEBUG = TwwbPreferenceActivity.DEBUG;
		private static final String TAG = "TwwbPreferenceFragment";

		//---------------------------------------------------------------------
		// MEMBER
		//---------------------------------------------------------------------
		private TwwbApplication    mApp                = null;
		private TwwbSettings       mSettings           = null;
		private SwitchPreference   mPrefOnOff          = null;
		private SeekBarPreference  mPrefPending        = null;
		private CheckBoxPreference mPrefBlockOnFoot    = null;
		private CheckBoxPreference mPrefBlockOnBicycle = null;
		private CheckBoxPreference mPrefBlockInVehicle = null;
		private CheckBoxPreference mPrefBlockTilting   = null;
		private SeekBarPreference  mPrefAlpha          = null;
		private CheckBoxPreference mPrefStatusbar      = null;

		//---------------------------------------------------------------------
		// PUBLIC METHOD
		//---------------------------------------------------------------------
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			if (DEBUG) Log.d(TAG, "onCreate");
			super.onCreate(savedInstanceState);
			getPreferenceManager().setSharedPreferencesName(Constants.SHARED_PREFS_NAME);
			addPreferencesFromResource(R.xml.settings);

			mApp = (TwwbApplication)getActivity().getApplicationContext();
			mSettings = mApp.getSettings();

			mPrefOnOff          = (SwitchPreference)findPreference(mSettings.getKeyOnOff());
			mPrefPending        = (SeekBarPreference)findPreference(mSettings.getKeyPending());
			mPrefBlockOnFoot    = (CheckBoxPreference)findPreference(mSettings.getKeyBlockOnFoot());
			mPrefBlockOnBicycle = (CheckBoxPreference)findPreference(mSettings.getKeyBlockOnBicycle());
			mPrefBlockInVehicle = (CheckBoxPreference)findPreference(mSettings.getKeyBlockInVehicle());
			mPrefBlockTilting   = (CheckBoxPreference)findPreference(mSettings.getKeyBlockTilting());
			mPrefAlpha          = (SeekBarPreference)findPreference(mSettings.getKeyAlpha());
			mPrefStatusbar      = (CheckBoxPreference)findPreference(mSettings.getKeyStatusbar());

			mPrefPending.mGetSetSeekVal = new SeekBarPreference.IGetSetSeekVal() {
				@Override
				public int get() {
					return mSettings.getPending();
				}
				@Override
				public void set(int val) {
					mSettings.setPending(val);
				}
			};

			mPrefAlpha.mGetSetSeekVal = new SeekBarPreference.IGetSetSeekVal() {
				@Override
				public int get() {
					return mSettings.getAlpha();
				}
				@Override
				public void set(int val) {
					mSettings.setAlpha(val);
				}
			};
		}

		@Override
		public void onResume() {
			if (DEBUG) Log.d(TAG, "onResume");
			super.onResume();
			update();
		}

		@Override
		public void onPause() {
			if (DEBUG) Log.d(TAG, "onPause");
			super.onPause();
		}

		public void update() {
			if (DEBUG) Log.d(TAG, "update");
			mPrefOnOff.setChecked(mApp.isRunningService());
			mPrefBlockOnFoot.setChecked(mSettings.getBlockOnFoot());
			mPrefBlockOnBicycle.setChecked(mSettings.getBlockOnBicycle());
			mPrefBlockInVehicle.setChecked(mSettings.getBlockInVehicle());
			mPrefBlockTilting.setChecked(mSettings.getBlockTilting());
			mPrefStatusbar.setChecked(mSettings.getStatusbar());

			final Resources res = getResources();
			mPrefPending.setSummary(
					res.getString(R.string.str_settings_pending_summary1)
					.concat(" ")
					.concat(String.valueOf(mSettings.getPending()))
					.concat(" ")
					.concat(res.getString(R.string.str_settings_pending_summary2)));
			mPrefAlpha.setSummary(
					res.getString(R.string.str_settings_alpha_summary1)
					.concat(" ")
					.concat(String.valueOf(mSettings.getAlpha()))
					.concat(" ")
					.concat(res.getString(R.string.str_settings_alpha_summary2)));
		}
	}
}
