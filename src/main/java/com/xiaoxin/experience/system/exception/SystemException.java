package com.xiaoxin.experience.system.exception;

/**
 * @author xiaoxin
 * 系统环境配置不正确所抛异常
 */
public class SystemException extends RuntimeException{

    private static final long serialVersionUID = 3953105655134951233L;

    public SystemException()
    {
        super();
    }

    public SystemException(String msg)
    {
        super(msg);
    }

    public SystemException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    public SystemException(Throwable cause)
    {
        super(cause);
    }
}
