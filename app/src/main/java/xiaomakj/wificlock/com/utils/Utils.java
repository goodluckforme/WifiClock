package xiaomakj.wificlock.com.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xiaomakj.wificlock.com.App;

/**
 * Created by Administrator on 2017/2/16.
 */

public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return [0-9]{5,9}
     */
    public static boolean isMobile(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^1(([3][0123456789])|([4][0123456789])|([5][0123456789])|([7][0123456789])|([8][0123456789]))[0-9]{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 格式化指定的时间格式
     *
     * @param match
     * @return
     */
    public static String getDateTime(String match, Date data) {
        return DateFormat.format(match, data).toString();
    }

    /**
     * 格式化指定的时间戳时间格式
     *
     * @return
     */
    public static String getDateTime(int data) {
        return DateFormat.format("YYYY-MM-dd:HH:mm", data * 1000).toString();
    }

    /**
     * @param count
     * @return 获取模拟的数据
     */
    public static List<String> getItems(int count) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            strings.add("item" + i);
        }
        return strings;
    }

    /**
     * @return 当前线程是否是主线程
     */
    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * @param context
     * @return 应用名称
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }


    /**
     * 以下为活去资源文件
     *
     * @param context
     * @param color
     * @return
     */
    public static int getColor(Context context, int color) {
        return context.getResources().getColor(color);
    }

    public static Drawable getDrawable(Context context, int drawable) {
        return context.getResources().getDrawable(drawable);
    }


    public static String[] getArrayList(Context context, int array) {
        return context.getResources().getStringArray(array);
    }

    public static String getString(Context context, int stringId) {
        return context.getResources().getString(stringId);
    }

    /**
     * 获取软件版本号
     *
     * @return versionCode
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            String packageName = App.instance.getPackageName();
            versionCode = App.instance.getPackageManager().
                    getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getPicNameFromPath(String picturePath) {
        String temp[] = picturePath.replaceAll("\\\\", "/").split("/");
        String fileName = "";
        if (temp.length > 1) {
            fileName = temp[temp.length - 1];
        }
        return fileName;
    }

    final static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离：单位为公里
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s *= EARTH_RADIUS;
        Log.i("距离", s + "");
        //double result = new BigDecimal(distance + s).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //double result = distance + Math.round(s * 10000) / 10000;
        return s;
    }
}
