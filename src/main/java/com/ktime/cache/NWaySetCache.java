package com.ktime.cache;

/**
 * Implementation of an N-way set-associative cache.
 */
public class NWaySetCache<K, V> implements Cache<K, V> {
    private static final int DEFAULT_BLOCK_SIZE = 4;
    private static final int DEFAULT_SET_SIZE = 4;
    private static final ReplacementPolicy DEFAULT_POLICY = StandardPolicy.LRU;

    private int blockSize;
    private int setSize;
    private CacheSet[] cacheSetArray;
    private ReplacementPolicy policy;

    public NWaySetCache() {
        this(DEFAULT_BLOCK_SIZE, DEFAULT_SET_SIZE, DEFAULT_POLICY);
    }

    public NWaySetCache(int blockSize, int setSize) {
        this(blockSize, setSize, DEFAULT_POLICY);
    }

    public NWaySetCache(ReplacementPolicy policy) {
        this(DEFAULT_BLOCK_SIZE, DEFAULT_SET_SIZE, policy);
    }

    public NWaySetCache(int blockSize, int setSize, ReplacementPolicy policy) {
        this.blockSize = blockSize;
        this.setSize = setSize;
        this.policy = policy;
        this.cacheSetArray = new CacheSet[setSize];
        for (int i = 0; i < setSize; i++) {
            cacheSetArray[i] = new CacheSet(this.blockSize, this.policy);
        }
    }

    public void put(K key, V val) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        CacheSet cacheSet = getCacheSet(setIndex);
        CacheBlock cacheBlock = new CacheBlock(keyHash, val);
        cacheSet.put(cacheBlock);
    }

    public V get(K key) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        Object val = getCacheSet(setIndex).get(keyHash);
        // Need to cast back to V. This is ok because we ensured that every object in the CacheSet is type V
        return (V) val;
    }

    private CacheSet getCacheSet(int index) {
        return cacheSetArray[index];
    }

    private int calculateSetIndex(int keyHash) {
        // keyHash can be negative, so keyHash % setSize can be negative.
        // Math.abs() is needed to ensure a positive index.
        return Math.abs(keyHash % setSize);
    }
}
