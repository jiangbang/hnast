package com.glface.base.utils;

/**
 * 命名转换
 * 驼峰命名 ——下划线小写
 * 下划线 ——驼峰命名(首字母小写，其它单词开头大写)
 * @author maowei
 */
public class NamingStrategyUtils {
	/**
	 * 驼峰命名 ——下划线小写
	 * 将驼峰式命名的字符串转换为下划线小写写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
	 * 例如：helloWorld->hello_word
	 *
	 * @param name
	 *            转换前的驼峰式命名的字符串
	 * @return 转换后下划线大写方式命名的字符串
	 */
	public static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			char[] names = name.toCharArray();
			result.append(names[0]);
			// 加下划线
			for (int i = 1; i < names.length; i++) {
				char c = names[i];
				if (Character.isUpperCase(c)
						&& !Character.isDigit(c)) {
					result.append("_");
				}
				result.append(c);
			}
		}
		return result.toString().toLowerCase();
	}

	/**
	 * 下划线 ——驼峰命名(首字母小写，其它单词开头大写)
	 * 将下划线小写方式命名的字符串转换为驼峰式。如果转换前的下划线小写方式命名的字符串为空，则返回空字符串。</br>
	 * 例如：HELLO_WORLD->helloWorld
	 *
	 * @param name
	 *            转换前的下划线大写方式命名的字符串
	 * @return 转换后的驼峰式命名的字符串
	 */
	public static String camelName(String name) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (name == null || name.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!name.contains("_")) {
			// 不含下划线，仅将首字母小写
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		// 用下划线将原始字符串分割
		String camels[] = name.split("_");
		for (String camel : camels) {
			// 跳过原始字符串中开头、结尾的下换线或双重下划线
			if (camel.isEmpty()) {
				continue;
			}
			// 处理真正的驼峰片段
			if (result.length() == 0) {
				// 第一个驼峰片段，全部字母都小写
				result.append(camel.toLowerCase());
			} else {
				// 其他的驼峰片段，首字母大写
				result.append(camel.substring(0, 1).toUpperCase());
				result.append(camel.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}
}
