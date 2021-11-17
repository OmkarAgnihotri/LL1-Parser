import java.util.HashMap;
import java.util.Map;

public class Map2D<K1, K2, V> {
    private final Map<K1, Map<K2, V>> mp;

    public  Map2D(){
        mp = new HashMap<K1, Map<K2, V>>();
    }

    public V put(K1 key1, K2 key2, V value) {
        Map<K2, V> map;

        if (mp.containsKey(key1)) {
            map = mp.get(key1);
        } else {
            map = new HashMap<K2, V>();
            mp.put(key1, map);
        }

        return map.put(key2, value);
    }
    public V get(K1 key1, K2 key2) {
        if (mp.containsKey(key1)) {
            return mp.get(key1).get(key2);
        } else {
            return null;
        }
    }

    public void display(){
        for(Map.Entry<K1, Map<K2, V>> tuple: mp.entrySet()){
            for(Map.Entry<K2, V> valueMap: tuple.getValue().entrySet()){
                System.out.println(
                        tuple.getKey() + " " +
                                valueMap.getKey() + " : " +
                                valueMap.getValue().toString()
                );
            }
        }
    }
}
