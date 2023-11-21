package ir.map.gr222.sem7.utils.observer;


import ir.map.gr222.sem7.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
