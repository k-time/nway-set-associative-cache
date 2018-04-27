package com.ktime.cache;

import java.util.LinkedList;

public interface ReplacementPolicy {
    void replace(CacheBlock cacheBlock, LinkedList<CacheBlock> blockList);
}
