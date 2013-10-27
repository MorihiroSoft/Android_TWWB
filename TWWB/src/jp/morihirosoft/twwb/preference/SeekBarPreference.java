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
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements
SeekBar.OnSeekBarChangeListener
{
	//-------------------------------------------------------------------------
	// CONSTANT
	//-------------------------------------------------------------------------
	private static final String NAMESPACE       = "http://schemas.android.com/apk/res/jp.morihirosoft.twwb";
	private static final String ATTR_SKB_LABEL  = "skbLabel";
	private static final String ATTR_SKB_SUFFIX = "skbSuffix";
	private static final String ATTR_SKB_MIN    = "skbMin";
	private static final String ATTR_SKB_MAX    = "skbMax";

	//-------------------------------------------------------------------------
	// MEMBER
	//-------------------------------------------------------------------------
	// View parts.
	private TextView mLabel;
	private TextView mSeekText;
	private SeekBar  mSeekBar;

	// Attribute.
	private int mLabelResId;
	private int mSeekResId;
	private int mSeekMin;
	private int mSeekMax;

	// Temporary value.
	private int mSeekVal;
	private String mSuffix = null;

	// Getter/Setter
	public interface IGetSetSeekVal {
		int get();
		void set(int val);
	}
	public IGetSetSeekVal mGetSetSeekVal = null;

	//-------------------------------------------------------------------------
	// PUBLIC/PROTECTED METHOD
	//-------------------------------------------------------------------------
	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		resolveAttrs(context, attrs);
		setDialogLayoutResource(R.xml.seekbar_dialog);
		setDialogIcon(null);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
	}

	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mSeekVal = mGetSetSeekVal.get() - mSeekMin;

		if (mSeekResId != 0) {
			mSuffix = getContext().getResources().getString(mSeekResId);	
		}

		mLabel = (TextView)view.findViewById(R.id.label);
		if (mLabel != null) {
			if (mLabelResId == 0) {
				mLabel.setVisibility(View.GONE);
			} else {
				mLabel.setText(mLabelResId);
			}
		}

		mSeekText = (TextView)view.findViewById(R.id.seektext);
		mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);

		mSeekBar.setMax(mSeekMax - mSeekMin);
		mSeekBar.setProgress(mSeekVal);
		mSeekBar.setOnSeekBarChangeListener(this);

		updateSeekBar();
	}

	@Override
	public void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			mGetSetSeekVal.set(mSeekVal + mSeekMin);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		mSeekVal = progress;
		updateSeekBar();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	//-------------------------------------------------------------------------
	// PRIVATE...
	//-------------------------------------------------------------------------
	private void resolveAttrs(Context context, AttributeSet attrs) {
		mLabelResId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_SKB_LABEL, 0);
		mSeekResId  = attrs.getAttributeResourceValue(NAMESPACE, ATTR_SKB_SUFFIX, 0);
		mSeekMin    = attrs.getAttributeIntValue(NAMESPACE, ATTR_SKB_MIN, 0);
		mSeekMax    = attrs.getAttributeIntValue(NAMESPACE, ATTR_SKB_MAX, 0);
	}

	private void updateSeekBar() {
		String t = String.valueOf(mSeekVal + mSeekMin);
		mSeekText.setText(mSuffix == null ? t : t.concat(" ").concat(mSuffix));
	}
}
