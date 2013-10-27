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

import jp.morihirosoft.twwb.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class WebViewPreference extends DialogPreference
{
	//-------------------------------------------------------------------------
	// CONSTANT
	//-------------------------------------------------------------------------
	private static final String NAMESPACE           = "http://schemas.android.com/apk/res/android";
	private static final String ATTR_DIALOG_TITLE   = "dialogTitle";
	private static final String ATTR_DIALOG_MESSAGE = "dialogMessage";

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	// Attribute.
	private String mTitle  = null;
	private String mMsgUrl = null;

	//-------------------------------------------------------------------------
	// PUBLIC METHOD
	//-------------------------------------------------------------------------
	public WebViewPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		resolveAttrs(context, attrs);
		setDialogLayoutResource(R.xml.webview_dialog);
		setDialogIcon(null);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(null);

		if (mTitle != null) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
						context.getPackageName(),
						PackageManager.GET_META_DATA);
				setDialogTitle(mTitle.concat(" Ver.").concat(packageInfo.versionName));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
		if (mMsgUrl != null) {
			WebView web = (WebView)view.findViewById(R.id.web);
			web.setBackgroundColor(Color.TRANSPARENT);
			web.loadUrl(mMsgUrl);
		}
	}

	//-------------------------------------------------------------------------
	// PRIVATE...
	//-------------------------------------------------------------------------
	private void resolveAttrs(Context context, AttributeSet attrs) {
		final Resources res = context.getResources();
		int res_id;

		// Title
		res_id = attrs.getAttributeResourceValue(NAMESPACE, ATTR_DIALOG_TITLE, 0);
		if (res_id > 0) {
			mTitle = res.getString(res_id);
		}

		// Message
		res_id = attrs.getAttributeResourceValue(NAMESPACE, ATTR_DIALOG_MESSAGE, 0);
		if (res_id > 0) {
			mMsgUrl = res.getString(res_id);
		}
	}
}
