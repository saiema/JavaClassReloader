package reloader.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to obtain a md5 digest of a java file prior to strip all coments and whitespace
 *
 * @author Simón Emmanuel Gutiérrez Brida
 * @version 0.4
 */
public class JustCodeDigest {
	private static final Pattern COMMENT_JAVADOC = Pattern.compile("/\\*\\*(.*)\\*/");
	private static final Pattern COMMENT_MULTILINE = Pattern.compile("/\\*((?<!\\*).*)\\*/");
	private static final Pattern COMMENT_SINGLE = Pattern.compile("//.*");
	private static final Pattern WHITESPACE = Pattern.compile("\\s");
	private static boolean printExceptions = false;
	private static Exception lastException = null;
	
	public static void printExceptions(boolean b) {
		JustCodeDigest.printExceptions = b;
	}
	
	public static Exception getLastException() {
		return JustCodeDigest.lastException;
	}

	private static String getJustCode(String original) {
		Matcher javadoc = COMMENT_JAVADOC.matcher(original);
		Matcher comment_multi = COMMENT_MULTILINE.matcher(javadoc.replaceAll(""));
		Matcher comment_single = COMMENT_SINGLE.matcher(comment_multi.replaceAll(""));
		Matcher whitespace = WHITESPACE.matcher(comment_single.replaceAll(""));
		return whitespace.replaceAll("");
	}
	
	public static byte[] digest(String original) {
		return digest(original, true);
	}
	
	public static byte[] digest(String original, boolean justCode) {
		String code = justCode?getJustCode(original):original;
		InputStream is = new ByteArrayInputStream(code.getBytes());
		DigestInputStream dis = null;
		JustCodeDigest.lastException = null;
		try {
			dis = new DigestInputStream(is, MessageDigest.getInstance("MD5"));
			while (dis.read() != -1) {}
			return dis.getMessageDigest().digest();
		} catch (NoSuchAlgorithmException e) {
			if (JustCodeDigest.printExceptions) e.printStackTrace();
			JustCodeDigest.lastException = e;
		} catch (IOException e) {
			if (JustCodeDigest.printExceptions) e.printStackTrace();
			JustCodeDigest.lastException = e;
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					if (JustCodeDigest.printExceptions) e.printStackTrace();
					if (JustCodeDigest.lastException != null) {
						Exception exc = new Exception(e);
						exc.initCause(JustCodeDigest.lastException);
						JustCodeDigest.lastException = exc;
					} else {
						JustCodeDigest.lastException = e;
					}
				}
			}
		}
		return null;
	}
	
	public static byte[] digest(File file) {
		return digest(file, true);
	}
	
	public static byte[] digest(File file, boolean justCode) {
		byte[] bytes;
		JustCodeDigest.lastException = null;
		try {
			bytes = Files.readAllBytes(file.toPath());
			String text = new String(bytes, StandardCharsets.UTF_8);
			return digest(text, justCode);
		} catch (IOException e) {
			if (JustCodeDigest.printExceptions) e.printStackTrace();
			JustCodeDigest.lastException = e;
		}
		return null;
	}

}
