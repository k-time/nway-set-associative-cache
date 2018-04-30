package com.ktime.cache;

public interface Cache<K, V> {
    void put(K key, V val);
    V get(K key);
    void evictAll();
    int size();
}
