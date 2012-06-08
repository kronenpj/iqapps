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

	String applicationName;
	TextView[] tvaChildFloat;
	TextView[] tvaChildPercent;
	TextView tvBillTotal;
	TextView tvCoupon;
	TextView tvGrandTotal;
	TextView tvTax;
	TextView tvTip;
	TextView tvCouponPercent;
	TextView tvTaxPercent;
	TextView tvTipPercent;

	boolean tipOnTax = false;
	double billTotalValue;
	double couponValue;
	double grandTotalValue;
	double taxValue;
	double tipValue;
	double couponPercentValue;
	double taxPercentValue;
	double tipPercentValue;

	private static enum element {
		BILLTOTAL, TIP, TIPP, TAX, TAXP, GRANDTOTAL, COUPON, COUPONP
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
		// Log.d(TAG, "onCreate.");

		prefs = new PreferenceHelper(this);

		taxPercentValue = prefs.getTaxPercentPref();
		tipPercentValue = prefs.getTipPercentPref();
		tipOnTax = prefs.getTipOnTaxPref();

		Resources res = getResources();
		applicationName = res.getString(R.string.app_name);

		Button clearButton = (Button) findViewById(R.id.clear);
		clearButton.setOnClickListener(mButtonListener);
		Button resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(mButtonListener);

		tvBillTotal = (TextView) findViewById(R.id.total);
		tvCoupon = (TextView) findViewById(R.id.coupon);
		tvGrandTotal = (TextView) findViewById(R.id.grandtotal);
		tvTax = (TextView) findViewById(R.id.tax);
		tvTip = (TextView) findViewById(R.id.tip);

		tvaChildFloat = new TextView[] { tvBillTotal, tvCoupon, tvGrandTotal,
				tvTax, tvTip };
		for (int count = 0; count < tvaChildFloat.length; count++) {
			try {
				final int index = count;
				tvaChildFloat[index]
						.setOnFocusChangeListener(mTextViewListener);
			} catch (NullPointerException e) {
				Toast.makeText(TipCalcActivity.this, "NullPointerException",
						Toast.LENGTH_SHORT).show();
			}
		}

		tvCouponPercent = (TextView) findViewById(R.id.couponpercent);
		tvTaxPercent = (TextView) findViewById(R.id.taxpercent);
		tvTipPercent = (TextView) findViewById(R.id.tippercent);

		tvaChildPercent = new TextView[] { tvCouponPercent, tvTaxPercent,
				tvTipPercent };
		for (int count = 0; count < tvaChildPercent.length; count++) {
			try {
				final int index = count;
				tvaChildPercent[index]
						.setOnFocusChangeListener(mTextViewListener);
			} catch (NullPointerException e) {
				Toast.makeText(TipCalcActivity.this, "NullPointerException",
						Toast.LENGTH_SHORT).show();
			}
		}

		// Map out the order of fields traversed when "Next" is pressed.
		tvBillTotal.setNextFocusDownId(R.id.coupon);
		tvBillTotal.setNextFocusLeftId(R.id.coupon);
		tvCoupon.setNextFocusDownId(R.id.tip);
		tvCoupon.setNextFocusLeftId(R.id.tip);
		tvTip.setNextFocusDownId(R.id.tax);
		tvTip.setNextFocusLeftId(R.id.tax);
		tvTax.setNextFocusDownId(R.id.grandtotal);
		tvTax.setNextFocusLeftId(R.id.grandtotal);
		tvGrandTotal.setNextFocusDownId(R.id.couponpercent);
		tvGrandTotal.setNextFocusLeftId(R.id.couponpercent);
		tvCouponPercent.setNextFocusDownId(R.id.tippercent);
		tvCouponPercent.setNextFocusLeftId(R.id.tippercent);
		tvTipPercent.setNextFocusDownId(R.id.taxpercent);
		tvTipPercent.setNextFocusLeftId(R.id.taxpercent);
		tvTaxPercent.setNextFocusDownId(R.id.total);
		tvTaxPercent.setNextFocusLeftId(R.id.total);

		fillValues();
	}

	/** Called when the activity resumed. */
	@Override
	public void onResume() {
		// Log.d(TAG, "onResume");

		super.onResume();
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		// Log.d(TAG, "onDestroy");

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
				break;
			case R.id.reset:
				billTotalValue = 0.0;
				couponValue = 0.0;
				grandTotalValue = 0.0;
				taxValue = 0.0;
				tipValue = 0.0;
				taxPercentValue = prefs.getTaxPercentPref();
				tipPercentValue = prefs.getTipPercentPref();
				break;
			}
			recalculate(element.BILLTOTAL);
			fillValues();
			tvBillTotal.requestFocus();
			tvBillTotal.setText("");
		}
	};

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	public OnFocusChangeListener mTextViewListener = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			// Perform action on selected list item.

			// Log.d(TAG, "onFocusChangeListener view id: " + v.getId());

			if (hasFocus == true) {
				Log.d(TAG, "OnFocusChangeListener: focused: '"
						+ ((TextView) v).getText().toString() + "'");
				if (((TextView) v).getText().toString().matches("0.00")
						|| ((TextView) v).getText().toString().matches("0.0%"))
					((EditText) v).setText("");
				else
					((EditText) v).selectAll();
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
			case R.id.couponpercent:
				textBox = (TextView) findViewById(R.id.couponpercent);
				try {
					couponPercentValue = Double.valueOf(textBox.getText()
							.toString());
				} catch (NumberFormatException e) {
					couponPercentValue = 0.0;
				}
				recalculate(element.COUPONP);
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
					taxPercentValue = prefs.getTaxPercentPref();
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
							.toString()) / 100.0;
				} catch (NumberFormatException e) {
					tipPercentValue = prefs.getTipPercentPref();
				}
				recalculate(element.TIPP);
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
	 * This method is called when the sending activity has finished, with the
	 * result it supplied.
	 * 
	 * @param requestCode
	 *            The original request code as given to startActivity().
	 * @param resultCode
	 *            From sending activity as per setResult().
	 * @param data
	 *            From sending activity as per setResult().
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check to see that what we received is what we wanted to see.
		// Log.d(TAG, "onActivityResult.");
		if (requestCode == PREFS_CODE) {
			// Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
			// This is a standard resultCode that is sent back if the
			// activity doesn't supply an explicit result. It will also
			// be returned if the activity failed to launch.
			tipOnTax = prefs.getTipOnTaxPref();
			// Log.d(TAG, "onActivityResult: tipOnTax: "
			// + (tipOnTax ? "True" : "False"));
			recalculate(element.TIPP);
		}
	}

	/**
	 * Calculate the grand total or other fields when one is changed.
	 * 
	 * @param item
	 *            The item of the enumeration which changed.
	 */
	private void recalculate(element item) {
		double includeTaxInTip = tipOnTax ? 1.0 : 0.0;
		// Log.d(TAG, "recaclulate: includeTaxInTip: " + includeTaxInTip);
		switch (item) {
		case BILLTOTAL:
			taxValue = billTotalValue
					- (billTotalValue / (1 + taxPercentValue));
			tipValue = (billTotalValue + couponValue + (taxValue * includeTaxInTip))
					* tipPercentValue;
			break;
		case COUPON:
			if (couponValue > 0) {
				couponPercentValue = 1 - (billTotalValue / (billTotalValue + couponValue));
				tipValue = (billTotalValue + couponValue + (taxValue * includeTaxInTip))
						* tipPercentValue;
			} else {
				couponValue = 0.0;
				couponPercentValue = 0.0;
			}
			break;
		case COUPONP:
			if (couponPercentValue > 0) {
				couponValue = billTotalValue
						- (billTotalValue / (1 + couponPercentValue));
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
				taxPercentValue = prefs.getTaxPercentPref();
			}
			break;
		case TAXP:
			if (taxPercentValue > 0) {
				taxValue = billTotalValue
						- (billTotalValue / (1 + taxPercentValue));
			} else {
				taxValue = 0.0;
				taxPercentValue = prefs.getTaxPercentPref();
			}
			break;
		case TIP:
			if (tipValue > 0)
				tipPercentValue = tipValue
						/ (billTotalValue + couponValue + (taxValue * includeTaxInTip));
			else {
				tipValue = 0.0;
				tipPercentValue = prefs.getTipPercentPref();
			}
			break;
		case TIPP:
			if (tipPercentValue > 0)
				tipValue = tipPercentValue
						* (billTotalValue + couponValue + (taxValue * includeTaxInTip));
			else {
				tipValue = 0.0;
				tipPercentValue = prefs.getTipPercentPref();
			}
			break;
		}
		grandTotalValue = billTotalValue + tipValue;
		fillValues();
	}

	private void fillValues() {
		tvBillTotal.setText(String.format("%.2f", billTotalValue));
		tvGrandTotal.setText(String.format("%.2f", grandTotalValue));
		tvCoupon.setText(String.format("%.2f", couponValue));
		tvTip.setText(String.format("%.2f", tipValue));
		tvTax.setText(String.format("%.2f", taxValue));

		tvCouponPercent.setText(String.format("%.1f%%",
				couponPercentValue * 100));
		tvTipPercent.setText(String.format("%.1f%%", tipPercentValue * 100));
		tvTaxPercent.setText(String.format("%.1f%%", taxPercentValue * 100));
	}
}
