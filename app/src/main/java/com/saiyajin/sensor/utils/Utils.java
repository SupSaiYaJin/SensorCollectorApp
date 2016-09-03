package com.saiyajin.sensor.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class Utils {
	public static void saveFile(String str, String fileName) {
		FileOutputStream fileOutputStream = null;
		File file = new File(Environment.getExternalStorageDirectory(), fileName);
		try {
			fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(str.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
