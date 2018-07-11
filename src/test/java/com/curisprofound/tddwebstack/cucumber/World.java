package com.curisprofound.tddwebstack.cucumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class World {
    private final Map<String,Object> context;
    private static final Logger logger = LoggerFactory.getLogger(World.class);

    public World() {
        context = new HashMap<>();
    }

    public void Clear(){
        context.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T Get(Class<T> clazz, String key){
        return (T) context.getOrDefault(key,null);
    }

    public <T> T Get(Class<T> clazz){
        return Get(clazz, clazz.getCanonicalName());
    }



    public <T> T Add(Class<T> clazz, T target, String key){
        context.put(key,target);
        return target;
    }
    public <T> T Add(Class<T> clazz, T target){
        return Add(clazz,target, clazz.getCanonicalName());
    }
    public <T> T Add(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return Add(clazz, clazz.newInstance(),clazz.getCanonicalName());
    }


}
