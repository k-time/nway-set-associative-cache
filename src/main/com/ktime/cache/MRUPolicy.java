package com.ktime.cache;

import java.util.LinkedList;

/**
 * Removes the most recently used CacheBlock and adds the new block.
 */
public class MRUPolicy implements ReplacementPolicy {
    @Override
    public void replace(CacheBlock block, LinkedList<CacheBlock> blockList) {
        blockList.removeFirst();
        blockList.addFirst(block);
    }
}
