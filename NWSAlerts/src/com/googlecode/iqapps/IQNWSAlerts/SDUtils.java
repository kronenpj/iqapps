package com.googlecode.iqapps.IQNWSAlerts;

/**
 * Class to export the current database to the SDcard.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 * @author Neil http://stackoverflow.com/users/319625/user319625
 */

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.util.Log;

public class SDUtils {
	private static final String TAG = "SDUtils";

	public static boolean writeToSD(String outputFileName, String packageName,
			String data) {
		return writeToSD(outputFileName, packageName, data.getBytes());
	}

	public static boolean writeToSD(String outputFileName, String packageName,
			byte[] data) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			Log.d(TAG, "SDBackup: databaseName: " + outputFileName);

			if (sd.canWrite()) {
				String filePath = new String(packageName.substring(packageName
						.lastIndexOf('.') + 1));
				File backupDir = new File(sd, filePath);
				File backupFile = new File(sd, filePath + "/" + outputFileName);

				Log.d(TAG, "SDBackup: filePath: " + filePath);

				if (!backupDir.exists()) {
					Log.d(TAG, "SDBackup: Creating directory: " + filePath);
					if (!backupDir.mkdir()) {
						Log.d(TAG, "SDBackup: Directory creation failed.");
					}
				}

				ByteBuffer src = ByteBuffer.wrap(data);
				FileChannel dst = new FileOutputStream(backupFile).getChannel();
				dst.write(src);
				dst.close();
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, "SDBackup threw exception: " + e.toString());
		}
		return false;
	}
}