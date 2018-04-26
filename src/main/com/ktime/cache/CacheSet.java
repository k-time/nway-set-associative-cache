package com.ktime.cache;

import java.util.LinkedList;
import java.util.ListIterator;

public class CacheSet {
    private int maxSize;
    private ReplacementPolicy policy;
    private LinkedList<CacheBlock> blockList; // List starts with MRU block; ends with LRU block

    public CacheSet(int maxSize, ReplacementPolicy policy) {
        this.maxSize = maxSize;
        this.policy = policy;
        this.blockList = new LinkedList<>();
    }

    public void put(CacheBlock cacheBlock) {
        boolean found = false;
        // Use a list iterator so you don't have to call linkedlist find. O(n) to find, O(1) to remove
        ListIterator<CacheBlock> listIter = blockList.listIterator(0);
        while (listIter.hasNext()) {
            CacheBlock curBlock = listIter.next();
            if (curBlock.getKeyHash() == cacheBlock.getKeyHash()) {
                listIter.remove();
                found = true;
                break;
            }
        }
        if (!found && isFull()) {
            blockList.addFirst(cacheBlock);
        }
        else {
            policy.replace(cacheBlock, blockList);
        }
    }

    public Object get() {
        //TODO: implement
        return null;
    }

    public int getSize() {
        return blockList.size();
    }

    public boolean isEmpty() {
        return blockList.isEmpty();
    }

    public boolean isFull() {
        return getSize() == maxSize;
    }
}
