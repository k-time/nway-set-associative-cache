package com.ktime.cache;

import java.util.Collection;

public interface CacheSetStorage<K, V> {
    void add(CacheBlock<K,V> block);
    CacheBlock<K, V> remove(K key);
    void replace(CacheBlock<K, V> block);
    void evictAll();
    int size();
    Collection<CacheBlock<K, V>> getBlocks();
    CacheSetStorage<K, V> createNewInstance();
}
