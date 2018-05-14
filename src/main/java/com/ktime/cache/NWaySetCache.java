package com.ktime.cache;

import org.apache.commons.collections4.list.FixedSizeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an N-way set-associative cache.
 */
public class NWaySetCache<K, V> implements Cache<K, V> {
    private static final int DEFAULT_BLOCKS_PER_SET = 4;
    private static final int DEFAULT_NUM_SETS = 64;

    private final int blocksPerSet;
    private final int numSets;
    private final List<CacheSet<K, V>> cacheSetList;

    public NWaySetCache() {
        this(DEFAULT_BLOCKS_PER_SET, DEFAULT_NUM_SETS, new LRUStorage<>());
    }

    public NWaySetCache(int blocksPerSet, int numSets) {
        this(blocksPerSet, numSets, new LRUStorage<>());
    }

    public NWaySetCache(CacheSetStorage<K, V> storage) {
        this(DEFAULT_BLOCKS_PER_SET, DEFAULT_NUM_SETS, storage);
    }

    public NWaySetCache(int blocksPerSet, int numSets, CacheSetStorage<K, V> storage) {
        this.blocksPerSet = blocksPerSet;
        this.numSets = numSets;
        List<CacheSet<K, V>> tempList = new ArrayList<>();
        for (int i = 0; i < this.numSets; i++) {
            tempList.add(new CacheSet<>(this.blocksPerSet, storage.createNewInstance()));
        }
        // Wrapper to fix the size of the list (number of sets is fixed)
        this.cacheSetList = FixedSizeList.fixedSizeList(tempList);
    }

    @Override
    public void put(K key, V val) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        CacheSet<K, V> cacheSet = getCacheSet(setIndex);
        CacheBlock<K, V> cacheBlock = new CacheBlock<>(key, val);
        cacheSet.put(cacheBlock);
    }

    @Override
    public V get(K key) {
        int keyHash = key.hashCode();
        int setIndex = calculateSetIndex(keyHash);
        return getCacheSet(setIndex).get(key);
    }

    @Override
    public void evictAll() {
        for (CacheSet cacheSet : cacheSetList) {
            cacheSet.evictAll();
        }
    }

    @Override
    public int size() {
        // O(# of sets) implementation used for testing.
        // Can improve to O(1) by keeping tracking of insertions/deletions.
        int count = 0;
        for (CacheSet cacheSet : cacheSetList) {
            count += cacheSet.size();
        }
        return count;
    }

    CacheSet<K, V> getCacheSet(int index) {
        return cacheSetList.get(index);
    }

    List<V> getBlocksFromSet(int index) {
        List<V> list = new ArrayList<>();
        for (CacheBlock<K, V> block : getCacheSet(index).getBlocks()) {
            list.add(block.getValue());
        }
        return list;
    }

    private int calculateSetIndex(int keyHash) {
        // keyHash can be negative. Math.abs() is needed to ensure a positive index.
        return Math.abs(keyHash % numSets);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numSets; i++) {
            sb.append(String.format("Set %d:\t", i));
            sb.append(getCacheSet(i).toString());
            sb.append("\n");
        }
        return sb.toString().trim();
    }
}
