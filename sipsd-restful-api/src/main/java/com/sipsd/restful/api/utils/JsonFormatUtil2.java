package com.sipsd.restful.api.utils;

import com.sipsd.restful.api.exception.BaseException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON格式化工具
 * 
 * @author StarlightFu
 *
 */
public class JsonFormatUtil2 {
	private static final String OBJECT_BRACE_REG = "([\\{\\}])";
	private static final String ARRAY_BRACE_REG = "([\\[\\]])";
	private static final String PROPERTY_NAME_REG = "(\\\".*\\\")(\\:)(.*)(\\,)?";
	private static final String STRING_REG = "\\\"([^\"]*)\\\"(\\,)?$";
	private static final String NUMBER_REG = "(-?\\d+)(\\,)?$";

	/** JSON着色样式 */
	public static final String JSON_STYLE = "<style>pre{font-family:\"微软雅黑\";font-size:10px;}.ObjectBrace{color:#00AA00;font-weight:bold;}.ArrayBrace{color:#0033FF;font-weight:bold;}"
			+ ".PropertyName{color:#CC0000;font-weight:bold;}.String{color:#007777;}.Number{color:#AA00AA;}.Boolean{color:#0000FF;}"
			+ ".Function{color:#AA6633;text-decoration:italic;}.Null{color:#0000FF;}.Comma{color:#000000;font-weight:bold;</style>";

	/**
	 * 校验JSON格式是否正确
	 * 
	 * @param json
	 *            JSON字符串
	 * @return true:格式正确;false:格式错误
	 */
	public static boolean validation(String json) {
		boolean result = false;
		FileReader reader = null;
		try {
			String valJson = "function valJson(jsonStr){var result=false;try{eval('('+jsonStr+')');result=true;}catch(e){result=false;}return result;}";
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			engine.eval(valJson);

			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				result = (Boolean) invoke.invokeFunction("valJson", json);
			}
		} catch (ScriptException e) {
			throw new BaseException("执行JSON校验JS异常");
		} catch (NoSuchMethodException e) {
			throw new BaseException("执行JSON校验JS异常");
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 格式化JSON字符串
	 * 
	 * @param jsonStr
	 * @return
	 * @throws BaseException
	 */
	public static String format(String jsonStr) throws BaseException {
		boolean valResult = false;
		valResult = JsonFormatUtil2.validation(jsonStr);
		if (!valResult) {
			throw new BaseException("JSON格式错误");
		}
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		boolean inString = false;
		char inStringBegin = '\0';
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			if (inString && current == inStringBegin) {
				// 判断前一个字符是否为 \
				if (last != '\\') {
					inString = false;
					inStringBegin = '\0';
				}
				sb.append(current);
			} else if (!inString && (current == '"' || current == '\'')) {
				inString = true;
				inStringBegin = current;
				sb.append(current);
			} else if (!inString && (current == ' ' || current == '\t' || current == '\n')) {
				current = '\0';
			} else if (!inString && current == ':') {
				sb.append(current).append(" ");
			} else if (!inString && current == ',') {
				sb.append(current).append('\n').append(indentBlank(indent));
			} else if (!inString && (current == '[' || current == '{')) {
				indent++;
				sb.append(current).append('\n').append(indentBlank(indent));
			} else if (!inString && (current == ']' || current == '}')) {
				indent--;
				sb.append('\n').append(indentBlank(indent)).append(current);
			} else {
				sb.append(current);
			}
		}
		return sb.toString();
	}

	/**
	 * 添加缩进
	 * 
	 * @param indent
	 * @return
	 */
	private static String indentBlank(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("      ");
		}
		return sb.toString();
	}

	/**
	 * JSON语法着色
	 * 
	 * @param formatJsonStr
	 *            格式化后的JSON字符串
	 * @return
	 */
	public static String coloring(String formatJsonStr) {
		StringBuilder sb = new StringBuilder();
		String[] nodes = formatJsonStr.split("\n");
		for (int i = 0; i < nodes.length; i++) {
			String node = nodes[i];

			Pattern r = Pattern.compile(JsonFormatUtil2.OBJECT_BRACE_REG);
			Matcher m = r.matcher(node);
			node = m.replaceAll("<span class='ObjectBrace'>$1</span>");

			r = Pattern.compile(JsonFormatUtil2.ARRAY_BRACE_REG);
			m = r.matcher(node);
			node = m.replaceAll("<span class='ArrayBrace'>$1</span>");

			r = Pattern.compile(JsonFormatUtil2.PROPERTY_NAME_REG);
			m = r.matcher(node);
			node = m.replaceAll("<span class='PropertyName'>$1</span>$2$3$4");

			r = Pattern.compile(JsonFormatUtil2.STRING_REG);
			m = r.matcher(node);
			node = m.replaceAll("<span class='String'>\"$1\"</span><span class='Comma'>$2</span>");

			r = Pattern.compile(JsonFormatUtil2.NUMBER_REG);
			m = r.matcher(node);
			node = m.replaceAll("<span class='Number'>$1</span><span class='Comma'>$2</span>");

			sb.append(node);
			if (i < nodes.length - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 格式化JSON并着色
	 * 
	 * @param json
	 * @return
	 */
	public static String formatAndColoring(String json) {
		return JsonFormatUtil2.coloring(JsonFormatUtil2.format(json));
	}

	/**
	 * 删除空格
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static String removeSpace(String jsonStr) {
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		char inStringBegin = '\0';
		boolean inString = false;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			if (inString && current == inStringBegin) {
				// 判断前一个字符是否为 \
				if (last != '\\') {
					inString = false;
					inStringBegin = '\0';
				}
				sb.append(current);
			} else if (!inString && (current == '"' || current == '\'')) {
				inString = true;
				inStringBegin = current;
				sb.append(current);
			} else if (!inString && (current == ' ' || current == '\t' || current == '\n')) {
				current = '\0';
			} else {
				sb.append(current);
			}
		}
		return sb.toString();
	}

	/**
	 * 删除空格并转义
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static String removeSpaceAndEscape(String jsonStr) {
		jsonStr = JsonFormatUtil2.removeSpace(jsonStr);
		return jsonStr.replaceAll("\"", "\\\\\"");
	}

	/**
	 * 去除转义
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static String removeEscape(String jsonStr) {
		return jsonStr.replaceAll("\\\\\"", "\"");
	}

//	public static void main(String[] args) {
//		String jsonStr = "{\"strValue\":\"here is str value\",\"nullValue\":null,\"intvalue\":999,\"doublevalue\":999,\"booleanValue\":true,\"array\":[\"a1\",\"a2\",true,2,33.3,null,{\"innerStr\":\"here is a inner str\",\"innerInteger\":123456789},[\"Hi, found me ?\"]],\"innerOBJ\":{\"innerStr\":\"here is a inner str\",\"innerInteger\":123456789}}";
//		System.out.println(JsonFormatUtil2.format(jsonStr));
//		System.out.println(JsonFormatUtil2.coloring(JsonFormatUtil2.format(jsonStr)));
//	}
}