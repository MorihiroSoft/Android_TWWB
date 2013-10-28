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

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class TwwbRecognition extends IntentService
{
	private static final boolean DEBUG = false;
	private static final String TAG = "TwwbRecognition";

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	public TwwbRecognition() {
		super("TwwbRecognition");
		if (DEBUG) Log.d(TAG, "TwwbRecognition");
	}

	@Override
	public void onHandleIntent(Intent intent) {
		if (DEBUG) Log.d(TAG, "onHandleIntent: intent="+intent);
		if (!ActivityRecognitionResult.hasResult(intent)) {
			return;
		}
		Log.d(TAG, "intent.extras="+intent.getExtras());

		ActivityRecognitionResult arr = ActivityRecognitionResult.extractResult(intent);
		DetectedActivity mpa = arr.getMostProbableActivity();

		Intent i = new Intent(Constants.ACTION_RECOGNITION);
		i.putExtra(Constants.EXTRA_TYPE,       mpa.getType());
		i.putExtra(Constants.EXTRA_CONFIDENCE, mpa.getConfidence());

		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}
}
