package com.ktime.cache;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Package-private. Class should be hidden from client.
 */
class CacheSet {
    private int maxSize;
    private ReplacementPolicy policy;
    private LinkedList<CacheBlock> blockList; // List starts with MRU block; ends with LRU block

    CacheSet(int maxSize, ReplacementPolicy policy) {
        this.maxSize = maxSize;
        this.policy = policy;
        this.blockList = new LinkedList<>();
    }

    void put(CacheBlock cacheBlock) {
        CacheBlock removedBlock = removeBlockIfExists(cacheBlock.getKeyHash());
        if (removedBlock != null || !isFull()) {
            blockList.addFirst(cacheBlock);
        }
        else {
            policy.replace(cacheBlock, blockList);
            if (blockList.size() > maxSize) {
                //throw new Exception("Invalid replacement policy");
            }
        }
    }


    Object get(int keyHash) {
        CacheBlock removedBlock = removeBlockIfExists(keyHash);
        if (removedBlock != null) {
            blockList.addFirst(removedBlock);
            return removedBlock.getValue();
        }
        return null;
    }

    private CacheBlock removeBlockIfExists(int keyHash) {
        // Use a list iterator so you don't have to call linkedlist find. O(n) to find, O(1) to remove
        ListIterator<CacheBlock> listIter = blockList.listIterator(0);
        while (listIter.hasNext()) {
            CacheBlock curBlock = listIter.next();
            if (curBlock.getKeyHash() == keyHash) {
                listIter.remove();
                return curBlock;
            }
        }
        return null;
    }

    int getSize() {
        return blockList.size();
    }

    boolean isEmpty() {
        return blockList.isEmpty();
    }

    boolean isFull() {
        return getSize() == maxSize;
    }
}
