package com.ktime.cache;

import org.apache.commons.collections4.map.LinkedMap;

public interface ReplacementPolicy {
    void replace(CacheBlock cacheBlock, LinkedMap<Integer, CacheBlock> blockMap);
}
