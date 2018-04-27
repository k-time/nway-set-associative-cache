package com.ktime.cache;

import org.junit.Test;

public class CacheTest {

    @Test
    public void Test() {
        Cache<Integer, Integer> cache = new NWaySetCache<>(4, 4, StandardPolicy.LRU);
        for (int i = 0; i < 20; i++) {
            cache.put(i, i);
        }
        int i=0;
    }
}
