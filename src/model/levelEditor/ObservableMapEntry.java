package model.levelEditor;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.*;

public class ObservableMapEntry<K,V> implements Map.Entry<K,V>, ObservableValue<V> {

    private K key;
    private V value;
    private Set<ChangeListener<? super V>> listeners;
    private Set<InvalidationListener> invalidationListeners;
    private boolean changed = false;

    public ObservableMapEntry(K key, V value){
        this.key = key;
        this.value = value;
        listeners = new HashSet<>();
        invalidationListeners = new HashSet<>();
    }

    @Override
    public void addListener(ChangeListener<? super V> o){
        if(o==null) throw new NullPointerException();
        if(!listeners.contains(o)){
            listeners.add(o);
        }
    }

    @Override
    public void addListener(InvalidationListener o){
        if(o==null) throw new NullPointerException();
        if(!invalidationListeners.contains(o)){
            invalidationListeners.add(o);
        }
    }

    @Override
    public void removeListener(ChangeListener<? super V> o){
        listeners.remove(o);
    }

    @Override
    public void removeListener(InvalidationListener o){
        invalidationListeners.remove(o);
    }

    @Override
    public K getKey(){
        return this.key;
    }

    @Override
    public V getValue(){
        return this.value;
    }

    @Override
    public V setValue(V v){
        V oldValue = this.value;
        this.value = v;
        notifyListeners(oldValue,value);
        return oldValue;
    }

    private void notifyListeners(V oldValue, V newValue){
        for(ChangeListener listener: listeners){
            listener.changed(this,oldValue,newValue);
        }
        for(InvalidationListener listener: invalidationListeners){
            listener.invalidated(this);
        }
    }

    @Override
    public boolean equals(Object o){
        return new AbstractMap.SimpleEntry<K,V>(key,value).equals(o);
    }

    @Override
    public int hashCode(){
        return new AbstractMap.SimpleEntry<K,V>(key,value).hashCode();
    }

    public String toString(){
        return new AbstractMap.SimpleEntry<K,V>(key,value).toString();
    }


}
