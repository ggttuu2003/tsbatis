package com.tsbatis.utils;

import java.io.InputStream;

/**
 * @author 尹强强
 * @version 1.0
 */
public class Resouces {

    private Resouces(){}

    public static InputStream getResourceAsReader(String url){
        return ClassLoader.getSystemClassLoader().getResourceAsStream(url);
    }
}
