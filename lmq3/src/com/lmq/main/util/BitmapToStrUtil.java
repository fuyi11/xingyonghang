package com.lmq.main.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Base64;

public class BitmapToStrUtil {

	/**
	 * 将文件转成base64 字符串
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}

	/**
	 * 将base64字符解码保存文件)
	 * 
	 * @param base64Code
	 * @param savePath
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code, String savePath)
			throws Exception {
		// byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
		FileOutputStream out = new FileOutputStream(savePath);
		Bitmap bitmap = null;
		out.write(buffer);
		out.close();
	}

}
