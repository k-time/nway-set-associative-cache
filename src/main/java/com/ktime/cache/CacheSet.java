package com.ktime.cache;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Package-private: class does not need to be visible to client.
 */
class CacheSet {
    private int maxSize;
    private ReplacementPolicy policy;
    // LRU block is first inserted, LRU block is last inserted. Maps block hash to block object.
    private LinkedMap<Integer, CacheBlock> blockMap;

    CacheSet(int maxSize, ReplacementPolicy policy) {
        this.maxSize = maxSize;
        this.policy = policy;
        this.blockMap = new LinkedMap<>();
    }

    void put(CacheBlock cacheBlock) {
        CacheBlock oldBlock = removeBlockIfExists(cacheBlock.getKeyHash());
        if (oldBlock != null) {
            // Update the old block and add it back
            oldBlock.updateValue(cacheBlock);
            oldBlock.use();
            blockMap.put(oldBlock.getKeyHash(), oldBlock);
        }
        else {
            if (!isFull()) {
                blockMap.put(cacheBlock.getKeyHash(), cacheBlock);
            }
            else {
                policy.replace(cacheBlock, blockMap);
                // Need to ensure that an alternative policy doesn't overfill the CacheSet,
                // because client may implement policy incorrectly.
                while (blockMap.size() > maxSize) {
                    blockMap.remove(blockMap.firstKey());
                }
            }
        }
    }

    Object get(int keyHash) {
        CacheBlock removedBlock = removeBlockIfExists(keyHash);
        if (removedBlock != null) {
            removedBlock.use();
            blockMap.put(removedBlock.getKeyHash(), removedBlock);
            return removedBlock.getValue();
        }
        return null;
    }

    void evictAll() {
        blockMap.clear();
    }

    int size() {
        return blockMap.size();
    }

    boolean isFull() {
        return size() == maxSize;
    }

    List<CacheBlock> getBlocks() {
        return new ArrayList<>(blockMap.values());
    }

    private CacheBlock removeBlockIfExists(Integer keyHash) {
        return blockMap.remove(keyHash);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CacheBlock block : blockMap.values()) {
            sb.append(block.getValue());
            sb.append("\t");
        }
        return sb.toString().trim();
    }
}
