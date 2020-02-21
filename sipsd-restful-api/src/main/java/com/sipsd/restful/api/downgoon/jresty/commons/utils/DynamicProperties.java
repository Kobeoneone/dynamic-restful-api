package com.sipsd.restful.api.downgoon.jresty.commons.utils;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**	动态配置文件
 * 用JProfiler观察可以看出只使用了一个监视线程
 *		String fn1 = "config/test111.properties";
 *		String fn2 = "config/test222.properties";
 *		DynamicProperties dp1 = new DynamicProperties(fn1,0,20);//如果第三参数20，修改成0，则成为静态配置文件.
 *		DynamicProperties dp2 = new DynamicProperties(fn2,0,20);//如果第三参数20，修改成0，则成为静态配置文件.
 *		while(true) {
 *			System.err.println("#1="+dp1.getProperty("key"));
 *			System.err.println("#2="+dp2.getProperty("key"));
 *			Thread.sleep(1000*5);
 *		}
 *
 * */
public final class DynamicProperties {

	static Logger log =  LoggerFactory.getLogger(DynamicProperties.class);

	/** 动态配置文件磁盘数据*/
	protected File file;

	/** 动态配置文件内存数据（ {@link DynamicProperties} 的目的就是保持从磁盘数据到内存数据的单向一致性，
	 *  暂不支持从内存到磁盘的数据同步，因此{@link DynamicProperties}不提供set方法，只提供get方法.*/
	protected Properties property;

	//TODO 临时
	public Properties properties() {
		return property;
	}


	/** 动态检测相关参数：第一次检测前的延迟时间，单位ms*/
	protected long delay;

	/** 动态检测相关参数：检测周期，单位ms*/
	protected long period;

	/** 动态检测相关参数：{@link FileMonitor} 最后检测时间，单位ms*/
	protected long lastMonitorTime;

	protected String charset = null;


	public String getFileName() {
		if(file==null) {
			return "System.getProperties()";
		}
		return file.getName();
	}

	long getLastModified() {
		return this.file.lastModified();
	}

	public long getDelay() {
		return delay;
	}

	public long getPeriod() {
		return period;
	}

	public String getCharset() {
		if(charset==null) {
			return "ISO 8859-1";
		}
		return charset;
	}

	long getLastMonitorTime() {
		return this.lastMonitorTime;
	}

	void setLastMonitorTime(long lastMonitorTime) {
		this.lastMonitorTime = lastMonitorTime;
	}



	@Override
	public String toString() {
		return this.property.toString();
	}



	/**
	 * 所有 {@link DynamicProperties} 实例共享一个 {@link FileMonitor}
	 * */
	private static FileMonitor monitor = null;

	private synchronized static void initFileMonitor() {
		if(monitor == null) {
			monitor = new FileMonitor();
		}
	}

	public DynamicProperties(Properties prop) {//适配的作用
		this.property = prop;
		this.delay = 0;
		this.period = 0;
	}

	/**
	 * @param	file	属性文件
	 * @param	delay	从<code>DynamicProperties</code>被创建到第一次动态监视的时间间隔.　约束范围delay > 0
	 * @param	period	动态监视的时间间隔.	 约束范围period >= 0；等于0表示不执行动态监视，退化为静态配置文件．
	 *
	 * */
	public DynamicProperties(File file,long delay,long period)
			throws IOException
	{
		doConstruct(file,delay,period,null);
	}

	private void doConstruct(File file,long delay,long period,String charset)
			throws IOException
	{
		if(delay < 0 || period < 0) {
			throw new IllegalArgumentException("参数delay和period都必须大于等于0");
		}
		this.file = file;
		this.delay = delay;
		this.period = period;
		this.charset = charset;
		this.property = new Properties();
		this.initAndLoad();//初始构造时，执行第一次加载.
	}

	public DynamicProperties(String fileName,long delay,long period)
			throws IOException
	{
		this(new File(fileName),delay,period);
	}

	public DynamicProperties(String fileName,long delay,long period,String name)
			throws IOException
	{
		this(fileName,delay,period,name, null);
	}

