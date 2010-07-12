/*
 * Copyright 2010 TipCalc authors.
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

package com.googlecode.iqapps.IQTipCalc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity for the TipCalc project. Implements the top-level user
 * interface for the application.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TipCalcActivity extends Activity {
	static PreferenceHelper prefs;

	private static final String TAG = "TipCalcActivity";
	private static final int PREFS_CODE = 0x01;
	private static final int ABOUT_CODE = 0x02;
	private static final int MENU_SETTINGS = 0x11;
	private static final int MENU_ABOUT = 0x12;

	public String applicationName;
	public TextView[] childFloat;
	public TextView[] childPercent;
	public TextView billTotal;
	public TextView coupon;
	public TextView grandTotal;
	public TextView tax;
	public TextView tip;
	public TextView couponPercent;
	public TextView taxPercent;
	public TextView tipPercent;
	public double billTotalValue;
	public double couponValue;
	public double grandTotalValue;
	public double taxValue;
	public double tipValue;
	public double couponPercentValue;
	public double taxPercentValue;
	public double tipPercentValue;

	private static enum element {
		BILLTOTAL, TIP, TIPP, TAX, TAXP, GRANDTOTAL, COUPON
	};

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme);
		setTitle(R.string.app_name);
		setContentView(R.layout.main);
		Log.d(TAG, "onCreate.");

		prefs = new PreferenceHelper(this);

		taxPercentValue = prefs.getTaxPercentDefault();
		tipPercentValue = prefs.getTipPercentDefault();

		Resources res = getResources();
		applicationName = res.getString(R.string.app_name);

		Button clearButton = (Button) findViewById(R.id.clear);
		clearButton.setOnClickListener(mButtonListener);

		billTotal = (TextView) findViewById(R.id.total);
		coupon = (TextView) findViewById(R.id.coupon);
		grandTotal = (TextView) findViewById(R.id.grandtotal);
		tax = (TextView) findViewById(R.id.tax);
		tip = (TextView) findViewById(R.id.tip);

		childFloat = new TextView[] { billTotal, coupon, grandTotal, tax, tip };
		for (int count = 0; count < childFloat.length; count++) {
			try {
				final int index = count;
				childFloat[index].setOnFocusChangeListener(mTextViewListener);
			} catch (NullPointerException e) {
				Toast.makeText(TipCalcActivity.this, "NullPointerException",
						Toast.LENGTH_SHORT).show();
			}
		}

		couponPercent = (TextView) findViewById(R.id.couponplabel);
		taxPercent = (TextView) findViewById(R.id.taxpercent);
		tipPercent = (TextView) findViewById(R.id.tipplabel);

		taxPercent.setOnFocusChangeListener(mTextViewListener);

		childPercent = new TextView[] { couponPercent, taxPercent, tipPercent };

		// Map out the order of fields traversed when "Next" is pressed.
		billTotal.setNextFocusDownId(R.id.coupon);
		billTotal.setNextFocusLeftId(R.id.coupon);
		coupon.setNextFocusDownId(R.id.tip);
		coupon.setNextFocusLeftId(R.id.tip);
		tip.setNextFocusDownId(R.id.tippercent);
		tip.setNextFocusLeftId(R.id.tippercent);
		tipPercent.setNextFocusDownId(R.id.grandtotal);
		tipPercent.setNextFocusLeftId(R.id.grandtotal);
		grandTotal.setNextFocusDownId(R.id.tax);
		grandTotal.setNextFocusLeftId(R.id.tax);
		tax.setNextFocusDownId(R.id.taxpercent);
		tax.setNextFocusLeftId(R.id.taxpercent);
		taxPercent.setNextFocusDownId(R.id.total);
		taxPercent.setNextFocusLeftId(R.id.total);

		fillValues();
	}

	/** Called when the activity resumed. */
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");

		// InputMethodManager mImm;
		// boolean result = false;
		// mImm = (InputMethodManager) this
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// mImm.getInputMethodList();
		// mImm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		// result = mImm.showSoftInput(billTotal.getRootView(),
		// InputMethodManager.SHOW_FORCED);
		// result = mImm.showSoftInput(billTotal,
		// InputMethodManager.SHOW_FORCED);
		// result = mImm.showSoftInput(billTotal.getRootView(),
		// InputMethodManager.SHOW_IMPLICIT);
		// Configuration config = this.getResources().getConfiguration();
		// if (config.hardKeyboardHidden ==
		// Configuration.HARDKEYBOARDHIDDEN_YES) {
		// result = mImm.showSoftInput(billTotal.getRootView(),
		// InputMethodManager.SHOW_IMPLICIT);
		// } else {
		// result = mImm.showSoftInput(billTotal.getRootView(),
		// InputMethodManager.SHOW_FORCED);
		// }
		// if (result)
		// Log.i(TAG, "showSoftInput returned true.");
		// else
		// Log.i(TAG, "showSoftInput returned false.");

		super.onResume();
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");

		super.onDestroy();
	}

	/*
	 * Creates the menu items (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ABOUT, 1, R.string.menu_about);
		item.setIcon(R.drawable.ic_menu_info_details);
		item = menu.add(0, MENU_SETTINGS, 2, R.string.menu_prefs);
		item.setIcon(R.drawable.ic_menu_preferences);
		return true;
	}

	/*
	 * Handles item selections (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			intent = new Intent(TipCalcActivity.this,
					MyPreferenceActivity.class);
			try {
				startActivityForResult(intent, PREFS_CODE);
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "StartActivityForResult for MyPreferenceActivity");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		case MENU_ABOUT:
			intent = new Intent(TipCalcActivity.this, AboutDialog.class);
			try {
				startActivity(intent);
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "startActivity for AboutDialog");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		return false;
	}

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	public OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			// Perform action on selected list item.

			// Log.d(TAG, "onClickListener view id: " + v.getId());

			switch (v.getId()) {
			case R.id.clear:
				billTotalValue = 0.0;
				couponValue = 0.0;
				grandTotalValue = 0.0;
				taxValue = 0.0;
				tipValue = 0.0;
				taxPercentValue = prefs.getTaxPercentDefault();
				tipPercentValue = prefs.getTipPercentDefault();
				fillValues();
				break;
			}
		}
	};

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	public OnFocusChangeListener mTextViewListener = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			// Perform action on selected list item.

			Log.d(TAG, "onFocusChangeListener view id: " + v.getId());

			if (hasFocus == true) {
				if (!((TextView) v).getText().toString().matches("0.00"))
					((EditText) v).selectAll();
				else
					((EditText) v).setText("");
				return;
			}

			TextView textBox = null;

			switch (v.getId()) {
			case R.id.coupon:
				textBox = (TextView) findViewById(R.id.coupon);
				try {
					couponValue = Double.valueOf(textBox.getText().toString());
				} catch (NumberFormatException e) {
					couponValue = 0.0;
				}
				recalculate(element.COUPON);
				break;
			case R.id.grandtotal:
				textBox = (TextView) findViewById(R.id.grandtotal);
				try {
					grandTotalValue = Double.valueOf(textBox.getText()
							.toString());
				} catch (NumberFormatException e) {
					grandTotalValue = 0.0;
				}
				recalculate(element.GRANDTOTAL);
				break;
			case R.id.tax:
				textBox = (TextView) findViewById(R.id.tax);
				try {
					taxValue = Double.valueOf(textBox.getText().toString());
				} catch (NumberFormatException e) {
					taxValue = 0.0;
				}
				recalculate(element.TAX);
				break;
			case R.id.taxpercent:
				textBox = (TextView) findViewById(R.id.taxpercent);
				try {
					taxPercentValue = Double.valueOf(textBox.getText()
							.toString()) / 100.0;
				} catch (NumberFormatException e) {
					taxPercentValue = prefs.getTaxPercentDefault();
				}
				recalculate(element.TAXP);
				break;
			case R.id.tip:
				textBox = (TextView) findViewById(R.id.tip);
				try {
					tipValue = Double.valueOf(textBox.getText().toString());
				} catch (NumberFormatException e) {
					tipValue = 0.0;
				}
				recalculate(element.TIP);
				break;
			case R.id.tippercent:
				textBox = (TextView) findViewById(R.id.tippercent);
				try {
					tipPercentValue = Double.valueOf(textBox.getText()
							.toString());
				} catch (NumberFormatException e) {
					tipPercentValue = prefs.getTipPercentDefault();
				}
				recalculate(element.TIP);
				break;
			case R.id.total:
				textBox = (TextView) findViewById(R.id.total);
				try {
					billTotalValue = Double.valueOf(textBox.getText()
							.toString());
				} catch (NumberFormatException e) {
					billTotalValue = 0.0;
				}
				recalculate(element.BILLTOTAL);
				break;
			}
		}

	};

	/**
	 * Calculate the grand total or other fields when one is changed.
	 * 
	 * @param item
	 *            The item of the enumeration which changed.
	 */
	private void recalculate(element item) {
		switch (item) {
		case BILLTOTAL:
			tipValue = (billTotalValue + couponValue) * tipPercentValue;
			taxValue = billTotalValue
					- (billTotalValue / (1 + taxPercentValue));
			break;
		case COUPON:
			if (couponValue > 0) {
				couponPercentValue = 1 - (billTotalValue / (billTotalValue + couponValue));
				tipValue = (billTotalValue + couponValue) * tipPercentValue;
			} else {
				couponValue = 0.0;
				couponPercentValue = 0.0;
			}
			break;
		case GRANDTOTAL:
			if (grandTotalValue > 0.0)
				if (billTotalValue > 0.0) {
					tipValue = grandTotalValue - billTotalValue;
					tipPercentValue = tipValue / (billTotalValue + couponValue);
				} else
					billTotalValue = grandTotalValue;
			else
				grandTotalValue = 0.0;
			break;
		case TAX:
			if (taxValue > 0) {
				taxPercentValue = (-taxValue) / (taxValue - billTotalValue);
			} else {
				taxValue = 0.0;
				taxPercentValue = prefs.getTaxPercentDefault();
			}
			break;
		case TAXP:
			if (taxPercentValue > 0) {
				taxValue = billTotalValue
						- (billTotalValue / (1 + taxPercentValue));
			} else {
				taxValue = 0.0;
				taxPercentValue = prefs.getTaxPercentDefault();
			}
			break;
		case TIP:
			if (tipValue > 0)
				tipPercentValue = tipValue / (billTotalValue + couponValue);
			else {
				tipValue = 0.0;
				tipPercentValue = prefs.getTipPercentDefault();
			}
			break;
		case TIPP:
			if (tipPercentValue > 0)
				tipValue = tipPercentValue * (billTotalValue + couponValue);
			else {
				tipValue = 0.0;
				tipPercentValue = prefs.getTipPercentDefault();
			}
			break;
		}
		grandTotalValue = billTotalValue + tipValue;
		fillValues();
	}

	private void fillValues() {
		billTotal.setText(String.format("%.2f", billTotalValue));
		coupon.setText(String.format("%.2f", couponValue));
		grandTotal.setText(String.format("%.2f", grandTotalValue));
		tip.setText(String.format("%.2f", tipValue));
		tax.setText(String.format("%.2f", taxValue));

		couponPercent
				.setText(String.format("%.1f%%", couponPercentValue * 100));
		tipPercent.setText(String.format("%.1f%%", tipPercentValue * 100));
		taxPercent.setText(String.format("%.1f%%", taxPercentValue * 100));
	}
}
