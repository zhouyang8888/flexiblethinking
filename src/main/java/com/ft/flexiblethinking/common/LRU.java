package com.ft.flexiblethinking.common;

import java.util.HashMap;
import java.util.Map;

public class LRU<V> {
    private class Node {
        protected V item = null;
        protected String name = null;
        protected Node pre = null;
        protected Node next = null;
    }

    Node lruFirst = null;
    Node lruLast = null;

    Map<String, Node> items = new HashMap<>();
    int maxSize = 100;

    public LRU(int maxSize) {
        if (maxSize > 100)
            this.maxSize = maxSize;
        lruFirst = new Node();
        lruLast = lruFirst;
        lruFirst.next = lruLast;
        lruLast.pre = lruFirst;
    }

    public synchronized void add(String name, V item) {
        if (items.containsKey(name)) {
            Node oldItem = items.get(name);
            pull(oldItem);
            oldItem.item = item;
            oldItem.name = name;
            push(oldItem);
        } else {
            Node newItem = new Node();
            newItem.item = item;
            newItem.name = name;
            push(newItem);
            items.put(name, newItem);
            if (items.size() > maxSize) {
                Node lastNode = lruLast.pre;
                pull(lastNode);
                items.remove(lastNode.name);
            }
        }
    }

    public synchronized V get(String name) {
        Node item = items.get(name);
        if (item != null) {
            pull(item);
            push(item);
            return item.item;
        }
        return null;
    }

    @SuppressWarnings("")
    public synchronized void remove(String name) {
        Node item = items.get(name);
        if (item != null) {
            items.remove(name);
            pull(item);
        }
    }

    private void pull(Node item) {
        item.pre.next = item.next;
        item.next.pre = item.pre;
        if (lruFirst == item) {
            lruFirst = lruFirst.next;
        }
    }

    private void push(Node item) {
        item.next = lruFirst;
        item.pre = lruFirst.pre;
        item.pre.next = item;
        item.next.pre = item;
        lruFirst = item;
    }
}
