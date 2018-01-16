package model.themeEditor;

import java.util.HashMap;
import java.util.Map;

public class HashMap2D<T1,T2,T3> {

    private Map<T1,Map<T2,T3>> map;

    public HashMap2D(){
        this.map = new HashMap<T1,Map<T2,T3>>();
    }

    public void put(T1 key1, T2 key2, T3 entry){
        Map<T2,T3> innerMap= this.map.get(key1);
        if(innerMap==null){
            /*no entry for key1*/
            innerMap = new HashMap<T2,T3>();
            this.map.put(key1,innerMap);
        }
        innerMap.put(key2,entry);
    }

    public T3 get(T1 key1, T2 key2){
        Map<T2,T3> key1Entry = this.map.get(key1);
        if(key1Entry!=null){
            return key1Entry.get(key2);
        }
        return null;
    }

    public void clear(){
        this.map.clear();
    }

    public void clearEntry(T1 key1){
        Map<T2,T3> innerMap = this.map.get(key1);
        if(innerMap!=null) innerMap.clear();
    }

    public void remove(T1 key1){
        this.map.remove(key1);
    }

    public void remove(T1 key1, T2 key2){
        Map<T2,T3> innerMap = this.map.get(key1);
        if(innerMap!=null){
            innerMap.remove(key2);
        }
    }

}

