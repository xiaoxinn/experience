package com.xiaoxin.singleton;

/**
 * <枚举-单例模式实现>
 * 这是实现单例模式的最佳方法。
 * 它更简洁，自动支持序列化机制，绝对防止多次实例化
 * @author xiaoxin
 * @version 2021/2/8
 */
public enum EnumSingleton {

    INSTANCE;

    @Override
    public String toString()
    {
        return Thread.currentThread().getName() + ": " + super.toString();
    }
}
