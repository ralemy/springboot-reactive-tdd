package com.curisprofound.tddwebstack.assertions;

public abstract class Assertions<T extends Assertions<T>> {
    protected boolean not = false;
    public T Not(){
        not = true;
        return (T)this;
    }
    protected T chain(){
        not=false;
        return (T)this;
    }
}
