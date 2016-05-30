package cu.uci.coj.Application.Filters;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by osvel on 5/5/16.
 */
public class Filter<T> implements Serializable{

    private List<String> stringFilter;
    private HashMap<String, T> map;

    public Filter(String firstElement) {
        stringFilter = new ArrayList<>();
        stringFilter.add(firstElement);
        map = new HashMap<>();
    }

    public void addFilter(String stringFilter, T filter){

        if (map.put(stringFilter, filter) == null){
            this.stringFilter.add(stringFilter);
        }

    }

    public String[] getFilterArray(){

        String array[] = new String[stringFilter.size()];

        for (int i = 0; i < stringFilter.size(); i++){
            array[i] = stringFilter.get(i);
        }

        Arrays.sort(array, 1, array.length);

        return array;
    }

    @Nullable
    public T getFilterValue(int pos){
        String key = getFilterArray()[pos];
        return map.get(key);
    }
}
