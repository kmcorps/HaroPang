package org.haroid.HaroPang;


public class ByteArrayToHexString {

	public static String main(String args) {

		// byte array with non printable characters
		byte[] bytes = new byte[] { 'a', 'b', 0, 5, 'c', 'd' };
		byte[] nonprintable = new byte[] { 'a', 'b', 0, 6, 'c', 'd' };

		// You can not print byte array as String because they may contain non
		// printable
		// characters e.g. 0 is NUL, 5 is ENQ and 6 is ACK in ASCII format

		String value = new String(bytes);
		System.out.println(value);
		String str = new String(nonprintable);
		System.out.println(str);

		// Converting byte array to Hex String in Java for printing
		System.out.println("Byte array to Hex String in Java :                       "
				+ bytesToHexString(bytes));

		return bytesToHexString(bytes);

		// Apache commons codec to convert byte array to Hex String in Java
		// String hex = Hex.encodeHexString(bytes);
		// System.out.println("Byte array to Hexadecimal String using Apache commons:   "
		// + hex);
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x ", b & 0xff));
		}
		return sb.toString();
	}

}
