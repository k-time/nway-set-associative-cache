package com.ktime.cache;

import java.util.Date;

public class CacheBlock {

    private int keyHash;
    private Object value;

    // These fields are not used in my solution. They are provided/calculated so clients
    // may use them to help implement their alternative replacement policies.
    private long uses;
    private Date lastUsedDate;
    private Date insertionDate;

    // Package-private constructor. Client cannot create new CacheBlocks or modify data; can only read data.
    CacheBlock(int keyHash, Object value) {
        this.keyHash = keyHash;
        this.value = value;
        this.uses = 1;
        this.lastUsedDate = new Date();
        this.insertionDate = new Date();
    }

    int getKeyHash() {
        return keyHash;
    }

    public Object getValue() {
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

    void updateValue(CacheBlock newBlock) {
        if (this.keyHash == newBlock.getKeyHash()) {
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
