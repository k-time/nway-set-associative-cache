package com.ktime.cache;

public class MRUStorage<K, V> extends LinkedMapStorage<K, V> {
    @Override
    public void replace(CacheBlock<K, V> block) {
        blockMap.remove(blockMap.lastKey());
        this.add(block);
    }

    @Override
    public CacheSetStorage<K, V> createNewInstance() {
        return new MRUStorage<>();
    }
}