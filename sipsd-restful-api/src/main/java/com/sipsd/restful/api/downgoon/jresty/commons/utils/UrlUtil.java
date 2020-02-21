package com.sipsd.restful.api.downgoon.jresty.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class UrlUtil {

	
	public final static boolean isWhiteURL(final String url, final String[] whiteDomains) {
		if(whiteDomains==null || whiteDomains.length <= 0) {//表示没有允许的白名单
			return false;
		}
		String hostName = getHostName(url);
		if(hostName==null || hostName.trim().equals("")) {
			return false;
		}
		for (String rootDomain : whiteDomains) {
			if(isSubDomain(hostName,  rootDomain)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断 targetDomain 是否是 rootDomain 的子域（包括相同）
	 * */
	public final static boolean isSubDomain(String targetDomain, String rootDomain) {
		if(targetDomain==null || targetDomain.equals("") || rootDomain==null || rootDomain.equals("")) {
			return false;
		}
		targetDomain = targetDomain.toLowerCase();
		rootDomain = rootDomain.toLowerCase();
		int foundIdx = targetDomain.indexOf(rootDomain);
		if(foundIdx!=-1 && (foundIdx==0 ||  targetDomain.charAt(foundIdx-1)=='.') && targetDomain.length()==foundIdx+rootDomain.length()) {
			return true;
		}
		return false;
	} 
	
	
	public final static String getHostName(final String url) {
		URL urlObj = null;
		try {
			urlObj = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		return (urlObj==null ? null:urlObj.getHost());
	}
	
	public final static String appendQS(final String urlAddr, Map<String,String> params ) {
		String url = urlAddr;
		if(params!=null && params.size() > 0) {
			Iterator<Entry<String, String>> entries = params.entrySet().iterator();
			while(entries.hasNext()) {
				Entry<String, String> e = entries.next();
				url = appendQS(url, e.getKey(), e.getValue());
			}
		}
		return url;
	}
	
	public final static String appendQS(String urlAddr,String param,String value) {
		return appendQueryString(urlAddr, param, value);
	}
	
	/**
	 * 给已有的 urlAddr增加输入参数
	 * */
	public final static String appendQueryString(String urlAddr,String param,String value) {
		String r = urlAddr;
		if(urlAddr.endsWith("/")) {
			r = urlAddr.substring(0, urlAddr.length()-"/".length()) + "?" + param+"="+urlencodeUTF8(value);
		} else if(urlAddr.indexOf("?")!=-1) {
			r = urlAddr + "&" + param + "=" + urlencodeUTF8(value);
		} else {
			r = urlAddr + "?" + param + "=" + urlencodeUTF8(value);
		}
		return r;
	}
	
	private static String urlencodeUTF8(String value) {
		if(value==null || value.equals("")) {
			return "null";
		}
		try {
			return URLEncoder.encode(value,"utf-8");
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	public final static String getNow() {
		return sdf.format(new Date());
	}

}