	/** REFER:  http://stackoverflow.com/questions/863838/problem-with-java-properties-utf8-encoding-in-eclipse  */
	public DynamicProperties(String fileName,long delay,long period,String name, String charset)
			throws IOException
	{
		if(charset!=null && charset.trim().equals("")) {
			charset = null;
		}
		File resourceFile = null;
		if(fileName!=null && fileName.startsWith("classpath:")) {
			try {
				// 这种方式在web容器中加载不了
//				URL url = ClassLoader.getSystemResource(fileName.substring("classpath:".length()));
				String absoluteClasspathLocation = fileName.substring("classpath:".length());
				if(!absoluteClasspathLocation.startsWith("/")) { // 强制使用classpath的绝对路径
					absoluteClasspathLocation = "/"+absoluteClasspathLocation;
				}
				URL url = DynamicProperties.class.getResource(absoluteClasspathLocation);
				resourceFile = new File(url.toURI());
			} catch (Exception e) {
				log.error("资源"+fileName+"未找到，请核实classpath路径，绝对路径以\"/\"开头。");
				throw new IOException(fileName+"搜索失败",e);
			}
		} else {
			resourceFile = new File(fileName);
		}
		doConstruct(resourceFile,delay,period,charset);
		if(name==null || name.equals("") || name.equals("default")) {//默认实例
			if(getInstance(DEFAULT) == null && getInstance("default")==null) {
				initInstance(DEFAULT,this);
			} else {
				throw new IOException("default instance existed");
			}
		} else {//其他命名实例
			initInstance(name,this);
		}
	}

	public DynamicProperties(File file,Date firstTime,long period)
			throws IOException
	{
		this(file,firstTime.getTime()-System.currentTimeMillis(),period);
	}

	public DynamicProperties(String fileName,Date firstTime,long period)
			throws IOException
	{
		this(new File(fileName),firstTime.getTime()-System.currentTimeMillis(),period);
	}

	public  DynamicProperties(File file,long period)
			throws IOException
	{
		this(file,0,period);
	}

	public DynamicProperties(String fileName,long period)
			throws IOException
	{
		this(new File(fileName),period);
	}


	private void initAndLoad() throws IOException {
		this.lastMonitorTime = System.currentTimeMillis();
		update();//首次将配置信息从文件加载到内存
		if(period > 0) {//如果period=0，则表示静态配置文件，不需要进行动态更新的监视
			initFileMonitor();
			monitor.addDetected(this);//启动FileMonitor，以监测磁盘文件内容的变化，并在变化时，由监视线程回调update()方法，进行重新加载
		}
	}

	/**
	 * {@link FileMonitor} 线程回调 {@link #update()} 方法，一定会在{@link #initAndLoad()}之后，
	 * 所以尽管 {@link #update()}方法会被两个线程执行，一个是构造 {@link DynamicProperties}实例所在的线程，
	 * 另一个是 {@link FileMonitor}线程，但是它们是顺序执行的，实例构造完成后，只有 {@link FileMonitor}
	 * 线程执行 {@link #update()}方法。因此 {@link #update()}不同担心线程安全的问题.
	 * */
	void update() throws IOException {
		if(charset==null) {
			InputStream in = new FileInputStream(file);
			this.property.load(in);
		} else {
			this.property.load(new InputStreamReader(new FileInputStream(file), charset));
		}

		if(this.property.size() <= 500 ) {
			log.info("dynamic properties from "+this.file.getAbsolutePath()+" are: "+this.toString());
		} else {
			log.info("dynamic properties from "+this.file.getAbsolutePath()+" are too large size: "+this.property.size()+" items");
		}
	}


	public String getProperty(String key, String defaultValue) {
		String val = this.property.getProperty(key);
		return val == null ? defaultValue : val.trim();
	}

	public String getProperty(String key) {
		String val = this.property.getProperty(key);
		return val == null ? null : val.trim();
	}

