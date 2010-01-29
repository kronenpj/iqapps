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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Wrapper for the ListView class. Currently only used to instrument use of the
 * ListView.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TaskListView extends ListView {

	private static final String TAG = "TaskListView";

	/**
	 * @param context
	 */
	public TaskListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "Constructor");
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public TaskListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "Constructor");
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TaskListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "Constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#addFooterView(android.view.View,
	 * java.lang.Object, boolean)
	 */
	@Override
	public void addFooterView(View v, Object data, boolean isSelectable) {
		// TODO Auto-generated method stub
		Log.d(TAG, "addFooterView");
		super.addFooterView(v, data, isSelectable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#addFooterView(android.view.View)
	 */
	@Override
	public void addFooterView(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "addFooterView");
		super.addFooterView(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#addHeaderView(android.view.View,
	 * java.lang.Object, boolean)
	 */
	@Override
	public void addHeaderView(View v, Object data, boolean isSelectable) {
		// TODO Auto-generated method stub
		Log.d(TAG, "addHeaderView");
		super.addHeaderView(v, data, isSelectable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#addHeaderView(android.view.View)
	 */
	@Override
	public void addHeaderView(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "addHeaderView");
		super.addHeaderView(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#canAnimate()
	 */
	@Override
	protected boolean canAnimate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "canAnimate");
		return super.canAnimate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#clearChoices()
	 */
	@Override
	public void clearChoices() {
		// TODO Auto-generated method stub
		Log.d(TAG, "clearChoices");
		super.clearChoices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.d(TAG, "dispatchDraw");
		super.dispatchDraw(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "dispatchKeyEvent");
		return super.dispatchKeyEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#findViewTraversal(int)
	 */
	@Override
	protected View findViewTraversal(int id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "findViewTraversal");
		return super.findViewTraversal(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#findViewWithTagTraversal(java.lang.Object)
	 */
	@Override
	protected View findViewWithTagTraversal(Object tag) {
		// TODO Auto-generated method stub
		Log.d(TAG, "findViewWithTagTraversal");
		return super.findViewWithTagTraversal(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getAdapter()
	 */
	@Override
	public ListAdapter getAdapter() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getAdapter");
		return super.getAdapter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getCheckedItemPosition()
	 */
	@Override
	public int getCheckedItemPosition() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getCheckedItemPosition");
		return super.getCheckedItemPosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getCheckedItemPositions()
	 */
	@Override
	public SparseBooleanArray getCheckedItemPositions() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getCheckedItemPositions");
		return super.getCheckedItemPositions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getChoiceMode()
	 */
	@Override
	public int getChoiceMode() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getChoiceMode");
		return super.getChoiceMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getDivider()
	 */
	@Override
	public Drawable getDivider() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getDivider");
		return super.getDivider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getDividerHeight()
	 */
	@Override
	public int getDividerHeight() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getDividerHeight");
		return super.getDividerHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getFooterViewsCount()
	 */
	@Override
	public int getFooterViewsCount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getFooterViewsCount");
		return super.getFooterViewsCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getHeaderViewsCount()
	 */
	@Override
	public int getHeaderViewsCount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getHeaderViewsCount");
		return super.getHeaderViewsCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getItemsCanFocus()
	 */
	@Override
	public boolean getItemsCanFocus() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItemsCanFocus");
		return super.getItemsCanFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#getMaxScrollAmount()
	 */
	@Override
	public int getMaxScrollAmount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getMaxScrollAmount");
		return super.getMaxScrollAmount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#isItemChecked(int)
	 */
	@Override
	public boolean isItemChecked(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "isItemChecked");
		return super.isItemChecked(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#layoutChildren()
	 */
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		Log.d(TAG, "layoutChildren");
		super.layoutChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onFinishInflate");
		super.onFinishInflate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onFocusChanged(boolean, int,
	 * android.graphics.Rect)
	 */
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onFocusChanged");
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onKeyMultiple(int, int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onKeyMultiple");
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onKeyUp");
		return super.onKeyUp(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ListView#onRestoreInstanceState(android.os.Parcelable)
	 */
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRestoreInstanceState");
		super.onRestoreInstanceState(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onSaveInstanceState()
	 */
	@Override
	public Parcelable onSaveInstanceState() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSaveInstanceState");
		return super.onSaveInstanceState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onTouchEvent");
		return super.onTouchEvent(ev);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#performItemClick(android.view.View, int,
	 * long)
	 */
	@Override
	public boolean performItemClick(View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "performItemClick");
		return super.performItemClick(view, position, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#removeFooterView(android.view.View)
	 */
	@Override
	public boolean removeFooterView(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "removeFooterView");
		return super.removeFooterView(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#removeHeaderView(android.view.View)
	 */
	@Override
	public boolean removeHeaderView(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "removeHeaderView");
		return super.removeHeaderView(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ListView#requestChildRectangleOnScreen(android.view.View,
	 * android.graphics.Rect, boolean)
	 */
	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rect,
			boolean immediate) {
		// TODO Auto-generated method stub
		Log.d(TAG, "requestChildRectangleOnScreen");
		return super.requestChildRectangleOnScreen(child, rect, immediate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setAdapter");
		super.setAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setCacheColorHint(int)
	 */
	@Override
	public void setCacheColorHint(int color) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setCacheColorHint");
		super.setCacheColorHint(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setChoiceMode(int)
	 */
	@Override
	public void setChoiceMode(int choiceMode) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setChoiceMode");
		super.setChoiceMode(choiceMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ListView#setDivider(android.graphics.drawable.Drawable)
	 */
	@Override
	public void setDivider(Drawable divider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setDivider");
		super.setDivider(divider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setDividerHeight(int)
	 */
	@Override
	public void setDividerHeight(int height) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setDividerHeight");
		super.setDividerHeight(height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setItemChecked(int, boolean)
	 */
	@Override
	public void setItemChecked(int position, boolean value) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setItemChecked");
		super.setItemChecked(position, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setItemsCanFocus(boolean)
	 */
	@Override
	public void setItemsCanFocus(boolean itemsCanFocus) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setItemsCanFocus");
		super.setItemsCanFocus(itemsCanFocus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setSelection(int)
	 */
	@Override
	public void setSelection(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setSelection");
		super.setSelection(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setSelectionAfterHeaderView()
	 */
	@Override
	public void setSelectionAfterHeaderView() {
		// TODO Auto-generated method stub
		Log.d(TAG, "setSelectionAfterHeaderView");
		super.setSelectionAfterHeaderView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setSelectionFromTop(int, int)
	 */
	@Override
	public void setSelectionFromTop(int position, int y) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setSelectionFromTop");
		super.setSelectionFromTop(position, y);
	}

}
