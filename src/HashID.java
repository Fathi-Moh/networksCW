// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashID {

	public static byte [] computeHashID(String line) throws Exception {
		if (line.endsWith("\n")) {
			// What this does and how it works is covered in a later lecture
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(line.getBytes(StandardCharsets.UTF_8));
			return md.digest();

		} else {
			// 2D#4 computes hashIDs of lines, i.e. strings ending with '\n'
			throw new Exception("No new line at the end of input to HashID");
		}
	}
	// This method calculates the distance between two hashIDs and performs an XOR operation
	public static int distanceFromHashID(byte[] hashOne, byte[] hashTwo){
		int distance = 0;
		for(int i = 0; i < hashOne.length; i++){
			int XOR = hashOne[i] ^ hashTwo[i];
			distance += Integer.bitCount(XOR);
		}
		return distance;
	}

	// This method calculates the leading zeros in a hashID represented in a byte array
	public static int amountOfLeadingZeros(byte[] hash) {
		int zerosCounter = 0;
		for (byte b : hash) {
			if (b != 0) {
				// Subtract 24 as SHA-256 hash bytes are unsigned
				zerosCounter += Integer.numberOfLeadingZeros(b) - 24;
				break;
			}
			// 8 bit for a byte
			zerosCounter += 8;
		}
		return zerosCounter;
	}

	// This method is used to perform a XOR operation on two hashIDs as byte arrays
	public static byte[] bitwiseXOR(byte[] hashOne, byte[] hashTwo) {
		byte[] result = new byte[hashOne.length];
		for (int i = 0; i < hashOne.length; i++) {
			result[i] = (byte) (hashOne[i] ^ hashTwo[i]);
		}
		return result;
	}
}