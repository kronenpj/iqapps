package com.googlecode.iqapps;

import static com.googlecode.iqapps.IOUtils.peek;
import static com.googlecode.iqapps.IOUtils.read;
import static java.lang.Character.isWhitespace;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * Simple scanner to delimit Sql-like text.
 *
 * Iterates over a statement at a time.  Statements are broken on semicolons.
 * Multi-line single quoted string literals and double-dash comments are allowed.
 *
 * This class isn't thread-safe.
 *
 * @author philhsmith
 */
public class SqlScanner implements Iterator<String>, Iterable<String> {
	private final BufferedReader in;
	private final StringBuilder buf;
	private boolean sawEof;
	private String next;
	private int lineNumber;
	private int lineNumberStatementStartedOn;

	public SqlScanner(InputStream in) {
		this(new InputStreamReader(in));
	}

	public SqlScanner(Reader in) {
		this.in = new BufferedReader(in, 512);
		this.buf = new StringBuilder(64);
		this.next = null;
		this.sawEof = false;
		this.lineNumber = 1;
		this.lineNumberStatementStartedOn = 1;
	}

	public boolean hasNext() {
		if (next == null) next = pull();
		return next.length() > 0 || peek(in) != -1;
	}

	public String next() {
		if (next != null) {
			String current = next;
			next = null;
			return current;
		}

		return pull();
	}

	private String pull() {
		String stmt = pullSimpleStatement();
		
		if (startsCompoundStatement(stmt)) {
			String start = stmt;
			StringBuilder compound = new StringBuilder(stmt);
			
			stmt = pullSimpleStatement();
			while (!sawEof && !endsCompoundStatementStartedWith(start, stmt)) {
				compound.append("; ").append(stmt);
				stmt = pullSimpleStatement();
			}
			compound.append("; ").append(stmt);
			
			stmt = compound.toString();
		}
		
		return stmt;
	}

	private boolean startsCompoundStatement(String stmt) {
		return stmt.trim().toLowerCase().matches("create.*trigger.*");
	}
	
	private boolean endsCompoundStatementStartedWith(String start, String end) {
		return end.trim().toLowerCase().matches("end");
	}
	
	private String pullSimpleStatement() {
		boolean inString = false;
		boolean inComment = false;
		boolean seenNonWhiteSpaceOutsideComment = false;
		int c = -1;

		buf.replace(0, buf.length(), "");

		while (true) {
			c = read(in);
			if (c == -1) {
				sawEof = true;
				break;
			}

			if (c == '-' && peek(in) == '-' && !inString) inComment = true;
			if (c == '\n' || c == '\r') {
				if (c == '\r' && peek(in) == '\n')
					read(in);
				lineNumber++;
				inComment = false;
			}

			if (inComment) continue;

			if (!seenNonWhiteSpaceOutsideComment && !isWhitespace(c)) {
				seenNonWhiteSpaceOutsideComment = true;
				lineNumberStatementStartedOn = lineNumber;
			}

			if (c == ';' && !inString) break;

			// Sqlite3 single quotes are escaped by two single quotes. So, counting
			// parity works as a dirty trick for determining if we're in a string.
			if (c == '\'') inString = !inString;

			buf.appendCodePoint(c);
		}

		return buf.toString().trim();
	}

	public void close() {
		IOUtils.close(in);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<String> iterator() {
		return this;
	}

	public int getLineNumber() {
		return lineNumberStatementStartedOn;
	}
}
