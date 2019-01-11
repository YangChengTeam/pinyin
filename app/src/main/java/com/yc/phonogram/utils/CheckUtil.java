package com.yc.phonogram.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangkai on 16/9/19.
 */
public class CheckUtil {

	public static List<String> packageNames = new ArrayList();

	public static void setPackageNames(Context context) {
		if (packageNames.size() > 0) {
			return;
		}
		List<ApplicationInfo> applicationInfos = context.getPackageManager().getInstalledApplications(0);
		for (int i = 0; i < applicationInfos.size(); i++) {
			ApplicationInfo applicationInfo = applicationInfos.get(i);
			packageNames.add(applicationInfo.packageName);
		}
	}

	/// < 表单检测
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isPhone(String phone) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	public static boolean isWeixinAvilible(Context context) {
		if (packageNames != null) {
			for (int i = 0; i < packageNames.size(); i++) {
				String pn = packageNames.get(i);
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWxInstall(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isQQAvilible(Context context) {
		if (packageNames != null) {
			for (int i = 0; i < packageNames.size(); i++) {
				String pn = packageNames.get(i);
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isQQClientAvailable(Context context) {
		if (packageNames != null) {
			for (int i = 0; i < packageNames.size(); i++) {
				String pn = packageNames.get(i);
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWeiboAvilible(Context context) {
		if (packageNames != null) {
			for (int i = 0; i < packageNames.size(); i++) {
				String pn = packageNames.get(i);
				if (pn.equals("com.sina.weibo")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkAliPayInstalled(Context context) {
		Uri uri = Uri.parse("alipays://platformapi/startApp");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ComponentName componentName = intent.resolveActivity(context.getPackageManager());
		return componentName != null;
	}

	/// < url检测
	public static boolean is404NotFound(String urlStr) {
		boolean result = false;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			int responseCode = httpURLConnection.getResponseCode();
			httpURLConnection.disconnect();
			result = responseCode != HttpURLConnection.HTTP_OK ? true : false;
		} catch (Exception e) {
			result = true;
		}
		return result;
	}

	public static String checkDesc(String desc) {
		String tmp = desc;
		if (desc == null || desc.isEmpty() || desc.equals("null")) {
			tmp = "暂无描述";
		}
		return tmp;
	}

	public static String checkStr(String desc, String dest) {
		String tmp = desc;
		if (desc == null || desc.isEmpty() || desc.equals("null")) {
			tmp = dest;
		}
		return tmp;
	}

	/**
	 * 检测网络是否可用
	 *
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：没有网络 1：WIFI网络 2：2G/3G/4G
	 */
	public static final int NETTYPE_NO = 0x00;
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_TYPE_MOBILE = 0x02;

	public static int getNetworkType(Context context) {
		int netType = NETTYPE_NO;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo != null && !extraInfo.isEmpty()) {
				netType = NETTYPE_TYPE_MOBILE;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	public static boolean isInternetAvailable(String domin) {
		try {
			InetAddress ipAddr = InetAddress.getByName(domin); // You can
																// replace it
																// with your
																// name
			return !ipAddr.equals("");
		} catch (Exception e) {
			return false;
		}
	}

	/// < 安装包检测
	public static boolean isInstall(Context context, String packageName) {
		if (packageName == null) {
			return false;
		}
		setPackageNames(context);
		for (int i = 0; i < packageNames.size(); i++) {
			if (packageName.equals(packageNames.get(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInstallGameBox(Context context) {
		Uri uri = Uri.parse("gamebox://?act=MainActivity");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ComponentName componentName = intent.resolveActivity(context.getPackageManager());
		return componentName != null;
	}

	// 判断手机使用是wifi还是mobile
	/**
	 * 判断手机是否采用wifi连接
	 */
	public static boolean isWIFIConnected(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	// 获取下载文件的长度
	public static int getFileLengthByUrl(String urlStr) {
		int fileLength = -1;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
			httpURLConnection.connect();
			
			if (httpURLConnection.getResponseCode() == 200) {
				fileLength = httpURLConnection.getContentLength();
			}
		} catch (Exception e) {
			fileLength = -1;
		}
		return fileLength;
	}

}
