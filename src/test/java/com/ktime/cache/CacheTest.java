package com.ktime.cache;

import org.junit.Test;

public class CacheTest {

    @Test
    public void Test() {
        Cache<String, String> cache = new NWaySetCache<>(4, 4, StandardPolicy.MRU);
    }
}
