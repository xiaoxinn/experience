package com.xiaoxin.singleton;

/**
 * <懒汉式-单例模式实现>
 * <线程不安全>
 * 不支持多线程。因为没有加锁 synchronized，所以严格意义上它并不算单例模式
 * @author xiaoxin
 * @version 2021/2/7
 */
public class LazySingleton {

    private static LazySingleton instance;

    private LazySingleton(){}

    public static LazySingleton getInstance()
    {
        if (instance == null)
        {
            instance = new LazySingleton();
        }
        return instance;
    }

    /**
     * 重写toString方法,打印创建的线程和该对象的地址值
     * @return 创建的线程名和该对象的地址值
     */
    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
