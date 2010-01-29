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

import com.googlecode.iqapps.AdapterWrapper;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Wrapper to provide a custom adapter for the time list. Currently only used to
 * instrument activity on the wrapper.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeListWrapper extends AdapterWrapper {

	private static final String TAG = "TimeListWrapper";

	/**
	 * @param delegate
	 */
	public TimeListWrapper(ListAdapter delegate) {
		super(delegate);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "Constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#areAllItemsEnabled()
	 */
	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		Log.d(TAG, "areAllItemsEnabled? "
				+ (super.areAllItemsEnabled() ? "Yes" : "No"));
		return super.areAllItemsEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getCount: " + Integer.toString(super.getCount()));
		return super.getCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItem for position " + Integer.toString(position) + ": "
				+ super.getItem(position).toString());
		return super.getItem(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItemId for position " + Integer.toString(position)
				+ ": " + Long.toString(super.getItemId(position)));
		return super.getItemId(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItemViewType for position " + Integer.toString(position)
				+ ": " + Integer.toString(super.getItemViewType(position)));
		return super.getItemViewType(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getView: "
				+ super.getView(position, convertView, parent).toString());
		return super.getView(position, convertView, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getViewTypeCount: "
				+ Integer.toString(super.getViewTypeCount()));
		return super.getViewTypeCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		Log.d(TAG, "hasStableIds? " + (super.hasStableIds() ? "Yes" : "No"));
		return super.hasStableIds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		Log.d(TAG, "isEmpty? " + (super.isEmpty() ? "Yes" : "No"));
		return super.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.iqapps.AdapterWrapper#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "isEnabled at position " + Integer.toString(position)
				+ (super.isEnabled(position) ? ": Yes" : ": No"));
		return super.isEnabled(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.googlecode.iqapps.AdapterWrapper#registerDataSetObserver(android.database
	 * .DataSetObserver)
	 */
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		super.registerDataSetObserver(observer);
		Log.d(TAG, "registerDataSetObserver");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.googlecode.iqapps.AdapterWrapper#unregisterDataSetObserver(android.database
	 * .DataSetObserver)
	 */
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		super.unregisterDataSetObserver(observer);
		Log.d(TAG, "unregisterDataSetObserver");
	}
}
