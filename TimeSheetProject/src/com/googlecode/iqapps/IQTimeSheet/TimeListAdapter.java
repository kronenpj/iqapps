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
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Wrapper for the SimpleCursorAdapter class. Currently used only to instrument
 * use of the cursor.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeListAdapter extends SimpleCursorAdapter {

	private static final String TAG = "TimeListAdapter";

	/**
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public TimeListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "Constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(view, context, cursor);
		Log.d(TAG, "bindView(" + view.toString() + "," + context.toString()
				+ "," + cursor.toString() + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#changeCursor(android.database.Cursor)
	 */
	@Override
	public void changeCursor(Cursor c) {
		// TODO Auto-generated method stub
		super.changeCursor(c);
		Log.d(TAG, "changeCursor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#convertToString(android.database.Cursor
	 * )
	 */
	@Override
	public CharSequence convertToString(Cursor cursor) {
		// TODO Auto-generated method stub
		Log.d(TAG, "convertToString: " + super.convertToString(cursor));
		return super.convertToString(cursor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#getCursorToStringConverter()
	 */
	@Override
	public CursorToStringConverter getCursorToStringConverter() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getCursorToStringConverter");
		return super.getCursorToStringConverter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#getStringConversionColumn()
	 */
	@Override
	public int getStringConversionColumn() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getStringConversionColumn:"
				+ Integer.toString(super.getStringConversionColumn()));
		return super.getStringConversionColumn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#getViewBinder()
	 */
	@Override
	public ViewBinder getViewBinder() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getViewBinder: " + super.getViewBinder().toString());
		return super.getViewBinder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#newDropDownView(android.content.Context
	 * , android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "newDropDownView");
		return super.newDropDownView(context, cursor, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "newView(" + context.toString() + "," + cursor.toString()
				+ "," + parent.toString() + ")");
		return super.newView(context, cursor, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#setCursorToStringConverter(android
	 * .widget.SimpleCursorAdapter.CursorToStringConverter)
	 */
	@Override
	public void setCursorToStringConverter(
			CursorToStringConverter cursorToStringConverter) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setCursorToStringConverter");
		super.setCursorToStringConverter(cursorToStringConverter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#setStringConversionColumn(int)
	 */
	@Override
	public void setStringConversionColumn(int stringConversionColumn) {
		// TODO Auto-generated method stub
		super.setStringConversionColumn(stringConversionColumn);
		Log.d(TAG, "setStringConversionColumn");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeandroid.widget.SimpleCursorAdapter#setViewBinder(android.widget.
	 * SimpleCursorAdapter.ViewBinder)
	 */
	@Override
	public void setViewBinder(ViewBinder viewBinder) {
		// TODO Auto-generated method stub
		super.setViewBinder(viewBinder);
		Log.d(TAG, "setViewBinder");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#setViewImage(android.widget.ImageView,
	 * java.lang.String)
	 */
	@Override
	public void setViewImage(ImageView v, String value) {
		// TODO Auto-generated method stub
		super.setViewImage(v, value);
		Log.d(TAG, "setViewImage");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter#setViewText(android.widget.TextView,
	 * java.lang.String)
	 */
	@Override
	public void setViewText(TextView v, String text) {
		// TODO Auto-generated method stub
		super.setViewText(v, text);
		Log.d(TAG, "setViewText");
	}

}
