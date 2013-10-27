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
package jp.morihirosoft.twwb.preference;

import jp.morihirosoft.twwb.Constants;
import jp.morihirosoft.twwb.R;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class LogPreference extends DialogPreference
{
	public LogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.xml.webview_dialog);
		setDialogIcon(null);
		setDialogTitle(R.string.str_settings_log_title);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(null);
	}

	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
		((WebView)view.findViewById(R.id.web)).loadUrl(
				"file:///"+getContext().getFilesDir()+"/"+Constants.LOG);
	}
}
