package com.googlecode.iqapps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class HttpHelper {
	private static final Logger logger = Logger.getLogger("HttpHelper");

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
		// byte[] result;
		byte[] chunk = new byte[4096];
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int bRead = 0;
		try {
			InputStream in = response.getEntity().getContent();
			while ((bRead = in.read(chunk)) > 0)
				byteStream.write(chunk, 0, bRead);
			in.close();
		} catch (Exception ex) {
			logger.debug("requestBytes: " + ex.toString());
			return null;
		}
		return byteStream.toByteArray();
	}
}
