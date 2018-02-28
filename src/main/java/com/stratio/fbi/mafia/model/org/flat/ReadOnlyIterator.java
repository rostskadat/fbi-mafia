package com.stratio.fbi.mafia.model.org.flat;

import java.util.Iterator;
import java.util.Map;

import com.stratio.fbi.mafia.model.Mafioso;

class ReadOnlyIterator implements Iterator<Mafioso> {

    private Iterator<Mafioso> iterator;

    public ReadOnlyIterator(Iterator<Mafioso> iterator) {
        this.iterator = iterator;
    }

    public ReadOnlyIterator(Map<String, Mafioso> map) {
        this.iterator = map.values().iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Mafioso next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}