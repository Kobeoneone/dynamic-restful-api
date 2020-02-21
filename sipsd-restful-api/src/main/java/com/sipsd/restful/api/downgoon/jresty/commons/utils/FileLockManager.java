package com.sipsd.restful.api.downgoon.jresty.commons.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/** REFER: http://blogs.oracle.com/sin/entry/using_a_filechannel_to_force */
public class FileLockManager {

	// A map between the filenames and lock files.
	private static Map<String, FileLock> exclusiveLock = new HashMap<String, FileLock>();

	// Lock to protect the threadsafe access.
	private static ReentrantLock lock = new ReentrantLock();

	public static boolean acquireExclusiveLock(String fileName) {
		lock.lock();
		RandomAccessFile raf = null;
		FileChannel channel = null;
		try {
			if (exclusiveLock.containsKey(fileName)) {
				return false;
			}
			// No lock found. create it.
			File f = new File(fileName);
			if (f.exists()) {
				f.createNewFile();
			}

			// Open the file and get the channel.
			raf = new RandomAccessFile(fileName, "rw");
			channel = raf.getChannel();

			// Try to obtain an exclusive lock over FileChannel.
			FileLock fileLock = channel.tryLock();
			if (fileLock == null) {
				// Couldn't get the lock. Throw the exception to get the
				// resource freed.
				throw new Exception("File Lock Occupied: " + fileName);
			}
			// Put it in the Map.
			exclusiveLock.put(fileName, fileLock);
			return true;

			// Bad to catch all but okay to keep this code short.

		} catch (Exception e) {
			try {
				if (channel != null) {
					channel.close();
				}
				if (raf != null) {
					raf.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;

		} finally {
			lock.unlock();
		}

	}

	public static boolean releaseLock(String lockFile) {
		lock.lock();
		try {

			FileLock lock = exclusiveLock.remove(lockFile);
			if (lock != null) {
				lock.release();
			}
			lock.channel().close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			lock.unlock();
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		String fileName = System.getProperty("user.dir") + File.pathSeparator + "tmp.lock";
		if (!FileLockManager.acquireExclusiveLock(fileName)) {
			System.out.println("Application Instance Found on this host");
			return;// REFER: 可用jps命令求解出来
					// http://houzhengqing.blog.163.com/blog/static/22754987200911166615324/
		}
		System.out.println("Application Run ..." + System.currentTimeMillis());
		Thread.sleep(1000L * 60 * 60);
		System.out.println("Application END");
	}
}
