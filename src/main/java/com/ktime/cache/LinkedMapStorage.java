package com.ktime.cache;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Collection;

public abstract class LinkedMapStorage<K, V> implements CacheSetStorage<K, V> {
    protected LinkedMap<K, CacheBlock<K, V>> blockMap;

    public LinkedMapStorage() {
        blockMap = new LinkedMap<>();
    }

    @Override
    public void add(CacheBlock<K, V> block) {
        blockMap.put(block.getKey(), block);
    }

    @Override
    public CacheBlock<K, V> remove(K key) {
        return blockMap.remove(key);
    }

    @Override
    public void evictAll() {
        blockMap.clear();
    }

    @Override
    public int size() {
        return blockMap.size();
    }

    @Override
    public Collection<CacheBlock<K, V>> getBlocks() {
        return blockMap.values();
    }

    @Override
    public abstract void replace(CacheBlock<K, V> block);

    @Override
    public abstract CacheSetStorage<K, V> createNewInstance();
}
