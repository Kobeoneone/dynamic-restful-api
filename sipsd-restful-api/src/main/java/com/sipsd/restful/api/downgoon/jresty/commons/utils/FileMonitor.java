package com.sipsd.restful.api.downgoon.jresty.commons.utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


class FileMonitor {
	
	private Timer timer = new Timer("DynamicPropertiesTimer");
	
	public synchronized void addDetected(final DynamicProperties detected) {
		if(detected.getDelay() < 0) ;//if (delay < 0)  delay = 0L;
		if(detected.getPeriod() <= 0) return;
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				long t = detected.getLastModified();
				if(t > detected.getLastMonitorTime()) {
					detected.setLastMonitorTime(t);
					try {
						detected.update();
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			
		}, detected.getDelay(), detected.getPeriod());
		
	}
	
}