	/** 只要不是NULL，就返回TRUE；如果空字符串，返回TRUE */
	public boolean has(String key) {
		String val = this.property.getProperty(key);
		return val != null;
	}

	public boolean getBoolean(String key) {
		String val = this.getProperty(key);
		return Boolean.parseBoolean(val);
	}

	public boolean getBoolean(String key,boolean defaultValue) {
		String val = this.getProperty(key);
		return val == null ? defaultValue : Boolean.parseBoolean(val);
	}

	public int getInt(String key) {
		String val = this.getProperty(key);
		return Integer.parseInt(val);
	}

	public int getInt(String key,int defaultValue) {
		String val = this.getProperty(key);
		return val == null ? defaultValue : Integer.parseInt(val);
	}

	public Byte getByte(String key) {
		String val = this.getProperty(key);
		if(val == null) {
			return null;
		}
		try {
			return Byte.parseByte(val);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public double getDouble(String key) {
		String val = this.getProperty(key);
		return Double.parseDouble(val);
	}

	public double getDouble(String key,double defaultValue) {
		String val = this.getProperty(key);
		return val == null ? defaultValue : Double.parseDouble(val);
	}



	/* 为了方便使用，避免对象实例在各个类中传递，提供些静态方法*/
	public static final void initInstance(String instanceName, DynamicProperties instance) {
		if(instanceName == null || instance == null) {
			throw new IllegalArgumentException("参数不能为空");
		}
		synchronized (context) {
			if(context.containsKey(instanceName)) {
				throw new IllegalStateException("名称为"+instanceName+"的实例已经存在");
			}
			context.put(instanceName, instance);
		}
	}

	public static final DynamicProperties getInstance(String instanceName) {
		synchronized (context) {
			return context.get(instanceName);
		}
	}

	private static final String DEFAULT = DynamicProperties.class.getName()+"#DEFAULT";

	public static final void initDefaultInstance(String fileName,long delay,long period)
	{
		try {
			initInstance(DEFAULT, new DynamicProperties(fileName,delay,period));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private DynamicProperties() {
		this.property = System.getProperties();
	}

	public static final DynamicProperties getDefaultInstance() {
		DynamicProperties dp = getInstance(DEFAULT);
		if(dp == null) {
			//throw new IllegalStateException("默认实例尚未初始化，请用initDefaultInstance方法进行初始化");

			log.warn("警告：默认实例尚未初始化，请用initDefaultInstance方法进行初始化。否则，程序将访问System.getProperties()的数值。");
			dp = new DynamicProperties();//以支持不需要配置文件的默认值的情况
			initInstance(DEFAULT, dp);
			return dp;
		}
		return dp;
	}

	private static Map<String, DynamicProperties> context = new HashMap<String, DynamicProperties>();//Collections.synchronizedMap(new HashMap());

	/** 小工具：将应用程序的命令行参数转化成 {@link Properties}*/
	public static Properties parseArgs(String[] args) {
		Properties config = new Properties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		for (int i = 0; i < args.length; i++) {
			writer.println(args[i]);
		}
		writer.flush();
		try {
			config.load(new ByteArrayInputStream(baos.toByteArray()));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return config;
	}

	public static String parseRefProperty(String ref) {
		if(ref==null) {
			return null;
		}
		if(ref.startsWith("dynamic/")) {//支持对另一个配置文件的引用
			String[] refItems = ref.split("/");//不可能为空
			if(refItems.length > 2) {
				ref = DynamicProperties.getInstance(refItems[1]).getProperty(refItems[2]);
			} else {
				ref = DynamicProperties.getDefaultInstance().getProperty(refItems[1]);
			}

			if(ref==null) {
				return null;
			}
		}
		return ref;
	}

	public static void main(String[] args) throws Exception {
		String fileName = "classpath:aidcat.properties";
		long delay = 0L;
		long period = 0L;
		String name = "aidcat";
		new DynamicProperties(fileName, delay, period, name);
		DynamicProperties dp = DynamicProperties.getInstance(name);
		System.out.println(dp);
	}
}




