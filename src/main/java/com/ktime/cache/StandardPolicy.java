package com.ktime.cache;

import java.util.LinkedList;

public enum StandardPolicy implements ReplacementPolicy {
    LRU {
        @Override
        public void replace(CacheBlock block, LinkedList<CacheBlock> blockList) {
            blockList.removeLast();
            blockList.addFirst(block);
        }
    },
    MRU {
        @Override
        public void replace(CacheBlock block, LinkedList<CacheBlock> blockList) {
            blockList.removeFirst();
            blockList.addFirst(block);
        }
    }
}
