package com.uutils.crypto;

import com.uutils.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Utils {

	public static String getStringSHA(String val) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.update(val.getBytes());
			byte[] m = sha.digest();// 加密
			// return getshaString(m);
			return ByteUtils.toHexString(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getStringMD5(String source) {
		String hash = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					source.getBytes());
			hash = getStreamMD5(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * 获取文件的MD5
	 * */
	public static String getFileMD5(String file) {
		String hash = null;
        FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			hash = getStreamMD5(in);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            FileUtils.close(in);
        }
        return hash;
	}

	private static String getStreamMD5(InputStream stream) {
		String hash = null;
		byte[] buffer = new byte[1024];
		BufferedInputStream in = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			in = new BufferedInputStream(stream);
			int numRead = 0;
			while ((numRead = in.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			hash = ByteUtils.toHexString(md5.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            FileUtils.close(in);
        }
        return hash;
	}
}
