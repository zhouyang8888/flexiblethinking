package com.ft.flexiblethinking.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LRU<V> {
    List<String> nameList = new LinkedList<>();
    Map<String, V> items = new HashMap<>();
    int maxSize = 100;

    public LRU(int maxSize) {
        if (maxSize > 100)
            this.maxSize = maxSize;
    }

    public void add(String name, V item) {
        if (items.containsKey(name)) {
            nameList.remove(name);
        }
        items.put(name, item);
        nameList.add(0, name);
        if (nameList.size() > maxSize) {
            name = nameList.remove(maxSize);
            items.remove(name);
        }
    }

    public V get(String name) {
        V item = items.get(name);
        if (item != null) {
            nameList.remove(name);
            nameList.add(0, name);
        }
        return item;
    }

    public void remove(String name) {
        nameList.remove(name);
        items.remove(name);
    }
}
