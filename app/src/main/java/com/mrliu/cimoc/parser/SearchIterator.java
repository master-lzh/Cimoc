package com.mrliu.cimoc.parser;

import com.mrliu.cimoc.model.Comic;

/**
 * Created by Hiroshi on 2016/9/21.
 */

public interface SearchIterator {

    boolean empty();

    boolean hasNext();

    Comic next();

}
