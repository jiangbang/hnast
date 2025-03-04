package com.glface.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 */
@Slf4j
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	private static final char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	/**
	 * _aa__Bc_C_c_  ==>  aaBcCc
	 * @param str
	 * @return 驼峰命名字符串
	 */
	public static String camelCase(String str){
		String camelCase = "";
		String [] arr = str.split("_");
		List<String> list = new ArrayList<String>();

		//将数组中非空字符串添加至list
		for(String a : arr){
			if(a.length() > 0){
				list.add(a);
			}
		}

		for(int i=0;i<list.size();i++){
			if(i>0){	//后面单词首字母大写
				char c = list.get(i).charAt(0);
				String s = String.valueOf(c).toUpperCase() + list.get(i).substring(1).toLowerCase();
				camelCase+=s;
			}else{	//首个单词小写
				camelCase+=list.get(i).toLowerCase();
			}
		}
		return camelCase;
	}

	public static String lowerFirst(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
	}

	public static String upperFirst(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 *
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str))
					.toCharArray()) {
				currentLength++;
				/*currentLength += String.valueOf(c).getBytes("GBK").length;*/
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 *
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
		return abbr(replaceHtml(str), length);
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}

	/**
	 * InputStream 转 String
	 * 此方法可以防止中文乱码
	 *
	 * @param inputStream InputStream
	 * @return String
	 */
	public static String inputStreamToString(InputStream inputStream) throws Exception {
		ByteArrayOutputStream boa = new ByteArrayOutputStream();
		try {
			int length = 0;
			byte[] buffer = new byte[1024];
			while ((length = inputStream.read(buffer)) > -1) {
				boa.write(buffer, 0, length);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			inputStream.close();
			boa.close();
		}

		byte[] result = boa.toByteArray();
		String temp = new String(result);
		if (temp.contains("utf-8")) {
			return new String(result, StandardCharsets.UTF_8);
		} else if (temp.contains("gb2312")) {
			return new String(result, "gb2312");
		} else {
			return new String(result, StandardCharsets.UTF_8);
		}
	}

	/**
	 * 获取当前主机的 Local Host
	 *
	 * @return String
	 */
	public static String localHost() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取电脑 Mac 物理地址列表
	 *
	 * @return Mac Array
	 */
	private List<String> localMacList() {
		ArrayList<String> macList = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			StringBuilder stringBuilder = new StringBuilder();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				List<InterfaceAddress> interfaceAddressList = networkInterface.getInterfaceAddresses();
				for (InterfaceAddress interfaceAddress : interfaceAddressList) {
					InetAddress inetAddress = interfaceAddress.getAddress();
					NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
					if (network == null) {
						continue;
					}
					byte[] mac = network.getHardwareAddress();
					if (mac == null) {
						continue;
					}
					stringBuilder.delete(0, stringBuilder.length());
					for (int i = 0; i < mac.length; i++) {
						stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
					macList.add(stringBuilder.toString());
				}
			}
			if (macList.size() > 0) {
				return macList.stream().distinct().collect(Collectors.toList());
			}
		} catch (Exception ignored) {
		}
		return macList;
	}

	/**
	 * Given an address resolve it to as many unique addresses or hostnames as can be found.
	 *
	 * @param address the address to resolve.
	 * @return the addresses and hostnames that were resolved from {@code address}.
	 */
	public static Set<String> getHostNames(String address) {
		return getHostNames(address, true);
	}

	/**
	 * Given an address resolve it to as many unique addresses or hostnames as can be found.
	 *
	 * @param address         the address to resolve.
	 * @param includeLoopback if {@code true} loopback addresses will be included in the returned set.
	 * @return the addresses and hostnames that were resolved from {@code address}.
	 */
	public static Set<String> getHostNames(String address, boolean includeLoopback) {
		Set<String> hostNames = newHashSet();

		try {
			InetAddress inetAddress = InetAddress.getByName(address);

			if (inetAddress.isAnyLocalAddress()) {
				try {
					Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

					for (NetworkInterface ni : Collections.list(nis)) {
						Collections.list(ni.getInetAddresses()).forEach(ia -> {
							if (ia instanceof Inet4Address) {
								boolean loopback = ia.isLoopbackAddress();

								if (!loopback || includeLoopback) {
									hostNames.add(ia.getHostName());
									hostNames.add(ia.getHostAddress());
									hostNames.add(ia.getCanonicalHostName());
								}
							}
						});
					}
				} catch (SocketException e) {
					log.warn("Failed to NetworkInterfaces for bind address: {}", address, e);
				}
			} else {
				boolean loopback = inetAddress.isLoopbackAddress();

				if (!loopback || includeLoopback) {
					hostNames.add(inetAddress.getHostName());
					hostNames.add(inetAddress.getHostAddress());
					hostNames.add(inetAddress.getCanonicalHostName());
				}
			}
		} catch (UnknownHostException e) {
			log.warn("Failed to get InetAddress for bind address: {}", address, e);
		}

		return hostNames;
	}

	public static String digitUppercase(double n) {
		String fraction[] = { "角", "分" };
		String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

		String head = n < 0 ? "负" : "";
		n = Math.abs(n);

		String s = "";
		for (int i = 0; i < fraction.length; i++) {
			/*
			 * s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] +
			 * fraction[i]) .replaceAll("(零.)+", "");
			 */
			double f1 = new BigDecimal(n).setScale(2, BigDecimal.ROUND_HALF_UP)
					.multiply(new BigDecimal(10 * Math.pow(10, i)))
					.doubleValue();
			s += (digit[(int) (Math.floor(f1) % 10)] + fraction[i]).replaceAll(
					"(零.)+", "");
		}
		if (s.length() < 1) {
			s = "整";
		}
		int integerPart = (int) Math.floor(n);

		for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
			String p = "";
			for (int j = 0; j < unit[1].length && n > 0; j++) {
				p = digit[integerPart % 10] + unit[1][j] + p;
				integerPart = integerPart / 10;
			}
			s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i]
					+ s;
		}
		return head
				+ s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "")
						.replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}



	/**
	 * 一个十六进制数（Hex），正好为4个二进制位。一个字节（byte）为8个二进制位。因此，一个字节可表示为两个十六进制数字。
	 * 我们可以将一个byte用两个Hex表示，同理，我们也可以将两个Hex转换为一个byte
	 * byte[]数组转十六进制
	 */
	public static String bytes2hexStr(byte[] bytes) {
		int len = bytes.length;
		if (len==0) {
			return null;
		}
		char[] cbuf = new char[len*2];
		for (int i=0; i<len; i++) {
			int x = i*2;
			cbuf[x]     = HEX_CHARS[(bytes[i] >>> 4) & 0xf];
			cbuf[x+1]    = HEX_CHARS[bytes[i] & 0xf];
		}
		return new String(cbuf);
	}


	/**
	 * 十六进制转byte[]数组
	 */
	public static byte[] hexStr2bytes(String hexStr) {
		if(isBlank(hexStr)) {
			return null;
		}
		if(hexStr.length()%2 != 0) {//长度为单数
			hexStr = "0" + hexStr;//前面补0
		}
		char[] chars = hexStr.toCharArray();
		int len = chars.length/2;
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			int x = i*2;
			bytes[i] = (byte)Integer.parseInt(String.valueOf(new char[]{chars[x], chars[x+1]}), 16);
		}
		return bytes;
	}

	/**
	 * 产生一个指定范围的随机数 包括min和max
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randNumber(int min,int max){
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}

	public static void main(String[] args) {
		String str="关于开展市级先进农村专业技术协会、农村科普示范基地的农村科普带头人和城镇";
		System.out.println(abbr(str,10));
	}

	public static String replaceHtmlBr(String content){
		if(isBlank(content)){
			return "";
		}
		return content.replaceAll("[\\r\\n]", "<br>");
	}
	public static String replaceWordNewLine(String content){
		if(isBlank(content)){
			return "";
		}
		//获取当前系统支持的换行符
		String line = System.getProperty("line.separator");
		return content.replaceAll("[\\r\\n]",line);
	}
}
