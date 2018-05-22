package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class Test7Maps {

    @ObjectId
    public Long objectId;

    private HashMap<Integer, String> integerStringHashMap;
    private Map<Long, Map<String, Test7Class1>> mapLongMap;

    public HashMap<Integer, String> getIntegerStringHashMap() {
        return integerStringHashMap;
    }

    public void setIntegerStringHashMap(HashMap<Integer, String> integerStringHashMap) {
        this.integerStringHashMap = integerStringHashMap;
    }

    public Map<Long, Map<String, Test7Class1>> getMapLongMap() {
        return mapLongMap;
    }

    public void setMapLongMap(Map<Long, Map<String, Test7Class1>> mapLongMap) {
        this.mapLongMap = mapLongMap;
    }
}
