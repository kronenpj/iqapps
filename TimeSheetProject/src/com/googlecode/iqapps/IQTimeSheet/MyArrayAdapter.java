package com.googlecode.iqapps.IQTimeSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter<T> extends ArrayAdapter<T> {
	/**
	 * The resource indicating what views to inflate to display the content of
	 * this array adapter.
	 */
	private int mResource;

	/**
	 * If the inflated resource is not a TextView, {@link #mFieldId} is used to
	 * find a TextView inside the inflated views hierarchy. This field must
	 * contain the identifier that matches the one defined in the resource file.
	 */
	private int mFieldId = 0;

	private Context mContext;
	private LayoutInflater mInflater;

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		init(context, resource, textViewResourceId);
	}

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		init(context, textViewResourceId, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		init(context, textViewResourceId, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int resource,
			int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, Arrays.asList(objects));
		init(context, resource, textViewResourceId);
	}

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		init(context, textViewResourceId, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public MyArrayAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		init(context, resource, textViewResourceId);
	}

	private void init(Context context, int resource, int textViewResourceId) {
		mContext = context;
		mResource = resource;
		mFieldId = textViewResourceId;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * {@inheritDoc}
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (mFieldId == 0) {
				// If no custom field is assigned, assume the whole resource is
				// a TextView
				text = (TextView) view;
			} else {
				// Otherwise, find the TextView field within the layout
				text = (TextView) view.findViewById(mFieldId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"ArrayAdapter requires the resource ID to be a TextView", e);
		}

		T item = getItem(position);
		if (item instanceof CharSequence) {
			text.setText((CharSequence) item);
		} else {
			text.setText(item.toString());
		}

		text.setTextSize((float) TimeSheetActivity.prefs.getFontSizeTaskList());

		return view;
	}
}
