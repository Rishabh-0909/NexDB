package io.github.rishabh0909.nexdb.storage.db;


import io.github.rishabh0909.nexdb.exception.InvalidDBObjectWrapper;

public class MutableDBObjectDecorator extends DBObject {

    public MutableDBObjectDecorator(DBObject decorated) throws InvalidDBObjectWrapper {
        super(decorated.getPage(), decorated.getBegin(), decorated.getEnd());
    }

    public void modifyData(int offset, byte[] value){
        // todo: throw err
    }

    public void setCollectionId(int collectionId) {
        // todo: throw err

    }

    public void setSize(int size) {
        // todo: throw err

    }

    public void deactivate() {
        // todo: throw err
    }

}
