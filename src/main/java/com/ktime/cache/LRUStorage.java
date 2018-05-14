package com.ktime.cache;

public class LRUStorage<K, V> extends LinkedMapStorage<K, V> {
    @Override
    public void replace(CacheBlock<K, V> block) {
        blockMap.remove(blockMap.firstKey());
        this.add(block);
    }

    @Override
    public CacheSetStorage<K, V> createNewInstance() {
        return new LRUStorage<>();
    }
}