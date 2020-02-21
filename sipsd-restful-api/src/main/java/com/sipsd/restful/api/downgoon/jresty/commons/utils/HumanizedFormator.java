package com.sipsd.restful.api.downgoon.jresty.commons.utils;

import java.text.NumberFormat;

/**
 * 数字，磁盘容量，时间 格式化显示
 */
public class HumanizedFormator {

	public static String numberFormat(double num) {
		return numberFormat((long) num);
	}

	public static String numberFormat(long num) {
		return NumberFormat.getInstance().format(num);
	}

	public static String percentFormat(double percent) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(percent);
	}

	/**
	 * 容量（比如磁盘容量）人性化显示
	 * 
	 * @param bytes
	 *            字节数（比如：磁盘容量）
	 */
	public static String bytes(long bytes) {
		return humanized(bytes, new long[] { 1024L * 1024L * 1024L, 1024L * 1024L, 1024L, 1L },
				new String[] { "G", "M", "K", "B" });
	}

	/**
	 * 时间 人性化显示
	 * 
	 * @param seconds
	 *            时间“秒”数
	 */
	public static String seconds(long seconds) {
		return humanized(seconds, new long[] { 60L * 60L * 24, 60L * 60L, 60L, 1L },
				new String[] { "days", "hours", "min", "sec" });
	}

	/**
	 * @param value
	 *            待人性化显示的数值
	 * @param unitRadix
	 *            每个单位对应的数值基数。比如磁盘容量领域，“MB”这个单位对应的“单位基数”是 1024 * 1024
	 *            “B”。元素数值必须从大到小有序。
	 * @param unitName
	 *            每个单位名称。比如磁盘容量领域，通常有“MB, KB”。
	 * @return 人性化显示
	 * 
	 * 比如磁盘容量人性化显示时：
	 * 
	 * humanized(bytes, new long[] { 1024L * 1024L * 1024L, 1024L * 1024L, 1024L, 1L },
 	 *			new String[] { "G ", "M ", "K ", "B " }
 	 *
 	 * 时间人性化显示时：
 	 * 
 	 * humanized(seconds, new long[] { 60L * 60L * 24, 60L * 60L, 60L, 1L },
	 *			new String[] { "days ", "hours ", "min ", "sec " })
	 */
	public static String humanized(long value, long[] unitRadix, String[] unitName) {
		if (unitRadix == null || unitName == null || unitRadix.length != unitName.length) {
			throw new IllegalArgumentException("unit-radix size not equal to unit-name size");
		}
		StringBuffer human = new StringBuffer();
		long remainder = value;
		if (remainder <= 0) {
			human.append(0).append(unitName[unitName.length - 1]);
		}
		boolean highLevelOK = false;// 已确定最高位单位
		int i = 0;
		while (remainder > 0 && i < unitRadix.length) {
			if (unitRadix[i] <= 0) {
				throw new IllegalArgumentException("unit-radix[i] should be greater than zero: i=" + i);
			}
			long quotient = remainder / unitRadix[i]; // 商，整数部分
			if (!highLevelOK) {
				if (quotient > 0) {
					highLevelOK = true;
					human.append(quotient).append(unitName[i]);
				}

			} else {
				if (remainder > 0) {
					human.append(quotient).append(unitName[i]);
				} else {
					break;
				}
			}

			remainder = remainder % unitRadix[i]; // 余数部分
			i++;
		}
		return human.toString();
	}

}
