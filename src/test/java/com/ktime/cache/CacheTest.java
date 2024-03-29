package com.ktime.cache;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class CacheTest {

    private class RandomStorage<K, V> extends LinkedMapStorage<K, V> {
        @Override
        public void replace(CacheBlock<K, V> block) {
            int index = (int) (Math.random() * blockMap.size());
            blockMap.remove(index);
            this.add(block);
        }

        @Override
        public CacheSetStorage<K, V> createNewInstance() {
            return new RandomStorage<>();
        }
    }

    private class InvalidStorage<K, V> extends LinkedMapStorage<K, V> {
        @Override
        public void replace(CacheBlock<K, V> block) {
            // Invalid because policy doesn't replace any blocks
            this.add(block);

        }

        @Override
        public CacheSetStorage<K, V> createNewInstance() {
            return new InvalidStorage<>();
        }
    }

    private class LFUStorage<K, V> extends LinkedMapStorage<K, V> {
        @Override
        public void replace(CacheBlock<K, V> block) {
            Date now = new Date();
            double maxTimePerUse = 0;
            CacheBlock<K, V> removeBlock = null;
            for (CacheBlock<K, V> curBlock : blockMap.values()) {
                double timePerUse = (now.getTime() - block.getInsertionDate().getTime()) / ((double) block.getUses());
                if (timePerUse > maxTimePerUse) {
                    maxTimePerUse = timePerUse;
                    removeBlock = curBlock;
                }
            }
            if (removeBlock != null) {
                blockMap.remove(removeBlock.getKey());
                this.add(block);
            }
            else {
                blockMap.remove(blockMap.firstKey());
                this.add(block);
            }
        }

        @Override
        public CacheSetStorage<K, V> createNewInstance() {
            return new LFUStorage<>();
        }
    }

    private static final int NUM_SETS = 4;
    private static final int BLOCKS_PER_SET = 4;
    private static final int MAX_CACHE_SIZE = NUM_SETS * BLOCKS_PER_SET;

    // Different ways to instantiate cache
    private final Cache<String, Integer> stringCache = new NWaySetCache<>();
    private final NWaySetCache<Integer, Integer> intCache = new NWaySetCache<>(BLOCKS_PER_SET, NUM_SETS, new LRUStorage<>());

    @Before
    public void clearCache() {
        stringCache.evictAll();
        intCache.evictAll();
    }

    @Test
    public void testNormalCase() {
        stringCache.put("Kenny", 1);
        stringCache.put("Shirley", 5);
        assertEquals((int) stringCache.get("Kenny"), 1);
        assertEquals((int) stringCache.get("Shirley"), 5);
        stringCache.put("Kenny", 7);
        assertEquals((int) stringCache.get("Kenny"), 7);
        assertNull(stringCache.get("James"));
        assertEquals(stringCache.size(), 2);
    }

    @Test
    public void testCacheSize() {
        populateIntCache(10);
        assertEquals(intCache.size(), 10);
        populateIntCache(100);
        assertEquals(intCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testEvictAll() {
        populateIntCache(30);
        intCache.evictAll();
        assertEquals(intCache.size(), 0);
    }

    @Test
    public void testPositionUpdatesInSet() {
        populateIntCache(MAX_CACHE_SIZE);
        intCache.put(0, 0);
        intCache.put(4, 4);
        assert(intCache.getBlocksFromSet(0).equals(Arrays.asList(8, 12, 0, 4)));
        assertEquals(intCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testLRUPolicy() {
        populateIntCache(MAX_CACHE_SIZE);
        intCache.put(16, 16);
        assert(intCache.getBlocksFromSet(0).equals(Arrays.asList(4, 8, 12, 16)));
        intCache.put(20, 20);
        assert(intCache.getBlocksFromSet(0).equals(Arrays.asList(8, 12, 16, 20)));
        intCache.get(12);
        intCache.put(-4, -4);
        assert(intCache.getBlocksFromSet(0).equals(Arrays.asList(16, 20, 12, -4)));
        assertEquals(intCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testMRUPolicy() {
        NWaySetCache<Integer, Integer> mruCache = new NWaySetCache<>(BLOCKS_PER_SET, NUM_SETS, new MRUStorage<>());
        populateIntCache(mruCache, MAX_CACHE_SIZE);
        mruCache.put(17, 17);
        assert(mruCache.getBlocksFromSet(1).equals(Arrays.asList(1, 5, 9, 17)));
        mruCache.put(1, 1);
        assert(mruCache.getBlocksFromSet(1).equals(Arrays.asList(5, 9, 17, 1)));
        mruCache.put(21, 21);
        assert(mruCache.getBlocksFromSet(1).equals(Arrays.asList(5, 9, 17, 21)));
        mruCache.get(9);
        assert(mruCache.getBlocksFromSet(1).equals(Arrays.asList(5, 17, 21, 9)));
        mruCache.put(-5, -5);
        assert(mruCache.getBlocksFromSet(1).equals(Arrays.asList(5, 17, 21, -5)));
        assertEquals(mruCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testValidAlternativePolicy() {
        NWaySetCache<Integer, Integer> randomCache = new NWaySetCache<>(BLOCKS_PER_SET, NUM_SETS, new RandomStorage<>());
        populateIntCache(randomCache, MAX_CACHE_SIZE);
        randomCache.put(17, 17);
        assert(randomCache.getBlocksFromSet(1).contains(17));
        randomCache.put(18, 18);
        assert(randomCache.getBlocksFromSet(2).contains(18));
        assertEquals(randomCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testInvalidAlternativePolicy() {
        Cache<Integer, Integer> cache = new NWaySetCache<>(4, 4, new InvalidStorage<>());
        populateIntCache(cache, MAX_CACHE_SIZE);
        cache.put(16, 16);
        cache.put(-4, -4);
        // Need to check that that the cache has not been overfilled
        assertEquals(cache.size(), MAX_CACHE_SIZE - BLOCKS_PER_SET + 1);
    }

    @Test
    public void testLeastFrequentlyUsedPolicy() throws InterruptedException {
        NWaySetCache<Integer, Integer> lfuCache = new NWaySetCache<>(BLOCKS_PER_SET, NUM_SETS, new LFUStorage<>());
        populateIntCache(lfuCache, MAX_CACHE_SIZE);
        populateIntCache(lfuCache, MAX_CACHE_SIZE-1);
        Thread.sleep(10);
        lfuCache.put(19, 19);
        assert(lfuCache.getBlocksFromSet(3).equals(Arrays.asList(3, 7, 11, 19)));
        assertEquals(lfuCache.size(), MAX_CACHE_SIZE);
    }

    @Test
    public void testCacheBlockUsage() {
        populateIntCache(MAX_CACHE_SIZE);
        for (int i = 0; i < NUM_SETS; i++) {
            for (CacheBlock block : intCache.getCacheSet(i).getBlocks()) {
                assertEquals(block.getUses(), 1);
                assertNotNull(block.getInsertionDate());
                assertNotNull(block.getLastUsedDate());
            }
        }
        populateIntCache(MAX_CACHE_SIZE);
        for (int i = 0; i < NUM_SETS; i++) {
            for (CacheBlock block : intCache.getCacheSet(i).getBlocks()) {
                // Check that uses has been incremented and lastUsedDate is updated
                assertEquals(block.getUses(), 2);
                assert(block.getInsertionDate().compareTo(block.getLastUsedDate()) <= 0);
            }
        }
    }

    private void populateIntCache(int max) {
        populateIntCache(this.intCache, max);
    }

    private void populateIntCache(Cache<Integer, Integer> cache, int max) {
        for (int i = 0; i < max; i++) {
            cache.put(i, i);
        }
    }
}
