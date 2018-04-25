package com.ktime.cache;

public class NWaySetCache implements Cache {

    private static final int DEFAULT_SET_SIZE = 4;
    private static final ReplacementPolicy DEFAULT_POLICY = ReplacementPolicy.LRU;

    private int setSize;
    private ReplacementPolicy policy;

    public NWaySetCache() {
        this(DEFAULT_SET_SIZE, DEFAULT_POLICY);

    }

    public NWaySetCache(int setSize, ReplacementPolicy policy) {
        this.setSize = setSize;
        this.policy = policy;
    }

    public Object put(Object key, Object val) {

    }

    public Object get(Object key) {

    }
}
