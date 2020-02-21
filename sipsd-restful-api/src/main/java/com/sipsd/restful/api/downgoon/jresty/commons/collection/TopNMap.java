package com.sipsd.restful.api.downgoon.jresty.commons.collection;

import java.util.*;

public class TopNMap<K,V> implements SortedMap<K,V> {

	private SortedMap<K,V> delegate;
	
	private int maxSize;
	private boolean ascending;
	
	public TopNMap(int maxSize, boolean ascending,SortedMap<K,V> sortedMap) {
		super();
		this.maxSize = maxSize;
		this.ascending = ascending;
		this.delegate = sortedMap;
	}
	
	public TopNMap(int maxSize, boolean ascending) {
		super();
		this.maxSize = maxSize;
		this.ascending = ascending;
		this.delegate = new TreeMap<K, V>();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public boolean isAscending() {
		return ascending;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public V get(Object key) {
		return delegate.get(key);
	}

	@Override
	public V put(K key, V value) {
		V v0 = delegate.put(key, value);
		if(delegate.size() > maxSize) {
			if(ascending) {
				delegate.remove(delegate.lastKey());
			} else {
				delegate.remove(delegate.firstKey());
			}
		}
		return v0;
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(key);
	}
	
	
	
	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		delegate.putAll(m);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Comparator<? super K> comparator() {
		return delegate.comparator();
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return delegate.subMap(fromKey, toKey);
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		return delegate.headMap(toKey);
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		return delegate.tailMap(fromKey);
	}

	@Override
	public K firstKey() {
		return delegate.firstKey();
	}

	@Override
	public K lastKey() {
		return delegate.lastKey();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	public static void main(String[] args) {
		TopNMap<Integer, String> maxN = new TopNMap<Integer, String>(3, false);
		maxN.put(45, "fajjf");
		maxN.put(100, "f100f");
		maxN.put(12, "120f");
		maxN.put(6789, "99f");
		maxN.put(6789, "99f");
		System.out.println(maxN);
	}
}
