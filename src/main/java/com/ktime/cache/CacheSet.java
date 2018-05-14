package com.ktime.cache;

import java.util.Collection;

/**
 * Package-private: class does not need to be visible to client.
 */
class CacheSet<K, V> {
    private int maxSize;
    private CacheSetStorage<K, V> storage;

    CacheSet(int maxSize, CacheSetStorage<K, V> storage) {
        this.maxSize = maxSize;
        this.storage = storage;
    }

    void put(CacheBlock<K, V> cacheBlock) {
        CacheBlock<K, V> oldBlock = removeBlockIfExists(cacheBlock.getKey());
        if (oldBlock != null) {
            // Update the old block and add it back
            oldBlock.updateValue(cacheBlock);
            oldBlock.use();
            storage.add(oldBlock);
        }
        else {
            if (!isFull()) {
                storage.add(cacheBlock);
            }
            else {
                storage.replace(cacheBlock);
                /* Need to ensure that an alternative policy doesn't overfill the CacheSet,
                   because client may implement policy incorrectly. Could throw an exception instead.
                   Might be overkill to evict all, but this way client doesn't have to implement
                   an extra removeAny() method. */
                if (storage.size() > maxSize) {
                    storage.evictAll();
                }
            }
        }
    }

    V get(K key) {
        CacheBlock<K, V> removedBlock = removeBlockIfExists(key);
        if (removedBlock != null) {
            removedBlock.use();
            // Move the block up
            storage.add(removedBlock);
            return removedBlock.getValue();
        }
        return null;
    }

    void evictAll() {
        storage.evictAll();
    }

    int size() {
        return storage.size();
    }

    boolean isFull() {
        return size() == maxSize;
    }

    Collection<CacheBlock<K, V>> getBlocks() {
        return storage.getBlocks();
    }

    private CacheBlock<K, V> removeBlockIfExists(K key) {
        return storage.remove(key);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CacheBlock block : getBlocks()) {
            sb.append(block.getValue());
            sb.append("\t");
        }
        return sb.toString().trim();
    }
}
