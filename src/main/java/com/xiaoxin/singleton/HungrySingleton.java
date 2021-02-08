package com.xiaoxin.singleton;

/**
 * <饿汉式-单例模式实现>
 * 优点:未加锁,执行效率会提高
 * 缺点:类加载时就初始化，浪费内存
 * @author xiaoxin
 * @version 2021/2/8
 */
public class HungrySingleton {

    private static final HungrySingleton INSTANCE = new HungrySingleton();

    private HungrySingleton(){}

    public static HungrySingleton getInstance()
    {
        return INSTANCE;
    }

    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
