package model.levelEditor;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservableHashMap<K,V> extends HashMap<K,V> {

    private ObservableList<Entry<K,V>> observableList;

    public ObservableHashMap (){
        super();
        observableList = FXCollections.observableArrayList();
        observableList.addListener( (ListChangeListener) c -> {
            System.out.println(c);
        });
    }

    @Override
    public void clear(){
        super.clear();
        observableList.clear();
    }

    @Override
    public V put(K key, V value){
        if (super.containsKey(key)){
            if(super.get(key) == value) return value;
            else {
                V oldValue = super.get(key);
                observableList.remove(new SimpleEntry<K,V>(key,oldValue));
            }
        }
        observableList.add(new SimpleEntry<K,V>(key,value));
        return super.put(key,value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map){
        for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()){
            this.put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public V remove(Object key){
        K castedKey;
        try {
            castedKey = (K) key;
        }
        catch(ClassCastException e){
            return null;
        }
        observableList.remove(new SimpleEntry<K,V>(castedKey,super.get(key)));
        return super.remove(key);
    }

    public ObservableList<Entry<K, V>> getObservableList() {
        return observableList;
    }



}
