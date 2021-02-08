package com.xiaoxin.singleton;

/**
 * <测试线程安全的类>
 * @author xiaoxin
 * @version 2021/2/7 17:48
 */
public class SingletonDemo {

    public static void main(String[] args)
    {
        /*
          线程安全的预期结果:
              三个线程的打印结果相同

          执行结果:
             custom-thread-1: com.xiaoxin.singleton.LazySingleton@469dad33
             custom-thread-3: com.xiaoxin.singleton.LazySingleton@16187bcd
             custom-thread-2: com.xiaoxin.singleton.LazySingleton@16187bcd

          结论:
              这是一种线程不安全的单例模式
         */
        checkSingletonIsUnsafe(()-> System.out.println(LazySingleton.getInstance()));

        checkSingletonIsUnsafe(() -> System.out.println(HungrySingleton.getInstance()));
    }


    public static void checkSingletonIsUnsafe(Runnable r)
    {
        // 创建3个线程来获取单例对象
        Thread thread1 = new Thread(r, "custom-thread-1");
        Thread thread2 = new Thread(r, "custom-thread-2");
        Thread thread3 = new Thread(r, "custom-thread-3");

        // 开启3个线程
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
