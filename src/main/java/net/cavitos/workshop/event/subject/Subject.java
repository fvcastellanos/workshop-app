package net.cavitos.workshop.event.subject;

public interface Subject<T> {

    void addObserver(T observer);
    void removeObserver(T observer);
    void notifyObservers();
}
