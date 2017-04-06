package com.lmq.main.util;


import com.lmq.main.api.MyLog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import java.security.PrivateKey;
import java.security.PublicKey;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;



/**
 *
 * @date 2014年8月22日 下午1:44:23
 */
public final class RSAUtilsZFT
{
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;
	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;
	/**
	 * RSA最大加密明文大小
	 */
	private static final String PUBLIC_EXPONENT="65537";

	/**
	 * 根据文件路径获取私钥
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKey(String path)
			throws FileNotFoundException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		InputStream inputStream = new FileInputStream(new File(path));

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String privateKey = sb.toString();
		byte[] keyBytes = new Base64().decode(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM,
				new BouncyCastleProvider());
		PrivateKey pk = keyFactory.generatePrivate(keySpec);
		return pk;
	}

	/**
	 * 根据文件路径获取私钥
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKey1(String path)
			throws FileNotFoundException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		InputStream inputStream = new FileInputStream(new File(path));

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String privateKey = sb.toString();
		byte[] keyBytes = new Base64().decode(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM,
				new BouncyCastleProvider());
		PrivateKey pk = keyFactory.generatePrivate(keySpec);
		RSAPrivateKey rspk=(RSAPrivateKey) pk;
		rspk.getModulus();
		return pk;
	}

	/**
	 * 根据公钥字符串获取公钥
	 * @param pkStr
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String pkStr) throws Exception{
//		byte[] keyBytes = new Base64().decode(pkStr);
		byte[] keyBytes = android.util.Base64.decode(pkStr, android.util.Base64.DEFAULT);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM,
				new BouncyCastleProvider());
		RSAPublicKey publicK = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		BigInteger bigIntModulus = new BigInteger(publicK.getModulus().toString());
		//指定公钥指数，重新生成公钥
		BigInteger bigIntPrivateExponent = new BigInteger(PUBLIC_EXPONENT);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus,
				bigIntPrivateExponent);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		System.out.println(publicKey.toString());
		return publicKey;

	}

	/**
	 * 公钥加密过程
	 *
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static byte[] encrypt(PublicKey publicKey, byte[] plainTextData)
			throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance(KEY_ALGORITHM,new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int inputLen = plainTextData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(plainTextData, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(plainTextData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}
	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	 * 字节数据转十六进制字符串
	 *
	 * @param data
	 *            输入数据
	 * @return 十六进制内容
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			// 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			// 取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 私钥解密过程
	 *
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance(KEY_ALGORITHM, new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int inputLen = cipherData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher
							.doFinal(cipherData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher
							.doFinal(cipherData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}


	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	public static String getEncodePwdByPublicKey(String pwd,PublicKey publicKey){
		String encodePwd=null;
		try {
			//此处获取公钥地址和文件名需要自行修改或者优化
			/*InputStream inputStream = new FileInputStream(new File(RSAUtil.class.getResource("/").getFile()+"assets/"+File.separator+"key.txt"));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String pkStr = sb.toString();
			PublicKey publicKey = getPublicKey(pkStr);*/
			MyLog.e("待加密数据：" + pwd);
			byte[] encryptByPublicKey = encrypt(publicKey,pwd.getBytes());
			encodePwd = byteArrayToString(encryptByPublicKey).replace(" ", "");
			MyLog.e("密码密文：" + encodePwd);
		} catch (FileNotFoundException e) {
			System.out.println("获取公钥文件失败！");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("公钥加密出错！");
			e.printStackTrace();
		}
		return encodePwd;
	}

}
