package com.ruterfu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruterfu.thirdpkg.apache.validator.UrlValidator;
import com.ruterfu.thirdpkg.apache.codec.Base64;
import com.ruterfu.utils.aes.AES;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * PackageName Ruter Utils
 * User ruter
 * Author Ruter
 * Time 10:26 21/09/2017
 * 最后更新 2019年12月23日
 * Ruter的工具包
 */
public class RtUtil {
    /**
     * 判断是否为IP
     * @param text 输入IP地址
     * @return 返回是否正确
     */
    public static boolean ipCheck(String text) {
        if (isNull(text)) {
            return false;
        }
        // 定义正则表达式
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        // 判断ip地址是否与正则表达式匹配
        if (text.matches(regex)) {
            // 返回判断信息
            return true;
        } else {
            // 返回判断信息
            return false;
        }
    }

    /**
     * 判断是否为URL, 需依赖 commons-validator:commons-validator:1.6
     * @param str 文本
     * @return 结果
     */
    public static boolean isURL(String str){
        if (isNull(str)) {
            return false;
        }
        UrlValidator validator = new UrlValidator();
        return validator.isValid(str);
    }

    /**
     * 判断是否为端口号
     * @param port 端口
     * @return 是否端口号
     */
    public static boolean isPort(int port) {
        return port >= 1 && port <= 65535;
    }

    /**
     * 判断是否为端口(文本)
     * @param port 端口
     * @return 是否端口号
     */
    public static boolean isPort(String port) {
        if(isNaN(port) && port.length() > 5) {
            return false;
        } else {
            return isPort(Integer.parseInt(port));
        }
    }

    /**
     * 判断是否为非数字
     * @param number 某个字符串数字
     * @return 判断是否为数字
     */
    public static boolean isNaN(String number){
        if(isNull(number)) return true;
        String regex = "([0-9])+";
        // 如果以负号开头, 移除负号
        if(number.startsWith("-")) {
            number = number.substring(1);
        }
        // 带小数部分会被分成2个数组, 要求每一个数组都是正整数
        int numberInPoint = number.indexOf(".");
        String number0 = numberInPoint == -1 ? number : number.substring(0, numberInPoint);
        String number1 = numberInPoint == -1 ? null : number.substring(numberInPoint + 1);
        Pattern pattern = Pattern.compile(regex);
        // 当小数后为null时表示非小数, 此时判断number0是否为数字且正数, 否则判断number0和number1是否为数字且正数, 然后最前面加一个!取反
        return !(number1 == null ? pattern.matcher(number0).matches() : pattern.matcher(number0).matches() && pattern.matcher(number1).matches());
    }

    /**
     * 判断一个字符串是否为日期,
     * 必须满足 2020-02-12 不能是 2020-2-12
     * @param datetime 某个日期
     * @return 是否日期
     */
    public static boolean isDate(String datetime) {
        if(isNull(datetime)) return false;
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(datetime);
        return match.matches();
    }

