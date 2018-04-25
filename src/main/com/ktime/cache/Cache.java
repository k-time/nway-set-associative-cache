package com.ktime.cache;

public interface Cache {
    public Object put(Object key, Object val);
    public Object get(Object key);
}
