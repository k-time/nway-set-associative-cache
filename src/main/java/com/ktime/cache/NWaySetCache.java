package com.ktime.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an N-way set-associative cache.
 */
public class NWaySetCache<K, V> implements Cache<K, V> {
    private static final int DEFAULT_BLOCKS_PER_SET = 4;
    private static final int DEFAULT_NUM_SETS = 16;
    private static final ReplacementPolicy DEFAULT_POLICY = StandardPolicy.LRU;

    private final int blocksPerSet;
    private final int numSets;
    private final CacheSet[] cacheSetArray;
    private final ReplacementPolicy policy;

    public NWaySetCache() {
        this(DEFAULT_BLOCKS_PER_SET, DEFAULT_NUM_SETS, DEFAULT_POLICY);
    }

    public NWaySetCache(int blocksPerSet, int numSets) {
        this(blocksPerSet, numSets, DEFAULT_POLICY);
    }

    public NWaySetCache(ReplacementPolicy policy) {
        this(DEFAULT_BLOCKS_PER_SET, DEFAULT_NUM_SETS, policy);
    }

    public NWaySetCache(int blocksPerSet, int numSets, ReplacementPolicy policy) {
        this.blocksPerSet = blocksPerSet;
        this.numSets = numSets;
        this.policy = policy;
        this.cacheSetArray = new CacheSet[numSets];
        for (int i = 0; i < numSets; i++) {
            cacheSetArray[i] = new CacheSet(blocksPerSet, this.policy);
        }
    }

    public void put(K key, V val) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        CacheSet cacheSet = getCacheSet(setIndex);
        CacheBlock cacheBlock = new CacheBlock(keyHash, val);
        cacheSet.put(cacheBlock);
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        Object val = getCacheSet(setIndex).get(keyHash);
        // Because CacheSet is not a generic class (see design pdf), the returned Object must be cast back
        // to type V. This is safe because we ensured that every object inserted into the CacheSet is type V.
        return (V) val;
    }

    public void evictAll() {
        for (CacheSet cacheSet : cacheSetArray) {
            cacheSet.evictAll();
        }
    }

    public int size() {
        // O(# of sets) implementation used for testing.
        // Can improve to O(1) by keeping tracking of insertions/deletions.
        int count = 0;
        for (CacheSet cacheSet : cacheSetArray) {
            count += cacheSet.size();
        }
        return count;
    }

    public ReplacementPolicy getPolicy() {
        return policy;
    }

    CacheSet getCacheSet(int index) {
        return cacheSetArray[index];
    }

    @SuppressWarnings("unchecked")
    List<V> getBlocksFromSet(int index) {
        List<V> list = new ArrayList<>();
        for (CacheBlock block : getCacheSet(index).getBlocks()) {
            list.add((V) block.getValue());
        }
        return list;
    }

    private int calculateSetIndex(int keyHash) {
        // keyHash can be negative. Math.abs() is needed to ensure a positive index.
        return Math.abs(keyHash % numSets);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numSets; i++) {
            sb.append(String.format("Set %d:\t", i));
            sb.append(getCacheSet(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
