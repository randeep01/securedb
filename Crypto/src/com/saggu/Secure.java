package com.saggu;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Secure {

	public static void main(String[] args) {

		if (args.length <= 2) {

			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("java -jar Secure [A/E/D/R][Key]<user>");

			System.out.println(" A - ADD\nE - Edit\nD - Delete\nR - Read");
			System.out.println("java -jar Secure A [key]");
			System.out.println("java -jar Secure R [key][all/<insitute>]<security>");
			System.out.println("java -jar Secure E [key][insitute] ");

			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// return;
		}

		int iterations = 250000;
		String userpassword = args[1];
		String salt = "salt";

		SecretKeyFactory factory;
		byte[] keyVal = null;
		SecretKey key = null;
		SecretKey secret = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			char[] passwordChars = userpassword.toCharArray();
			KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), iterations, 128);
			key = factory.generateSecret(spec);

			// keyVal = key.;

			secret = new SecretKeySpec(key.getEncoded(), "AES");

			// keyVal = secret.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// String currentDir = System.getProperty("user.dir");
		// System.out.println("Current dir using System:" + currentDir);
		// System.out.println(args[0]);
		// System.out.println(args[1]);
		try {

			File file = new File("data");
			file.createNewFile();
			HashMap<String, Credentials> map;

			map = loadDataToMap(file, secret);
			if (map == null) {
				return;
			}
			switch (args[0]) {
			case "A": {
				System.out.print("Insitute :");
				String insitute = System.console().readLine();

				System.out.print("UserName :");
				String userName = System.console().readLine();

				System.out.print("Password : ");
				String password = System.console().readLine();
				Credentials temp = new Credentials(userName, password);

				if (map.containsKey(insitute)) {
					System.out.println("Can't Enter Key already exists");
				}

				if (map.put(insitute, temp) == null) {
					System.out.println("Added");
				}
				saveMaptoFile(map, file, secret);

				break;
			}
			case "R": {
				String insitute = args[2];
				String security = args[3];

				System.out.println("-----------------------------------------------------------------------------");
				System.out.printf("%20s| %20s| %20s| ", "Insitute", "User Name", "Password");
				System.out.println();
				System.out.println("-----------------------------------------------------------------------------");

				map.forEach((k, v) -> printData(k, v, insitute, security));
				System.out.println("-----------------------------------------------------------------------------");
				break;
			}
			case "D": {
				String insitute = args[2];
				if (map.containsKey(insitute) == true) {

					map.remove(insitute);

				} else {
					System.out.println("No Record");
				}
				saveMaptoFile(map, file, secret);
				break;
			}
			case "E": {
				String insitute = args[2];
				if (map.containsKey(insitute) == true) {
					System.out.print("UserName :");
					String userName = System.console().readLine();

					System.out.print("Password: ");
					String password = System.console().readLine();
					Credentials temp = new Credentials(userName, password);

					if (map.put(insitute, temp) != null) {
						System.out.println("Record Edited");
					}
					saveMaptoFile(map, file, secret);

				} else {
					System.out.println("Record doesn't exists");
				}

				break;
			}
			}

		} catch (IOException | CryptoException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		//	e.printStackTrace();
		} finally {

		}

	}

	private static void printData(String k, Credentials v, String insitute, String security) {

		if (insitute == null || insitute.equals("")) {
			insitute = "all";
		}
		if (security == null || security.equals("")) {
			security = "high";
		}

		if (insitute.equalsIgnoreCase("all") || k.equalsIgnoreCase(insitute)) {

			String password = v.getPassword();
			switch (security) {
			case "low": {
				break;
			}
			case "high": {
				// password = password;
				int length = password.length();
				char[] chArr = new char[length];
				for (int i = 0; i < length; i++) {
					chArr[i] = '*';
				}

				password = new String(chArr);
				break;

			}
			case "mid": {
				int length = password.length();

				char[] chArr = password.toCharArray();
				for (int i = 0; i < length; i++) {
					if (i % 2 == 0) {
						chArr[i] = '*';
					} else {
						// chArr[i] = chArr[i];
					}

				}
				password = new String(chArr);
				break;
			}
			default: {
				int length = password.length();
				char[] chArr = new char[length];
				for (int i = 0; i < length; i++) {
					chArr[i] = '*';
				}

				password = new String(chArr);
				break;

			}

			}
			System.out.format("%20s| %20s| %20s| ", k, v.getUserName(), password);
			System.out.println();
		}
		// return null;
	}

	private static void saveMaptoFile(HashMap<String, Credentials> map, File file, SecretKey key)
			throws IOException, CryptoException {

		FileOutputStream fos = new FileOutputStream(file);
		try {
			Set<Entry<String, Credentials>> s = map.entrySet();
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Credentials> val : s) {
				sb.append(val.getKey()).append(",").append(val.getValue().getUserName()).append(",")
						.append(val.getValue().getPassword()).append("\r\n");
			}
			byte[] strToBytes = sb.toString().getBytes();
			byte[] output = null;
			output = CryptoUtils.encrypt(key, strToBytes);
			fos.write(output);
			fos.close();
		} finally {
			fos.close();
		}

	}

	private static HashMap<String, Credentials> loadDataToMap(File file, SecretKey key)
			throws IOException, CryptoException {
		HashMap<String, Credentials> map = new HashMap<>();
		int lenght = (int) file.length();
		if (lenght == 0) {
			return map;
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] inputBytes = new byte[lenght];
		fis.read(inputBytes);
		byte[] output = null;
		output = CryptoUtils.decrypt(key, inputBytes);
		ByteArrayInputStream bis = new ByteArrayInputStream(output);
		Scanner sc = new Scanner(bis);
		String line = "";
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String str[] = line.split(",");
			Credentials temp = new Credentials(str[1], str[2]);
			// System.out.println(str[0] + "," + temp.toString());
			map.put(str[0], temp);
		}
		sc.close();
		fis.close();
		return map;
	}
}