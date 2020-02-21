package com.sipsd.restful.api.downgoon.jresty.commons.collection;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TopNQueue<E> extends PriorityQueue<E> {

	private static final long serialVersionUID = -6541245268210747229L;

	private int maxSize;
	
	public TopNQueue(int maxSize) {
		super();
		if(maxSize < 1) {
			throw new IllegalArgumentException("queue max size must >= 1");
		}
		this.maxSize = maxSize;
	}
	
	public TopNQueue(int maxSize, Comparator<? super E> comparator) {
		super(11,comparator);
		if(maxSize < 1) {
			throw new IllegalArgumentException("queue max size must >= 1");
		}
		this.maxSize = maxSize;
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public boolean add(E e) {
		boolean r = super.add(e);
		if(size() > maxSize) {
			poll();//出队
		}
		return r;
	}
	
	
	public static void main(String[] args) throws Exception { 
//		TopNQueue<Integer> q = new TopNQueue<Integer>(2);
//		q.add(45);
//		System.out.println(q);
//		q.add(56);
//		System.out.println(q);
//		q.add(599);
//		System.out.println(q);
//		q.add(1);
//		System.out.println(q);//小的被寄出去
		
		
	}
	
}
