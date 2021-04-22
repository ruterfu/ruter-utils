package com.ruterfu.utils;

import com.ruterfu.third_pkg.apache.validator.UrlValidator;
import com.ruterfu.third_pkg.apache.codec.Base64;

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
     */
    public static boolean isPort(int port) {
        return port >= 1 && port <= 65535;
    }

    /**
     * 判断是否为端口(文本)
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
     * 判断一个字符串是否为日期, 必须满足 2020-02-12 不能是 2020-2-12
     * @param s
     * @return
     */
    public static boolean isDate(String s) {
        if(isNull(s)) return false;
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        return match.matches();
    }

    /**
     * 判断字符串是否为日期, 并符合某一个格式
     * @param pat 有YYYYMM, YYYYMMDD,MMDD这几个选择
     */
    public static boolean isDate(String s, String pat) {
        if(isNull(s) || RtUtil.isNull(pat)) return false;
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
        Matcher match = pattern.matcher(s);
        return match.matches();
    }

    /**
     * 判断是否是时间, 必须是03:04的格式
     */
    public static boolean isTime(String s) {
        if(isNull(s)) return false;
        String regex = "\\d{2}:\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        return match.matches();
    }

    /**
     * 获得一个文件名的文件扩展名, 如果没有扩展名会返回空字符串
     */
    public static String getSuffix(String name) {
        int index = isNull(name) ? -1 : name.lastIndexOf(".");
        if(index >= 0) {
            return name.substring(index + 1);
        }
        return "";
    }

    /**
     * 创建一个文件夹, 如果files是文件会创建上层文件夹
     */
    public static boolean makeDirs(File file) {
        return makeDirs(file, file.isFile());
    }

    /**
     * 创建一个文件夹, 如果files是文件会创建上层文件夹, 或可以规定只创建上层文件夹
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
     */
    public static String emptyForNull(String data) {
        return RtUtil.isNull(data) ? "" : data;
    }


    /**
     * 判断是否为IP
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
     * 判断是否为空
     */
    public static boolean isNull(String text) {
        return text == null || text.length() == 0 || text.trim().length() == 0;
    }

    /**
     * 判断是否为空, 其中Object == null 即为空
     */
    public static boolean isNull(Object text) {
        return text == null;
    }

    /**
     * 合并URL, 将2个URL进行格式化
     * 例如 /aa, /bb会合并成 /aa/bb, 而/aa, bb也会合并成/aa/bb
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
     */
    public static String[] split(String str) {
        return split(str, ",");
    }

    /**
     * 分割字符串, 使用Tokenizer
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
     */
    public static long getOrDefaultLong(String data, long defaultLong) {
        return isNaN(data) ? defaultLong : Long.parseLong(data);
    }

    /**
     * 获得一个字符串, 类似于Map.getOrDefault
     */
    public static String getOrDefaultString(String data, String defaultString) {
        return isNull(data) ? defaultString : data;
    }

    /**
     * 随机一串纯英文字符串, 包含大小写
     */
    public static String randomEnglish(int len) {
        String base = "AOEIUVBPMFDTNLGKHJQXZCSRYWaoeiuvbpmfdtnlgkhjqxzcsryw";
        return random(base, len);
    }
    /**
     * 随机一串纯数字字符串
     */
    public static String randomNumber(int len) {
        String base = "1234567890";
        return random(base, len);
    }
    /**
     * 随机一串英文小写和数字的字符串
     */
    public static String randomEnglishAndNumLower(int len) {
        String base = "abcdef1234567890";
        return random(base, len);
    }
    /**
     * 随机一段类似哈希值
     */
    public static String randomHash(int len) {
        String base = "abcdef1234567890";
        return random(base, len);
    }
    /**
     * 随机一串字符, 包含英文大小写, 数字
     */
    public static String random(int len) {
        String base = "AOEIUVBPMFDTNLGKHJQXZCSRYWaoeiuvbpmfdtnlgkhjqxzcsryw1234567890";
        return random(base, len);
    }

    /**
     * 随机一个UUID, 会移除中间分割线
     */
    public static String randomUUID() {
        String pick = System.currentTimeMillis() + System.currentTimeMillis() + "";
        return UUID.nameUUIDFromBytes(pick.getBytes()).toString().replace("-", "");
    }

    /**
     * 获得当前时间, 并格式化成 2019-12-15 10:10:10 的格式
     */
    public static String getTime() {
        return getTime(System.currentTimeMillis());
    }

    /**
     * 获得指定时间, 并格式化成 2019-12-15 10:10:10 的格式
     */
    public static String getTime(Date date) {
        return getTime(date.getTime());
    }

    /**
     * 获得指定字符串时间, 并格式化成 2019-12-15 10:10:10 的格式
     */
    public static String getTime(String timeStamp) {
        if (isNaN(timeStamp)) {
            return null;
        } else {
            return getTime(Long.parseLong(timeStamp));
        }
    }

    /**
     * 获得指定时间戳的时间, 并格式化成 2019-12-15 10:10:10 的格式
     * 如果
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
     */
    public static String getTime(Long timeStamp, boolean chinese) {
        if(timeStamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(chinese ? "yyyy年MM月dd日 HH:mm:ss" : "yyyy-MM-dd HH:mm:ss");
        return sdf.format(timeStamp);
    }

    /**
     *
     * @param timeStamp 给定一个适合做文件名的时间
     */
    public static String getTimeInFileName(Long timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return sdf.format(timeStamp);
    }

    /**
     * 获得当前时间, 并格式化成 2019-12-15 10:10:10
     */
    public static Date parseTime(String timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        if(isNaN(timeStamp)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return sdf.parse(timeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0);
            }
        } else {
            return new Date(Long.parseLong(timeStamp));
        }
    }

    /**
     * MD5计算
     */
    public static String md5(String source) {
        return hash(source, "MD5");
    }

    /**
     * MD5计算
     */
    public static String md5(Object... sources) {
        try {
            if (sources == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (Object source : sources) {
                stringBuilder.append(source.toString());
            }

            return md5(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SHA1计算
     */
    public static String sha1(String source) {
        return hash(source, "SHA1");
    }

    /**
     * SHA1计算
     */
    public static String sha1(Object... sources) {
        try {
            if (sources == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (Object source : sources) {
                stringBuilder.append(source.toString());
            }

            return sha1(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hash(String source, String algorithm) {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(source.getBytes(StandardCharsets.UTF_8));
            String s = new BigInteger(1, md5Digest.digest()).toString(16);
            if (s.length() != 32) {
                int len = 32 - s.length();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    sb.append("0");
                }
                return sb.toString() + s;
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化Long值的大小改为人可读的文件大小, 单位是B
     * 例如 1024 -> 1KB, 1024 * 1024 -> 1MB
     */
    public static String sizeToHumanReadable(long size) {
        if(size == 0) {
            return "0 B";
        }
        int i = (int)(Math.floor(Math.log(size) / Math.log(1024)));
        String[] sizes = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        BigDecimal decimalFormat = new BigDecimal(size * 1.0);
        BigDecimal decimalFormat2 = new BigDecimal(Math.pow(1024, i) * 1.0);
        return (decimalFormat.divide(decimalFormat2, 2, RoundingMode.UP).doubleValue()) * 1 + " " + sizes[i];
    }

    /**
     * 保留最后2位小数
     * @param value
     * @return
     */
    public static String toFixed(double value) {
        return toFixed(value, "0.00");
    }
    /**
     * 保留最后n位小数
     */
    public static String toFixed(double value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * 多个值乘法计算, 只能算乘法
     * 例如: 传入 [10 * 10 * 15] 返回1500
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
     */
    public static boolean isToday(Long datetime) {
        if (datetime == null) {
            return false;
        }
        long today = System.currentTimeMillis();
        if (Math.abs(today - datetime) > 24 * 60 * 60 * 1000) {
            return false;
        }
        Calendar todayInstance = Calendar.getInstance();

        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(datetime);
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
            if(notHideString == null) {
                dataList.add(s.startsWith("at") ? ("\t" + s) : s);
            } else {
                if(s.contains(notHideString) || length == 0) {
                    dataList.add(s.startsWith("at") ? ("\t" + s) : s);
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
     */
    public static boolean isTrue(Object data) {
        if(data == null) {
            return false;
        } else {
            return "true".equals(data.toString());
        }
    }

    /**
     * Base64相关类, Base64Encode, 需依赖 commons-codec:commons-codec:1.12
     */
    public static String base64Encode(String data) {
        return Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64Decode类
     * @param base64 Base64值
     * @return 返回结果
     */
    public static String base64Decode(String base64) {
        return new String(Base64.decodeBase64(base64), StandardCharsets.UTF_8);
    }

    /**
     * 身份证校验, 如果输入15位, 则直接通过
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
     * 从身份证中获得身份证信息, 15为的身份证直接进行截取, 而17位的身份证会进行校验
     * @param idCardNumber 身份证号码
     * @return 返回结果
     * {
     *     "province": 33, 省份区号
     *     "city": 10, // 市级区号
     *     "area": 00, // 区级区号
     *     "born": "1994-01-01", // 出生年-月-日
     *     "bornYear": 1994, // 出生年
     *     "bornMonth": 2, // 出生月
     *     "bornDate": 1, // 出生日期
     *     "boy": true // 是否为男生
     * }
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
            boolean boy = ((isFifteenLength ? idCardNumber.charAt(14) : idCardNumber.charAt(16)) - '0') % 2 != 0;
            Map<String, Object> obj = new HashMap<>();
            obj.put("province", Long.parseLong(provinceInfo));
            obj.put("city", Long.parseLong(cityInfo));
            obj.put("area", Long.parseLong(areaInfo));
            obj.put("born", year + "-" + month + "-" + date);
            obj.put("bornYear", Long.parseLong(year));
            obj.put("bornMonth", Long.parseLong(month));
            obj.put("bornDate", Long.parseLong(date));
            obj.put("boy", boy);
            return obj;
        } else {
            return null;
        }
    }

    /**
     * 页码转下标
     */
    public static long pageIndexToOffset(long page, long parsedPagePerSize) {
        return page * parsedPagePerSize;
    }

    /**
     * 下标转页码
     */
    public static long pageOffsetToIndex(long index, long parsedPagePerSize) {
        return index / parsedPagePerSize;
    }

    /**
     * 判断是否为JSON, 需依赖 com.alibaba:fastjson
     */
    public static boolean isJsonFormat(String collect) {
        try {
            if (collect.startsWith("{") && collect.endsWith("}")) {
                return true;
            } else if(collect.startsWith("[") && collect.endsWith("]")) {
                return true;
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 文本缩短, 会去掉 换行符, 制表符
     */
    public static String hideString(String text, int len) {
        if (text == null) {
            return "";
        }
        text = text.replace("\r", "");
        text = text.replace("\n", "");
        text = text.replace("\t", "");
        text = text.replace("  ", "");
        if (text.length() > len) {
            return text.substring(0, len) + "...";
        } else {
            return text;
        }
    }

    /**
     * 字符串join, 主要兼容java8前的代码
     */
    public static String join(List<String> textArray) {
        return String.join(",", textArray);
    }

    /**
     * 像Logger一样的打印输出 Info
     */
    public static String printLoggerLike(String message) {
        return printLoggerLike(message, "\033[32;0m" + "INFO");

    }

    /**
     * 像Logger一样的打印输出 Warning
     */
    public static String printWarningLike(String message) {
        return printLoggerLike(message, "\033[33;0m" + "WARNING");
    }

    /**
     * 像Logger一样的打印输出 Error
     */
    public static String printErrorLike(String message) {
        return printLoggerLike(message, "\033[31;0m" + "ERROR");
    }

    /**
     * 像Logger一样的打印输出 Debug
     */
    public static String printDebugLike(String message) {
        return printLoggerLike(message, "\033[32;0m" + "DEBUG");
    }

    /**
     * 获得当前时间 20200101010101
     */
    public static String getNowTimeString() {
        String[] data = getTimeStringArray();
        return data[0] + data[1] + data[2] + data[3] + data[4] + data[5];
    }

    /**
     * 获得当前时间 20200101
     */
    public static String getTodayTimeString() {
        String[] data = getTimeStringArray();
        return data[0] + data[1] + data[2];
    }

    /**
     * Gzip压缩
     */
    public static byte[] gzip(byte[] unGzipStr) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(unGzipStr);
            // 必须提前close掉, 不然就乱了
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            return encode;
        } catch (IOException e) {
            e.printStackTrace();
            return unGzipStr;
        }
    }

    /**
     * Gzip解压
     */
    public static byte[] unGzip(byte[] t) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream(); ByteArrayInputStream in = new ByteArrayInputStream(t); GZIPInputStream gzip = new GZIPInputStream(in)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 尝试获得本机IP
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

    // 私有方法 开始 ====================================================
    private static String printLoggerLike(String message, String level) {
        String lastClass = null;
        try {

            StackTraceElement[] classArray= new Exception().getStackTrace();
            if(classArray.length > 0) {
                StackTraceElement stackTraceElement = classArray[classArray.length - 1];
                lastClass = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
            }
        } catch (Exception ignored) { }

        String timer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
        System.out.println(timer + "  " + level + " \033[35;0m" + Thread.currentThread().getId() + "\033[0m" +
                " --- \033[36;0m" + lastClass + " : \033[0m" + message);
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

    private static String random(String rand, int len) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        len = len < 1 || len > 100 ? 10 : len;
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(rand.length());
            sb.append(rand.charAt(number));
        }
        return sb.toString();
    }
    private static String[] getTimeStringArray() {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        String m = singleToTen(calendar.get(Calendar.MONTH) + 1);
        String d = singleToTen(calendar.get(Calendar.DAY_OF_MONTH));
        String h = singleToTen(calendar.get(Calendar.HOUR_OF_DAY));
        String min = singleToTen(calendar.get(Calendar.MINUTE));
        String sec = singleToTen(calendar.get(Calendar.SECOND));
        return  new String[]{y + "", m, d, h, min, sec};
    }
    private static String singleToTen(int i) {
        return i < 10 ? "0" + i : i + "";
    }
    // 私有方法 结束 ====================================================
}