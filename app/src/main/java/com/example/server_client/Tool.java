package com.example.server_client;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Tool {
	public Tool(){

	}
	public static Context mContext;
	public static String STORE_DIR;
	public static void save(Context context,String dataText, String fileName){
		save(context,dataText,fileName,"","");
	}
	public static void save(Context context,String dataText, String fileName,String addDir){
		save(context,dataText,fileName,addDir,"");
	}
	public static void save(Context context, String dataText, String fileName,String addDir, String savePath) {
		mContext = context;
		if (TextUtils.isEmpty(savePath)) {
			File externalFilesDir = mContext.getExternalFilesDir(null);

			if (externalFilesDir != null) {
				if (addDir.length()!=0){
					STORE_DIR = externalFilesDir.getAbsolutePath()+"/"+addDir;
				}else {
					STORE_DIR = externalFilesDir.getAbsolutePath();
				}
				Log.d("sssss", "externalFilesDir:" + STORE_DIR);
			} else {
				Toast.makeText(mContext, "No save path assigned!", Toast.LENGTH_SHORT);
			}
		} else {
			STORE_DIR = savePath;
		}

		File file = new File(STORE_DIR + "/");
		if (file.exists() == false) {
			file.mkdirs();
		}
		try {
			File file2 = new File(STORE_DIR + "/", fileName + ".txt");
			FileWriter fw = new FileWriter(file2, false);// turn:直接寫入 false:空白寫入
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "UTF-8"));

			bw.write(dataText);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public static String read(Context context,String fileName) {
		return read(context,fileName,"");
	}
	public static String read(Context context,String fileName,String addDir) {
		return read(context,fileName,addDir,"");
	}

	public static String read(Context context,String fileName,String addDir, String savePath) {
		mContext = context;
		String data;
		if (TextUtils.isEmpty(savePath)) {
			File externalFilesDir = mContext.getExternalFilesDir(null);

			if (externalFilesDir != null) {
				if (addDir.length()!=0){
					STORE_DIR = externalFilesDir.getAbsolutePath()+"/"+addDir;
				}else {
					STORE_DIR = externalFilesDir.getAbsolutePath();
				}
				Log.d("sssss", "externalFilesDir:" + STORE_DIR);
			} else {
				Toast.makeText(mContext, "No save path assigned!", Toast.LENGTH_SHORT);
			}
		} else {
			STORE_DIR = savePath;
		}
		File file = new File(STORE_DIR + "/");
		if (file.exists() == false) {
			file.mkdirs();
		}
		try {
			InputStreamReader fr = new InputStreamReader(new FileInputStream(STORE_DIR+"/"+fileName + ".txt"), "UTF-8");
			BufferedReader br = new BufferedReader(fr);
			String readData = "";
			String temp = br.readLine();
			while (temp != null) {
				readData += temp;
				temp = br.readLine();
			}
			data = readData;

		} catch (Exception e) {
			// e.printStackTrace();
			data = "";
		}
		return data;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!deleteDir(files[i]))
					return false;
			}
		}
		// 目錄現在清空了可以刪除
		return dir.delete();
	}

	public static void downloadImage(String urlString, String filename) {
		downloadImage(urlString, filename, "");
	}

	public static void downloadImage(String urlString, String filename, String Referer) {
		downloadImage(urlString, filename, Referer, "");
	}

	public static void downloadImage(String urlString, String filename, String Referer, String path) {
		
		if (path.length() == 0) {
			path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			path = path.replace("/", "\\");
			path = path.substring(1);
			path = path.replace("%20", " ");
			

		}
		File file = new File(path.substring(0, path.lastIndexOf("\\")+1));
		System.out.println(path.substring(0, path.lastIndexOf("\\")+1));
		if (file.exists() == false) {
			file.mkdirs();
		}

		try {
			URL url = new URL(urlString);// 构造URL
			URLConnection con = url.openConnection();// 打开连接
			if (Referer.length() != 0) {
				con.setRequestProperty("Referer", Referer);
			}

			InputStream is = con.getInputStream();// 输入流

			// InputStream in = new URL(urlString).openStream(); // 從網址上下載

			byte[] bs = new byte[1024];// 1K的数据缓冲
			int len;// 读取到的数据长度
			OutputStream os = new FileOutputStream(path + filename);// 输出的文件流

			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			} // 开始读取

			os.close();// 完毕，关闭所有链接
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}