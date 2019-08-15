package org.xy.who;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xy.utils.SHA256Digest;

public class SHATest {
	public static void main(String[] args) throws NoSuchAlgorithmException {

		 System.out.println(SHA256Digest.digest("Hello"));
	}
}