    /**
     * 判断字符串是否为日期, 并符合某一个格式
     * @param pat 有YYYYMM, YYYYMMDD,MMDD这几个选择
     * @param datetime 日期
     * @return 结果
     */
    public static boolean isDate(String datetime, String pat) {
        if(isNull(datetime) || RtUtil.isNull(pat)) return false;
        String regex;
        if(pat.equals("YYYYMM")) {
            regex = "20\\d\\d(0[1-9]|1[0-2])";
        } else if(pat.equals("YYYYMMDD")) {
            regex = "20\\d\\d(0[1-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1])";
        } else if(pat.equals("MMDD")) {
            regex = "(0[1-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1])";
        } else {
            return false;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(datetime);
        return match.matches();
    }

    /**
     * 判断是否是时间
     * 必须是03:04的格式
     * @param time 时间字符串
     * @return 是否符合时间格式
     */
    public static boolean isTime(String time) {
        if(isNull(time)) return false;
        String regex = "\\d{2}:\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(time);
        return match.matches();
    }

    /**
     * 获得一个文件名的文件扩展名, 如果没有扩展名会返回空字符串
     * @param name 文件名
     * @return 返回扩展名
     */
    public static String getSuffix(String name) {
        if(isNull(name)) {
            return "";
        }
        String tmpName = name.toLowerCase();
        if(tmpName.startsWith("http://") || tmpName.startsWith("https://")) {
            int w = tmpName.lastIndexOf("?");
            String url = w == -1 ? tmpName : tmpName.substring(0, w);
            int z = url.lastIndexOf("/");
            name = z == -1 ? null : url.substring(z + 1);
        }
        int index = isNull(name) ? -1 : name.lastIndexOf(".");
        if(index >= 0) {
            return name.substring(index + 1);
        }
        return "";
    }

    /**
     * 创建一个文件夹, 如果files是文件会创建上层文件夹
     * @param file 文件路径
     * @return 是否创建成功
     */
    public static boolean makeDirs(File file) {
        return makeDirs(file, file.isFile());
    }

    /**
     * 创建一个文件夹, 如果files是文件会创建上层文件夹, 或可以规定只创建上层文件夹
     * @param file 文件路径
     * @param makeParent 是否只创建父级，如果file是文件，那这个值无意义
     * @return 是否创建成功
     */
    public static boolean makeDirs(File file, boolean makeParent) {
        if(makeParent || file.isFile()) {
            return file.getParentFile().exists() || file.getParentFile().mkdirs();
        } else {
            return file.exists() || file.mkdirs();
        }
    }

    /**
     * 当字符串为null时,返回空字符串
     * @param data 字符串
     * @return 当字符串为null时,返回空字符串
     */
    public static String emptyForNull(String data) {
        return RtUtil.isNull(data) ? "" : data;
    }


    /**
     * 判断是否为IP
     * @param addr IP地址字符串
     * @return 是否IP
     */
    public boolean isIP(String addr) {
        if(isNull(addr)) return false;
        if(addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    /**
     * 判断是否为空(复制 org.apache.commons.lang3.StringUtils#isBlank)
     * @param cs 文本
     * @return 文本为null 或空字符串，或trim后为空字符串均视为null
     */
    public static boolean isNull(final CharSequence cs) {
        int strLen = cs == null ? 0 : cs.length();
        if (strLen != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }
    /**
     * 判断是否为非空，空字符串页视为空
     * @param cs 文本
     * @return 文本不为null 且不为空字符串，且trim后为不为空字符串
     */
    public static boolean isNotNull(final CharSequence cs) {
        return !isNull(cs);
    }

    /**
     * 判断是否为空文本（不等同于isNull，复制 org.apache.commons.lang3.StringUtils#isEmpty）
     * @param cs 文本
     * @return 文本为null的情况下，空字符串视为文本为空
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断是否为空, 其中Object == null 即为空
     * @param obj 对象
     * @return 对象 == null即视为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 合并URL, 将2个URL进行格式化
     * 例如 /aa, /bb会合并成 /aa/bb, 而/aa, bb也会合并成/aa/bb
     * @param u1 字符串1
     * @param u2 字符串2
     * @return 合并后的字符串
     */
    public static String joinUrl(String u1, String u2) {
        if(RtUtil.isNull(u1)) {
            u1 = "/";
        }
        if(RtUtil.isNull(u2)) {
            u2 = "/";
        }
        char c1 = u1.charAt(u1.length() - 1);
        char c2 = u2.charAt(0);
        if(c1 == '/' && c2 == '/') { // 如果是http://11.com/ /api时, 去掉第第二个第一个"/"
            return u1 + u2.substring(1);
        } else if(c1 != '/' && c2 != '/') { // 如果是http://11.com api时, 中间加上/
            return u1 + "/" + u2;
        } else {
            return u1 + u2; // 否则, 直接返回
        }
    }

    /**
     * 分割字符串, 使用Tokenizer, 按,分割
     * @param str 字符串
     * @return 按逗号【,】分割字符串
     */
    public static String[] split(String str) {
        return split(str, ",");
    }

    /**
     * 分割字符串, 使用Tokenizer
     * @param str 字符串
     * @param separator 分隔符
     * @return 按指定分隔符分割后的字符串
     */
    public static String[] split(String str, String separator) {
        if (!str.contains(separator)) {
            if(str.trim().length() == 0) {
                return new String[]{};
            }
            return new String[]{str.trim()};
        }
        StringTokenizer st = new StringTokenizer(str, separator);
        String[] s = new String[st.countTokens()];
        int t = 0;
        while (st.hasMoreTokens()) {
            if (t < s.length) {
                s[t++] = st.nextToken().trim();
            }
        }
        return s;
    }

    /**
     * 获得一个长整形, 类似于Map.getOrDefault
     * @param data 文本
     * @param defaultLong 若文本不为数字返回的数字
     * @return 返回数字
     */
    public static long getOrDefaultLong(String data, long defaultLong) {
        return isNaN(data) ? defaultLong : Long.parseLong(data);
    }

    /**
     * 获得一个字符串, 类似于Map.getOrDefault
     * @param data 文本
     * @param defaultString 若文本为null或空字符串返回的内容
     * @return 返回内容
     */
    public static String getOrDefaultString(String data, String defaultString) {
        return isNull(data) ? defaultString : data;
    }

    /**
     * 随机生成一个数字边界的数字
     * @param start 开始位置
     * @param end 结束位置
     * @return 返回这个位置区间的数字，如果结束位置小于开始位置，则会返回开始位置的值
     */
    public static int randomNumBoundary(int start, int end) {
        if(end <= start) {
            return start;
        }
        return new Random().nextInt(end - start + 1) + start;
    }

    /**
     * 随机一串纯英文字符串, 包含大小写
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String randomEnglish(int randomLength) {
        String base = "AOEIUVBPMFDTNLGKHJQXZCSRYWaoeiuvbpmfdtnlgkhjqxzcsryw";
        return random(base, randomLength);
    }

    /**
     * 随机一串纯数字字符串
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String randomNumber(int randomLength) {
        String base = "1234567890";
        return random(base, randomLength);
    }

    /**
     * 随机一串英文小写和数字的字符串
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String randomEnglishAndNumLower(int randomLength) {
        String base = "aoeiuvbpmfdtnlgkhjqxzcsryw1234567890";
        return random(base, randomLength);
    }

    /**
     * 随机一段类哈希字符串， 即由0-9和a-f组成
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String randomHash(int randomLength) {
        String base = "abcdef1234567890";
        return random(base, randomLength);
    }

    /**
     * 随机一串字符, 包含英文大小写, 数字
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String random(int randomLength) {
        return random(randomLength, false);
    }

    /**
     * 随机一串字符, 包含英文大小写, 数字
     * @param randomLength 随机长度
     * @param withSymbol 是否包含特殊符号
     * @return 随机字符串
     */
    public static String random(int randomLength, boolean withSymbol) {
        String base = "AOEIUVBPMFDTNLGKHJQXZCSRYWaoeiuvbpmfdtnlgkhjqxzcsryw1234567890" + (withSymbol ? ",./!@#$%^&*()_+[]\\;'<>?:{}|" : "");
        return random(base, randomLength);
    }

    /**
     * 随机一串字符，自定义随机种子
     * @param rand 随机种子
     * @param randomLength 随机长度
     * @return 随机字符串
     */
    public static String random(String rand, int randomLength) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        randomLength = randomLength < 1 ? 10 : randomLength;
        for (int i = 0; i < randomLength; i++) {
            int number = random.nextInt(rand.length());
            sb.append(rand.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机一个UUID, 会移除中间分割线
     * @return 随机字符串
     */

    public static String randomUUID() {
        return randomUUIDWithLine().replace("-", "");
    }
    public static String randomUUIDWithLine() {
        return UUID.randomUUID().toString();
    }
    public static String randomAES256Key() {
        return random(43);
    }

    /**
     * 获得当前时间
     * @return 当前时间，格式化成yyyy-MM-dd HH:mm:ss的时间
     */
    public static String getTime() {
        return getTime(System.currentTimeMillis());
    }

    /**
     * 获得指定时间, 并格式化成 2019-12-15 10:10:10 的格式
     * @param date 时间
     * @return 格式化成yyyy-MM-dd HH:mm:ss的时间
     */
    public static String getTime(Date date) {
        return getTime(date.getTime());
    }

    /**
     * 获得指定字符串时间, 并格式化成 2019-12-15 10:10:10 的格式
     * @param timeStampString 字符串时间戳
     * @return 格式化成yyyy-MM-dd HH:mm:ss的时间
     */
    public static String getTime(String timeStampString) {
        if (isNaN(timeStampString)) {
            return null;
        } else {
            return getTime(Long.parseLong(timeStampString));
        }
    }

    /**
     * 获得指定时间戳的时间, 并格式化成 2019-12-15 10:10:10 的格式
     * @param timeStamp 时间戳，若时间戳长度小于13，则视为秒为单位，会 * 1000后取时间
     * @return 格式化成yyyy-MM-dd HH:mm:ss的时间
     */
    public static String getTime(Long timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        if(timeStamp < 13) {
            timeStamp = timeStamp * 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timeStamp);
    }

    /**
     * 获得字符串的时间
     * @param chinese 是否返回中文的年月日
     * @param timeStamp 时间戳
     * @return 格式化成YYYY年MM月DD日 HH:mm:ss的时间（chinese == true），或者yyyy-MM-dd HH:mm:ss（chinese == false）
     */
    public static String getTime(Long timeStamp, boolean chinese) {
        if(timeStamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(chinese ? "yyyy年MM月dd日 HH:mm:ss" : "yyyy-MM-dd HH:mm:ss");
        return sdf.format(timeStamp);
    }

    /**
     * 获得一个适合用来做文件名的时间
     * @param timeStamp 时间戳
     * @return 格式化成YYYY_MM_DD_HH_mm_ss的时间
     */
    public static String getTimeInFileName(Long timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return sdf.format(timeStamp);
    }

    /**
     * 获得指定时间的对象
     * @param dateTimeOrTimeStamp 可以是yyyy-MM-dd HH:mm:ss 或时间戳（毫秒）
     * @return 获得date对象
     */
    public static Date parseTime(String dateTimeOrTimeStamp) {
        if (dateTimeOrTimeStamp == null) {
            return null;
        }
        if(isNaN(dateTimeOrTimeStamp)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return sdf.parse(dateTimeOrTimeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0);
            }
        } else {
            return new Date(Long.parseLong(dateTimeOrTimeStamp));
        }
    }

    /**
     * 获得当前时间 20200101010101
     * @return 当前时间
     */
    public static String getNowTimeString() {
        int[] data = getTodayYMDHMSArray();
        return data[0] + singleToTen(data[1]) + singleToTen(data[2]) + singleToTen(data[3]) + singleToTen(data[4]) + singleToTen(data[5]) + "";
    }

    /**
     * 获得当前年月日 20200101
     * @return 当前年月日
     */
    public static String getTodayTimeString() {
        int[] data = getTodayYMDHMSArray();
        return data[0] + singleToTen(data[1]) + singleToTen(data[2]) + "";
    }


    /**
     * 当前时间转int
     * @return 时间int
     */
    public static int[] getTodayYMDHMSArray() {
        Calendar calendar = Calendar.getInstance();
        return  new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)};
    }

    /**
     * MD5计算
     * @param source 源数据
     * @return MD5
     */
    public static String md5(String source) {
        return digest(source.getBytes(StandardCharsets.UTF_8), "MD5");
    }

    /**
     * 计算文件的MD5摘要值
     * @param fileSource 源文件
     * @return 算法值
     */
    public static String md5(File fileSource) {
        return digestFile(fileSource, "MD5");
    }

    /**
     * MD5计算
     * @param sources 源数据(多个，如果数组中是null则视为空字符串处理)
     * @return MD5
     */
    public static String md5(Object... sources) {
        try {
            if (sources == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (Object source : sources) {
                stringBuilder.append(source == null ? "" : source.toString());
            }

            return md5(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SHA1计算
     * @param source 源数据
     * @return SHA1
     */
    public static String sha1(String source) {
        return digest(source.getBytes(StandardCharsets.UTF_8), "SHA1");
    }

    /**
     * 计算文件的SHA1摘要值
     * @param fileSource 源文件
     * @return 算法值
     */
    public static String sha1(File fileSource) {
        return digestFile(fileSource, "SHA1");
    }

    /**
     * MD5计算
     * @param sources 源数据(多个，如果数组中是null则视为空字符串处理)
     * @return MD5
     */
    public static String sha1(Object... sources) {
        try {
            if (sources == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (Object source : sources) {
                stringBuilder.append(source == null ? "" : source.toString());
            }

            return sha1(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按指定算法后进行摘要计算
     * @param source 源文本
     * @param algorithm 算法
     * @return 摘要值
     */
    public static String digest(byte[] source, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(source);
            return toHex(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按指定算法后对文件进行摘要计算
     * @param sourceFile 源文件
     * @param algorithm 算法
     * @return 摘要值
     */
    public static String digestFile(File sourceFile, String algorithm) {
        if(sourceFile == null || !sourceFile.exists()) {
            return null;
        }
        try(FileInputStream fis = new FileInputStream(sourceFile)) {
            MessageDigest digest = MessageDigest.getInstance(isNull(algorithm) ? "MD5" : algorithm);

            byte[] bytes = new byte[2048];
            int len;
            while ((len = fis.read(bytes)) != -1) {
                digest.update(bytes, 0, len);
            }
            return toHex(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化Long值的大小改为人可读的文件大小, 单位是B
     * 例如 1024会返回 1KB, 1048576会返回 1MB
     * @param size 以B为单位的文件大小
     * @return 返回格式化后的文本(保留2位)
     */
    public static String sizeToHumanReadable(long size) {
        return sizeToHumanReadable(size, 2);
    }

    /**
     * 格式化Long值的大小改为人可读的文件大小, 单位是B
     * 例如 1024会返回 1KB, 1048576会返回 1MB
     * @param size 以B为单位的文件大小
     * @param scale 保留位数
     * @return 返回格式化后的文本(保留自定义位数位)
     */
    public static String sizeToHumanReadable(long size, int scale) {
        if(size == 0) {
            return "0 B";
        }
        int i = (int)(Math.floor(Math.log(size) / Math.log(1024)));
        String[] sizes = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        BigDecimal decimalFormat = new BigDecimal(size * 1.0);
        BigDecimal decimalFormat2 = BigDecimal.valueOf(Math.pow(1024, i) * 1.0);
        return (decimalFormat.divide(decimalFormat2, scale, RoundingMode.UP).doubleValue()) * 1 + " " + sizes[i];
    }

    /**
     * 保留最后2位小数
     * @param value 格式化前的小数
     * @return 格式化后的字符串
     */
    public static String toFixedString(double value) {
        return toFixedString(value, "0.00");
    }

    /**
     * 保留最后n位小数, 给定指定匹配
     * @param value 格式化前的小数
     * @param pattern 指定匹配
     * @return 格式化后的字符串
     */
    public static String toFixedString(double value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * 四舍五入，保留最后2位小数
     * @param value 格式化前的小数
     * @return 格式化后的小数
     */
    public static double toFixed(double value) {
        return toFixed(value, 2);
    }

    /**
     * 四舍五入，保留最后n位小数
     * @param value 格式化前的小数
     * @param scale 保留位数
     * @return 格式化后的小数
     */
    public static double toFixed(double value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(scale, RoundingMode.UP).doubleValue();
    }

    /**
     * 多个值乘法计算, 只能算乘法
     * 例如: 传入 [10 * 10 * 15] 返回1500
     * Hint：这个的初衷是为了方便配置springboot中的application.properties，经常遇到配置指定秒的，1天得写86400，我就可以写成24 * 60 * 60了
     * @param multipleCalc 多个乘法表达式， 例如  12 * 14 * 15，不支持计算小数
     * @param defaultValue 报错时的默认值
     * @return 计算值
     */
    public static Long calcMultiply(String multipleCalc, long defaultValue) {
        long start = 1;
        boolean isError = false;
        try {
            StringTokenizer st = new StringTokenizer(multipleCalc, "*");
            while (st.hasMoreTokens()) {
                String calc = st.nextToken().trim();
                if (isNaN(calc)) {
                    isError = true;
                    break;
                } else {
                    long d = Long.parseLong(calc);
                    start = start * d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isError = true;
        }
        return isError ? defaultValue : start;
    }

    /**
     * URLEncoder方法
     * @param src 文本
     * @return URLEncode后的文本
     */
    public static String url(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * URLDecoder方法
     * @param src 文本
     * @return URLDecode后的文本
     */
    public static String unUrl(String src) {
        if (src == null) {
            return null;
        }
        try {
            return URLDecoder.decode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * 判断指定日期是否为今天
     * @param timeStamp 时间戳
     * @return 这个时间戳是否是今天
     */
    public static boolean isToday(Long timeStamp) {
        if (timeStamp == null) {
            return false;
        }
        long today = System.currentTimeMillis();
        if (Math.abs(today - timeStamp) > 24 * 60 * 60 * 1000) {
            return false;
        }
        Calendar todayInstance = Calendar.getInstance();

        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timeStamp);
        return todayInstance.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH) &&
                todayInstance.get(Calendar.MONTH) == target.get(Calendar.MONTH);
    }

    /**
     * 获得堆栈文本(全部)
     * @param throwable 抛出的错误类型
     * @return 返回字符串
     */
    public static String getExceptionStack(Throwable throwable) {
        return getExceptionStack(throwable, null);
    }

    /**
     * 获得堆栈文本(过滤)
     * 这里例如notHideString传入了fubo, 那么除非这一行堆栈有fubo, 否则会返回...
     * Exception: xxx
     *  at com.j.d.q.Exception xxx
     *  at com.fubo.d.q.Exception xxx
     *  at com.d.d.q.Exception xxx
     *  at com.c.d.q.Exception xxx
     *  at com.fubo.d.q.Exception xxx
     * 会返回
     * Exception: xxxxyyyy
     *  ...
     *  at com.fubo.d.q.Exception xxx
     *  ...
     *  at com.fubo.d.q.Exception xxx
     *
     * @param throwable 抛出的错误类型
     * @param notHideString 不隐藏包含指定关键字的堆栈内容
     * @return 返回字符串
     */
    public static String getExceptionStack(Throwable throwable, String notHideString) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String data = sw.toString();
        String[] dataArray = RtUtil.split(data, "\n");
        List<String> dataList = new ArrayList<>();
        boolean stillNotCurrent = false;
        int length = 0;
        for(String s : dataArray) {
            String at = s.startsWith("at") ? ("\t" + s) : s;
            if(notHideString == null) {
                dataList.add(at);
            } else {
                if(s.contains(notHideString) || length == 0) {
                    dataList.add(at);
                    stillNotCurrent = false;
                } else {
                    if(!stillNotCurrent) {
                        dataList.add("...");
                        stillNotCurrent = true;
                    }
                }
            }
            length++;
        }
        return String.join("\n", dataList);
    }

    /**
     * 返回是否为True
     * @param data 字符串
     * @return 当字符串是null时返回false，否则把字符串转成String，然后和true做equal返回结果
     */
    public static boolean isTrue(Object data) {
        if(data == null) {
            return false;
        } else {
            return "true".equalsIgnoreCase(data.toString());
        }
    }

    /**
     * Base64相关类, Base64Encode, 需依赖 commons-codec:commons-codec
     * @param data 文本
     * @return Base64后的文本
     */
    public static String base64Encode(String data) {
        return Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64Decode类, 需依赖 commons-codec:commons-codec
     * @param base64 Base64值
     * @return 返回结果
     */
    public static String base64Decode(String base64) {
        return new String(Base64.decodeBase64(base64), StandardCharsets.UTF_8);
    }

    /**
     * 身份证校验, 如果输入15位, 则直接通过
     * @param idCardNumber 身份证号码
     * @return 返回是否有效
     */
    public static boolean validateIDCard(String idCardNumber) {
        if(isNull(idCardNumber)) {
            return false;
        } else if(idCardNumber.length() == 15) {
            return true;
        } else if(idCardNumber.length() == 18) {
            char[] RANDOM = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
            int[] FACTOR = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
            char[] charArray = idCardNumber.toCharArray();
            char idCardLast = charArray[17];
            // 计算1-17位与相应因子乘积之和
            int total = 0;
            for (int i = 0; i < 17; i++) {
                total += Character.getNumericValue(charArray[i]) * FACTOR[i];
            }
            char ch = RANDOM[total % 11];
            return String.valueOf(ch).toUpperCase().equals(String.valueOf(idCardLast).toUpperCase());
        } else {
            return false;
        }
    }

    /**
     * 从身份证中获得身份证信息, 15为的身份证直接进行截取, 而17位的身份证会进行校验， 返回结果如下
     * {
     *     "province": 33, 省份区号 2位
     *     "city": 10, // 市级区号 2位
     *     "area": 00, // 区级区号 2位
     *     "born": "1994-01-01", // 出生年-月-日 8位
     *     "bornYear": 1994, // 出生年
     *     "bornMonth": 2, // 出生月
     *     "bornDate": 1, // 出生日期
     *     "male": true // 是否为男生
     * }
     * @param idCardNumber 身份证号码
     * @return 返回结果
     *
     */
    public static Map<String, Object> getIdCardInfo(String idCardNumber) {
        if(isNull(idCardNumber)) {
            return null;
        } else if(idCardNumber.length() == 15 || idCardNumber.length() == 18) {
            boolean isFifteenLength = idCardNumber.length() == 15;
            if(!isFifteenLength && !validateIDCard(idCardNumber)) {
                return null;
            }
            String provinceInfo = idCardNumber.substring(0, 2);
            String cityInfo = idCardNumber.substring(2, 4);
            String areaInfo = idCardNumber.substring(4, 6);
            String bornInfo = isFifteenLength ? ("19" + idCardNumber.substring(6, 12)) : idCardNumber.substring(6, 14);
            String year = bornInfo.substring(0, 4);
            String month = bornInfo.substring(4, 6);
            String date = bornInfo.substring(6, 8);
            boolean male = ((isFifteenLength ? idCardNumber.charAt(14) : idCardNumber.charAt(16)) - '0') % 2 != 0;
            Map<String, Object> obj = new HashMap<>();
            obj.put("province", Long.parseLong(provinceInfo));
            obj.put("city", Long.parseLong(cityInfo));
            obj.put("area", Long.parseLong(areaInfo));
            obj.put("born", year + "-" + month + "-" + date);
            obj.put("bornYear", Long.parseLong(year));
            obj.put("bornMonth", Long.parseLong(month));
            obj.put("bornDate", Long.parseLong(date));
            obj.put("male", male);
            return obj;
        } else {
            return null;
        }
    }

    /**
     * 页码转下标，例如 当前时第3页，每页有10个，则返回index是20
     * @param page 页码（第1页开始，0和1均视为第1页）
     * @param pagePerSize 每页多少数量
     * @return 返回下标
     */
    public static long pageIndexToOffset(long page, long pagePerSize) {
        return pagePerSize == 0 ? 0 : Math.max(page - 1, 0) * pagePerSize;
    }

    /**
     * 下标转页码
     * @param index 下标
     * @param pagePerSize 每页多少数量
     * @return 返回页码，页码从第一页开始
     */
    public static long pageOffsetToIndex(long index, long pagePerSize) {
        return (pagePerSize == 0 ? 0 : index / pagePerSize) + 1;
    }

    /**
     * 判断是否长得像JSON
     * 只判断开头和结尾是不是[ ] ， 或者是不是{ }
     * @param string 字符串
     * @return 判断该字符串是否长得像json
     */
    public static boolean isJsonLike(String string) {
        try {
            if (string.startsWith("{") && string.endsWith("}")) {
                return true;
            } else if(string.startsWith("[") && string.endsWith("]")) {
                return true;
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 文本缩短, 会去掉 换行符, 制表符
     * @param text 文本
     * @param limitLength 最大长度
     * @return 缩短后的文本
     */
    public static String hideString(String text, int limitLength) {
        if (text == null) {
            return "";
        }
        text = text.replace("\r", "");
        text = text.replace("\n", "");
        text = text.replace("\t", "");
        text = text.replace("  ", "");
        if (text.length() > limitLength) {
            return text.substring(0, limitLength) + "...";
        } else {
            return text;
        }
    }

    /**
     * 字符串join, 主要兼容java8前的代码
     * @param textArray 字符数组
     * @return join后的字符串
     */
    public static String join(List<String> textArray) {
        return String.join(",", textArray);
    }

    /**
     * 像Logger一样的打印输出 Info
     * @param message 消息
     * @return 返回类Logger的消息
     */
    public static String printLoggerLike(String message) {
        return printLoggerLike(message, false);

    }
    public static String printLoggerLike(String message, boolean withColor) {
        return printLoggerLike(message, (withColor ? "\033[32;0m" : "") + "INFO", withColor);
    }

    /**
     * 像Logger一样的打印输出 warn
     * @param message 消息
     * @return 返回类Logger的消息
     */
    public static String printWarningLike(String message) {
        return printWarningLike(message, false);
    }
    public static String printWarningLike(String message, boolean withColor) {
        return printLoggerLike(message, (withColor ? "\033[33;0m" : "") + "WARNING", withColor);
    }

    /**
     * 像Logger一样的打印输出 error
     * @param message 消息
     * @return 返回类Logger的消息
     */
    public static String printErrorLike(String message) {
        return printErrorLike(message, false);
    }
    public static String printErrorLike(String message, boolean withColor) {
        return printLoggerLike(message, (withColor ? "\033[31;0m" : "") + "ERROR", withColor);
    }

    /**
     * 像Logger一样的打印输出 debug
     * @param message 消息
     * @return 返回类Logger的消息
     */
    public static String printDebugLike(String message) {
        return printDebugLike(message, false);
    }
    public static String printDebugLike(String message, boolean withColor) {
        return printLoggerLike(message, (withColor ? "\033[32;0m" : "") + "DEBUG", withColor);
    }

    /**
     * Gzip压缩
     * @param contentBytes 源内容的字节数组
     * @return Gzip压缩过的字节数组
     */
    public static byte[] gzip(byte[] contentBytes) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(contentBytes);
            // 必须提前close掉, 不然就乱了
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            return encode;
        } catch (IOException e) {
            e.printStackTrace();
            return contentBytes;
        }
    }

    /**
     * Gzip解压
     * @param gzippedContentBytes Gzip压缩过的字节数组
     * @return 源内容的字节数组
     */
    public static byte[] unGzip(byte[] gzippedContentBytes) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream(); ByteArrayInputStream in = new ByteArrayInputStream(gzippedContentBytes); GZIPInputStream gzip = new GZIPInputStream(in)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gzippedContentBytes;
    }

    /**
     * 尝试获得本机IP
     * @return 尝试获得的IP地址
     */
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (!(netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp())) {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从JSON中获取指定key的值
     * 简单循环了下，性能还好，固定json提取6层，循环如下次数，耗时比较
     * count 1, time: 0.4ms（循环1000次取平均值）
     * count 10, time: 17ms
     * count 100, time: 78ms
     * count 1000, time: 334ms
     * count 10000, time: 936ms
     * count 100000, time: 7292ms
     * count 1000000, time: 72308ms
     * 例如 这样的json
     * {
     *     "key1": {
     *         "keyArr1": [
     *              "111",
     *              "222"
     *         ]
     *     }
     * }
     * @param json json字符串
     * @param findKey 查找的Key，如上面的json，想获得 222，可以使用 key1.keyArr1[1] 来获得222
     * @return 返回json具体内容，这取决于查询key的类型，使用的json库是fastjson，所以如果它是json类型的，只会返回 JSONObject，JSONArray，如果是其他类型的，会正常返回Int，String等
     */
    public static Object getJSONValue(String json, String findKey) {
        try {
            Object jsonObject = JSON.parse(json);
            Stack<String> st = new Stack<>();
            List<String> s = Arrays.asList(split(findKey, "."));
            if(s.size() == 0) {
                return jsonObject;
            }
            for (int i = s.size() - 1; i >= 0; i--) {
                st.push(s.get(i));
            }
            return recursiveGetJsonValue(st, jsonObject, new ArrayList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptAESString(String text, String key) {
        return new AES(key).encryptString(text);
    }
    public static byte[] decryptAESString(String text, String key) {
        return new AES(key).decryptString(text);
    }

    // 私有方法 开始 ====================================================

    /**
     * 打印类Logger的消息
     * @param message 消息
     * @param level 级别
     * @return 打印类Logger消息
     */
    private static String printLoggerLike(String message, String level) {
        return printLoggerLike(message, level, false);
    }
    private static String printLoggerLike(String message, String level, boolean withColor) {
        String lastClass = null;
        try {

            StackTraceElement[] classArray= new Exception().getStackTrace();
            if(classArray.length > 0) {
                StackTraceElement stackTraceElement = classArray[classArray.length - 1];
                lastClass = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
            }
        } catch (Exception ignored) { }

        String timer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
        System.out.println(timer + "  " + level + " " + (withColor ? "\033[35;0m" : "") + Thread.currentThread().getId() + (withColor ? "\033[0m" : "") +
                " --- " + (withColor ? "\033[36;0m" : "") + lastClass + " : " + (withColor ? "\033[0m" : "") + message);
        if(level.contains("DEBUG")) {
            return timer + "  DEBUG " + Thread.currentThread().getId() +
                    " --- " + lastClass + " : " + message;
        } else if(level.contains("ERROR")) {
            return timer + "  ERROR " + Thread.currentThread().getId() +
                    " --- " + lastClass + " : " + message;
        } else if(level.contains("WARNING")) {
            return timer + "  WARNING " + Thread.currentThread().getId() +
                    " --- " + lastClass + " : " + message;
        } else {
            return timer + "  INFO " + Thread.currentThread().getId() +
                    " --- " + lastClass + " : " + message;
        }
    }


    /**
     * 将小于10的数字前面加个0
     * @param i 数字
     * @return 字符串 小于10返回0x， 大于10返回本身
     */
    private static String singleToTen(int i) {
        return i < 10 ? "0" + i : i + "";
    }

    private static Object recursiveGetJsonValue(Stack<String> jsonStack, Object json, List<String> poppedKey, int recursiveTime) {
        if(jsonStack == null || jsonStack.empty()) {
            printErrorLike("Json has no stack, return null.");
            return null;
        } else if(recursiveTime > 100) {
            printErrorLike("Json reader stopped, because of too much recursive time, MAX recursive count 100, force return null.");
            return null;
        }
        // 获得待提取的jsonKey
        String key = jsonStack.pop();
        poppedKey.add(key);
        int index = -1;
        // 判断这个key是array还是object，如果 key == null 但是index >= 0 则表示此时从array开始的
        if(key.contains("[") && key.contains("]")) {
            int indexStart = key.indexOf("[");
            int indexEnd = key.indexOf("]");
            index = Integer.parseInt(key.substring(indexStart + 1, indexEnd));
            key = indexStart == 0 ? null : key.substring(0, indexStart);
        }
        Object value;
        // 如果index == -1 表示它一定是jsonObject
        if(index == -1) {
            JSONObject o = json instanceof JSONObject ? (JSONObject) json : null;
            if(o != null && o.containsKey(key)) {
                value = o.get(key);
            } else {
                if(o == null) {
                    printErrorLike("JSONObject on pop " + String.join(".", poppedKey) + " returns null, it maybe null object or not JSONArray object.");
                } else {
                    printErrorLike("JSONObject on pop key " + String.join(".", poppedKey) + " has no value contains");
                }
                return null;
            }
        } else {
            if(key == null) {
                JSONArray o = json instanceof JSONArray ? (JSONArray) json : null;
                if(o == null) {
                    printErrorLike("JSONArray when pop key " + String.join(".", poppedKey) + " returns null, it maybe null object or not JSONArray object.");
                }
                value = o != null && o.size() > index ? o.get(index) : null;
                if(value == null) {
                    printErrorLike("JSONArray on pop key " + String.join(".", poppedKey) + " returns null, it maybe index [" + (index + 1) + "] out of array range [" + (o == null ? "NULL" : o.size()) + "].");
                }
            } else {
                JSONObject o = json instanceof JSONObject ? (JSONObject) json : null;
                if(o == null) {
                    printErrorLike("JSONObject when pop key " + String.join(".", poppedKey) + " returns null, it maybe null object or not JSONObject object.");
                }
                Object ob = o != null && o.containsKey(key) ? o.get(key) : null;
                JSONArray a = ob instanceof JSONArray ? (JSONArray) ob : null;
                if(a == null) {
                    printErrorLike("JSONArray when pop key " + String.join(".", poppedKey) + " returns null, it maybe null object or not JSONArray object or not contains this key.");
                }
                value = a != null && a.size() > index ? a.get(index) : null;
                if(value == null) {
                    printErrorLike("JSONArray on pop key " + String.join(".", poppedKey) + " returns null, it maybe index [" + (index + 1) + "] out of array range [" + (o == null ? "NULL" : o.size()) + "].");
                }
            }
        }
        if(value == null || jsonStack.empty()) {
            return value;
        } else {
            return recursiveGetJsonValue(jsonStack, value, poppedKey, ++recursiveTime);
        }
    }

    private static String toHex(byte[] bytes) {
        String s = new BigInteger(1, bytes).toString(16);
        if (s.length() != 32) {
            int currLen = 32 - s.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currLen; i++) {
                sb.append("0");
            }
            return sb.toString() + s;
        }
        return s;
    }
    // 私有方法 结束 ====================================================
}