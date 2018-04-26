package com.ktime.cache;

import java.util.LinkedList;

/**
 * Removes the least recently used CacheBlock and adds the new block.
 */
public class LRUPolicy implements ReplacementPolicy {
    @Override
    public void replace(CacheBlock block, LinkedList<CacheBlock> blockList) {
        blockList.removeLast();
        blockList.addFirst(block);
    }
}
