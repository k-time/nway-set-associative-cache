package com.ktime.cache;

import org.apache.commons.collections4.map.LinkedMap;

public enum StandardPolicy implements ReplacementPolicy {
    LRU {
        @Override
        public void replace(CacheBlock block, LinkedMap<Object, CacheBlock> blockMap) {
            blockMap.remove(blockMap.firstKey());
            blockMap.put(block.getKey(), block);
        }
    },
    MRU {
        @Override
        public void replace(CacheBlock block, LinkedMap<Object, CacheBlock> blockMap) {
            blockMap.remove(blockMap.lastKey());
            blockMap.put(block.getKey(), block);
        }
    }
}
