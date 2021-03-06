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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TwwbBootReceiver extends BroadcastReceiver
{
	private static final boolean DEBUG = false;
	private static final String TAG = "TwwbBootReceiver";

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	@Override
	public void onReceive(Context context, Intent intent) {
		if (DEBUG) Log.d(TAG, "onReceive");
		TwwbApplication app = (TwwbApplication)context.getApplicationContext();
		TwwbSettings ts = app.getSettings();
		if (ts.getOnOff() && !app.isRunningService()) {
			Intent i = new Intent(context, TwwbService.class);
			context.startService(i);
		}
	}
}
