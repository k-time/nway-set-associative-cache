package com.ktime.cache;

public interface Cache<K, V> {
    public void put(K key, V val);
    public V get(K key);
}
