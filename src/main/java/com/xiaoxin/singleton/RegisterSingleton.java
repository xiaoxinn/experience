package com.xiaoxin.singleton;

/**
 * <登记式-单例模式实现>
 * 这种方式能达到双检锁方式一样的功效，但实现更简单。
 * 对静态域使用延迟初始化，应使用这种方式而不是双检锁方式。
 * 这种方式只适用于静态域的情况，双检锁方式可在实例域需要延迟初始化时使用
 * @author xiaoxin
 * @version 2021/2/8
 */
public class RegisterSingleton {

    private static class SingletonHolder {
        private static final RegisterSingleton INSTANCE = new RegisterSingleton();
    }

    private RegisterSingleton() {}

    public static RegisterSingleton getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
