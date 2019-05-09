package com.sonpen.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by 1109806 on 2019-05-07.
 */
public interface BufferedReaderCallback {

    int doSomethingWithReader(BufferedReader br) throws IOException;
}
