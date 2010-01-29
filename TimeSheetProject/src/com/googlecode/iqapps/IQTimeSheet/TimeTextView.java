/*
 * Copyright 2010 TimeSheet authors.
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

package com.googlecode.iqapps.IQTimeSheet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckedTextView;

/**
 * Extension of the CheckedTextView to give a better user experience. Currently
 * not used in the application.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeTextView extends CheckedTextView {

	private static final String TAG = "TimeTextView";

	/**
	 * @param context
	 */
	public TimeTextView(Context context) {
		super(context);
		Log.d(TAG, "Constructor");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "Constructor");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "Constructor");
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#drawableStateChanged()
	 */
	@Override
	protected void drawableStateChanged() {
		// TODO Auto-generated method stub
		Log.d(TAG, "drawableStateChanged");
		super.drawableStateChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#isChecked()
	 */
	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		Log.d(TAG, "isChecked");
		return super.isChecked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#onCreateDrawableState(int)
	 */
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreateDrawableState");
		return super.onCreateDrawableState(extraSpace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDraw");
		super.onDraw(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#setChecked(boolean)
	 */
	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setChecked");
		super.setChecked(checked);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CheckedTextView#setCheckMarkDrawable(android.graphics.
	 * drawable.Drawable)
	 */
	@Override
	public void setCheckMarkDrawable(Drawable d) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setCheckMarkDrawable");
		super.setCheckMarkDrawable(d);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#setCheckMarkDrawable(int)
	 */
	@Override
	public void setCheckMarkDrawable(int resid) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setCheckMarkDrawable");
		super.setCheckMarkDrawable(resid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#setPadding(int, int, int, int)
	 */
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setPadding");
		super.setPadding(left, top, right, bottom);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CheckedTextView#toggle()
	 */
	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		Log.d(TAG, "toggle");
		super.toggle();
	}
}
