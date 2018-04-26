package com.ktime.cache;

public class CacheBlock {

    private int keyHash;
    private Object value;

    public CacheBlock(int keyHash, Object value) {
        this.keyHash = keyHash;
        this.value = value;
    }

    public int getKeyHash() {
        return keyHash;
    }

    public Object getValue() {
        return value;
    }
}
