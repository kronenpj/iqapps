package com.googlecode.iqapps.testtools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

public class IOUtils {
	public static void cp(String source, String target) {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new BufferedInputStream(new FileInputStream(source));
			out = new BufferedOutputStream(new FileOutputStream(target));

			for (int c = read(in); c != -1; c = read(in)) write(out, c);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			close(in);
			close(out);
		}
	}

	public static boolean rm(String file) {
		File f = new File(file);
		if (!f.exists()) return true;
		return f.delete();
	}

	public static void mv(String source, String target) {
		new File(source).renameTo(new File(target));
	}

	public static String [] ls(String dir, String pattern) {
		File d = new File(dir);
		if (!d.isDirectory()) throw new IllegalArgumentException("Not a directory.");
		File [] files = d.listFiles(new RegexFilenameFilter(pattern));
		String [] fileNames = new String [files.length];

		try {
			for (int i = 0; i < files.length; i++) fileNames[i] = files[i].getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return fileNames;
	}

	public static String basename(String file) {
		File f = new File(file);
		if (!f.exists()) throw new IllegalArgumentException("Can't find " + file);
		return f.getName();
	}

	public static String dirname(String file) {
		File f = new File(file);
		if (!f.exists()) throw new IllegalArgumentException("Can't find " + file);
		return f.getParent();
	}
	
	public static String join(String... parts) {
		if (parts.length == 0) return "";
		StringBuilder joined = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			joined.append(File.separator).append(parts[i]);
		}
		return joined.toString();
	}
	
	private static class RegexFilenameFilter implements FilenameFilter {
		private final Pattern pattern;

		public RegexFilenameFilter(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		public boolean accept(File dir, String filename) {
			return pattern.matcher(filename).matches();
		}
	}

	public static int read(InputStream in) {
		try {
			return in.read();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String toString(InputStream in) {
		StringBuilder buf = new StringBuilder();
		for (int c = read(in); c != -1; c = read(in)) {
			buf.appendCodePoint(c);
		}
		return buf.toString();
	}
	
	public static void write(OutputStream out, int c) {
		try {
			out.write(c);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static int read(Reader in) {
		try {
			return in.read();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void write(Writer out, int c) {
		try {
			out.write(c);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable == null) return;

		try {
			closeable.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static int peek(Reader in) {
		if (!in.markSupported()) throw new IllegalArgumentException();

		try {
			in.mark(1);
			int c = in.read();
			in.reset();

			return c;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
