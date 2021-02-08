package com.xiaoxin.singleton;

/**
 * <懒汉式-单例模式实现>
 * <线程安全>
 * 优点：第一次调用才初始化，避免内存浪费。
 * 缺点：必须加锁 synchronized 才能保证单例，但加锁会影响效率。
 * @author xiaoxin
 * @version 2021/2/8
 */
public class LazySingletonSafe {

    private static LazySingletonSafe instance;

    private LazySingletonSafe(){}

    public static synchronized LazySingletonSafe getInstance()
    {
        if (instance == null)
        {
            instance = new LazySingletonSafe();
        }
        return instance;
    }

    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
