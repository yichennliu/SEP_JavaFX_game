package model.levelEditor;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservableHashMap<K,V> extends HashMap<K,V> {

    private ObservableList<ObservableMapEntry<K,V>> observableList;
    private Pair lastPut;

    public ObservableHashMap (){
        super();
        observableList = FXCollections.observableArrayList(p -> new javafx.beans.Observable[]{p});
        observableList.addListener( (ListChangeListener) c -> {
            c.next();
            ObservableMapEntry<K,V> entry = this.observableList.get(c.getFrom());
            silentPutInMap(entry.getKey(),entry.getValue());
        });
    }

    @Override
    public void clear(){
        super.clear();
        observableList.clear();
    }

    @Override
    public V put(K key, V value){
        if(new Pair(key,value).equals(lastPut)) return value;
        if (super.containsKey(key)){
            if(super.get(key) == value) return value;
            else {
                V oldValue = super.get(key);
                observableList.remove(new ObservableMapEntry<>(key,oldValue));
            }
        }
        observableList.add(new ObservableMapEntry<>(key,value));
        lastPut = new Pair(key,value);
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

    private void silentPutInMap(K key, V val){
        super.put(key,val);
        System.out.println(key + " - " + super.get(key));
    }

    public ObservableList<ObservableMapEntry<K,V>> getObservableList() {
        return observableList;
    }



}
