package ir.map.gr222.sem7.utils.observer;

import ir.map.gr222.sem7.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}