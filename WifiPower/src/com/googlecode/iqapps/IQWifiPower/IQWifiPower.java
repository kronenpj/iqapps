package com.googlecode.iqapps.IQWifiPower;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.iqapps.ShellCommand;
import com.googlecode.iqapps.ShellCommand.CommandResult;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class IQWifiPower extends Activity {
	private String message;
	public static final String MSG_TAG = "IQWifiPower";
	public String DATA_FILE_PATH;

	/*
	 * TODO Hacky debug mode pref detection. Redo.
	 */
	File debug = new File(
			"/data/data/com.googlecode.iqapps.IQWifiPower/debugmode");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	/** Called when the activity is resumed. */
	@Override
	public void onResume() {
		super.onResume();
		if (debug.exists())
			Log.d(MSG_TAG, "Entering onResume.");

		File datPath = Environment.getDataDirectory();

		try {
			if (debug.exists())
				Log.d(MSG_TAG,
						"Data directory path: " + datPath.getCanonicalPath());
			setPath(datPath.getCanonicalPath()
					+ "/data/com.googlecode.iqapps.IQWifiPower");
		} catch (IOException e) {
			setPath("/data/data/com.googlecode.iqapps.IQWifiPower");
		}

		message = copyFile(DATA_FILE_PATH + "/iwconfig", "0755", R.raw.iwconfig);
		try {
			if (debug.exists())
				Log.d(MSG_TAG, "Copy file message: " + message);
		} catch (NullPointerException e) {
		}

		TextView textView = (TextView) findViewById(R.id.message);
		boolean retVal = runIWConfig("eth0", "0");
		if (retVal) {
			textView.setText(R.string.powerReduce);
		} else {
			textView.setText(R.string.powerFailed);
		}
	}

	public void setPath(String path) {
		this.DATA_FILE_PATH = path;
	}

	public boolean chmod(String file, String mode) {
		if (runShellCommand("su", "exit", "/bin/chmod " + mode + " " + file) == "0") {
			return true;
		}
		return false;
	}

	public boolean runIWConfig(String device, String power) {
		if (debug.exists())
			Log.d(MSG_TAG, "runIWConfig " + device + " to txpower " + power);
		String res = runShellCommand("su", "exit", DATA_FILE_PATH
				+ "/iwconfig " + device + " txpower " + power);
		if (debug.exists())
			Log.d(MSG_TAG, "runIWConfig result: " + res);
		if (res.equalsIgnoreCase("0")) {
			return true;
		}
		return false;
	}

	/**
	 * Runs shell commands as sh or su.
	 * 
	 * @param UserType
	 *            - sh or su
	 * @param OutputType
	 *            - exit, stdout, or stderr
	 * @param Command
	 *            - properly formed shell command
	 * @return Output
	 */
	public String runShellCommand(String UserType, String OutputType,
			String Command) {
		String Output = "";

		if (UserType == "su") {
			ShellCommand cmd = new ShellCommand();
			CommandResult r = cmd.su.runWaitFor(Command);
			if (!r.success()) {
				if (debug.exists())
					Log.d(MSG_TAG, "Error " + r.stderr);
			} else {
				if (debug.exists())
					Log.d(MSG_TAG, "Successfully executed command " + Command
							+ " Result is: " + r.stdout);
				if (OutputType == "stdout") {
					Output = r.stdout;
				}
				if (OutputType == "stderr") {
					Output = r.stderr;
				}
				if (OutputType == "exit") {
					Output = Integer.toString(r.exit_value);
				}
			}
		} else {
			ShellCommand cmd = new ShellCommand();
			CommandResult r = cmd.sh.runWaitFor(Command);
			if (!r.success()) {
				if (debug.exists())
					Log.d(MSG_TAG, "Error " + r.stderr);
			} else {
				if (debug.exists())
					Log.d(MSG_TAG, "Successfully executed command " + Command
							+ " Result is: " + r.stdout);
				if (OutputType == "stdout") {
					Output = r.stdout;
				}
				if (OutputType == "stderr") {
					Output = r.stderr;
				}
				if (OutputType == "exit") {
					Output = Integer.toString(r.exit_value);
				}
			}
		}
		return Output;
	}

	private String copyFile(String filename, String permission, int ressource) {
		String result = this.copyFile(filename, ressource);
		if (result != null) {
			return result;
		}
		if (chmod(filename, permission) != true) {
			result = "Can't change file-permission for '" + filename + "'!";
		}
		return result;
	}

	private String copyFile(String filename, int ressource) {
		File outFile = new File(filename);
		if (outFile.exists()) {
			if (debug.exists())
				Log.d(MSG_TAG, "Skipping '" + filename + "', already exists...");
			return null;
		}
		if (debug.exists())
			Log.d(MSG_TAG, "Copying file '" + filename + "' ...");
		InputStream is = this.getResources().openRawResource(ressource);
		byte buf[] = new byte[1024];
		int len;
		try {
			OutputStream out = new FileOutputStream(outFile);
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			is.close();
		} catch (IOException e) {
			return "Couldn't install file - " + filename + "!";
		}
		return null;
	}
}
