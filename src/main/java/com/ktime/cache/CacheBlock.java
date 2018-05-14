package com.ktime.cache;

import java.util.Date;

public class CacheBlock<K, V> {

    private K key;
    private V value;

    // These fields are not used in my solution. They are provided/calculated so clients
    // may use them to help implement their alternative replacement policies.
    private long uses;
    private Date lastUsedDate;
    private Date insertionDate;

    // Package-private constructor. Client cannot create new CacheBlocks or modify data; can only read data.
    CacheBlock(K key, V value) {
        this.key = key;
        this.value = value;
        this.uses = 1;
        this.lastUsedDate = new Date();
        this.insertionDate = new Date();
    }

    public K getKey() { return key; }

    public V getValue() {
        return value;
    }

    public long getUses() {
        return uses;
    }

    public Date getLastUsedDate() {
        return lastUsedDate;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    void updateValue(CacheBlock<K, V> newBlock) {
        if (this.key == newBlock.getKey()) {
            this.value = newBlock.getValue();
        }
    }

    void use() {
        // Update date
        this.lastUsedDate = new Date();
        // Update uses count
        if (uses < Long.MAX_VALUE) {
            uses++;
        }
    }
}
