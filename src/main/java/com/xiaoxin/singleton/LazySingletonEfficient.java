package com.xiaoxin.singleton;

/**
 * <懒汉式-单例模式实现>
 * 这种方式采用双锁机制，安全且在多线程情况下能保持高性能。
 * @author xiaoxin
 * @version 2021/2/8
 */
public class LazySingletonEfficient {

    private static volatile LazySingletonEfficient instance;

    private LazySingletonEfficient(){}

    public static LazySingletonEfficient getInstance()
    {
        if (instance == null)
        {
            synchronized (LazySingletonEfficient.class)
            {
                if (instance == null)
                {
                    instance = new LazySingletonEfficient();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
