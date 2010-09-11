package com.googlecode.iqapps;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.util.ByteArrayBuffer;

public class HttpHelper {

	public static String request(HttpResponse response) {
		String result = "";
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line + "\n");
			}
			in.close();
			result = str.toString();
		} catch (Exception ex) {
			result = "Error";
		}
		return result;
	}

	public static byte[] requestBytes(HttpResponse response) {
		byte[] result;
		ByteArrayBuffer buffer = new ByteArrayBuffer(102400);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			// TODO: This doesn't work.
			byteStream.write(reader.read());
			in.close();
		} catch (Exception ex) {
			result = null;
		}
		result=byteStream.toByteArray();
		return result;
	}
}
