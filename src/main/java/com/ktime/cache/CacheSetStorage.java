package com.ktime.cache;

import java.util.Collection;

public interface CacheSetStorage<K, V> {
    void add(CacheBlock<K, V> block);
    CacheBlock<K, V> remove(K key);
    void replace(CacheBlock<K, V> block);
    CacheBlock<K, V> get(K key);
    void evictAll();
    int size();
    Collection<CacheBlock<K, V>> getBlocks();
    CacheSetStorage<K, V> createNewInstance();
    default void onUsage(CacheBlock<K, V> block) {
        // Actions to be taken after a block is used; ex. move block to front or back.
        // Default action is to do nothing.
    }
}
