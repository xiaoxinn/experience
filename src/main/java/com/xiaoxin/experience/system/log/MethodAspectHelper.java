package com.xiaoxin.experience.system.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
/**
 * create by xiaoxin on 2021/3/13
 */
public class MethodAspectHelper
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodAspectHelper.class);

    private ProceedingJoinPoint joinPoint;

    private MethodSignature signature;

    private String methodFullName;

    private Class<?>[] paramTypes;

    private Object[] params;

    private Object param;

    public MethodAspectHelper(ProceedingJoinPoint pjp)
    {
        this.joinPoint = pjp;
        signature = (MethodSignature)pjp.getSignature();
        String methodName = signature.getName();
        String className = pjp.getTarget().getClass().getName();
        this.methodFullName = className + "." + methodName;
        paramTypes = signature.getParameterTypes();
        this.params = pjp.getArgs();

        if (null != params && params.length > 0)
        {
            param = params[0];
        }
    }

    public <T> T getFirstParam()
    {
        //noinspection unchecked
        return (T)param;
    }

    public <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass)
    {
        return signature.getMethod().getAnnotation(annotationClass);
    }

    private Class getReturnType()
    {
        return signature.getMethod().getReturnType();
    }

    private Object getReturnObject()
    {
        Object retObject = null;
        try
        {
            Class retClass = getReturnType();
            retObject = retClass.newInstance();
        }
        catch (Exception e)
        {
            LOG.warn("getReturnObject class newInstance fail.", e.getMessage());
        }
        return retObject;
    }

    public Object getMethodResponse(int errorCode, Throwable e)
            throws Throwable
    {
        Object retObject = getReturnObject();
        /*if (retObject instanceof Response)
        {
            Response response = (Response)retObject;
            response.setResultCode(errorCode);
            response.setResultDesc(e.getMessage());
        }
        else
        {
            throw e;
        }*/
        return retObject;
    }

    public void logEnter()
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info((">>> Enter " + methodFullName));
        }

        if (LOG.isDebugEnabled())
        {
            StringBuilder builder = new StringBuilder("Parameters:[");
            if (null != params && params.length > 0)
            {
                for (int i = 0; i < params.length; i++)
                {
                    Object arg = params[i];
                    builder.append(arg);
                    if (i < params.length - 1)
                    {
                        builder.append(", ");
                    }
                }
            }
            builder.append("]");
            LOG.debug(builder.toString());
        }
    }

    public void logLeave(Object returnValue)
    {
        if (LOG.isDebugEnabled())
        {
            /*if (returnValue instanceof AcBaseBean)
            {
                LOG.debug("Return:[{}]", returnValue);
            }
            else if (returnValue instanceof Serializable)
            {
                LOG.debug("Return:[{}]", JSONUtil.log(returnValue));
            }
            else
            {
                LOG.debug("Return:[{}]", returnValue);
            }*/
        }

        if (LOG.isInfoEnabled())
        {
            LOG.info("<<< Exit " + methodFullName);
        }
    }

    public void logError(Throwable e)
    {
        LOG.error(methodFullName + " occur error: ", e);
    }

    public Object logExecute()
            throws Throwable
    {
        logEnter();

        Object response = null;
        ProceedingJoinPoint pjp = this.joinPoint;
        response = pjp.proceed();
        /*try
        {
            if (null != params && params.length > 0)
            {
                for (int i = 0; i < params.length; i++)
                {
                    Object param = params[i];
                    Class<?> pClass = paramTypes[i];
                    //只对AcBaseBean的子类进行进行校验
                    *//*if (AcBaseBean.class.isAssignableFrom(pClass))
                    {
                        VerifyUtil.validateParam(param);
                    }*//*
                }
            }
            response = pjp.proceed();
        }
        catch (IllegalArgumentException iae)
        {
            LOG.error("param illegal: {}", iae.getMessage());
            response = getMethodResponse(ErrorCode.ERROR_PARAMETER, iae);
        }
        catch (IllegalStateException ise)
        {
            LOG.error("internal state error: {}", ise.getMessage());
            response = getMethodResponse(ErrorCode.ERROR_INTERNAL, ise);
        }
        catch (DatabaseException dbe)
        {
            LOG.error("database execute error: {}", dbe.getMessage());
            response = getMethodResponse(ErrorCode.ERROR_DATABASE, dbe);
        }
        catch (BusinessException be)
        {
            LOG.error("business handle error: {}", be.getMessage());
            response = getMethodResponse(be.getError(), be);
        }
        catch (Throwable e)
        {
            logError(e);
            response = getMethodResponse(ErrorCode.ERROR_UNKNOWN, e);
        }
        finally
        {
            recordLog(response);
            logLeave(response);
        }
*/
        return response;
    }

    private void recordLog(Object returnObj)
    {
        try
        {
            if (null == params || params.length != 1)
            {
                LOG.debug("method not request response mode, cannot record log.");
                return;
            }

            /*if (!(param instanceof BaseRequest) || !(returnObj instanceof BaseResponse))
            {
                LOG.debug("method not request response mode, cannot record log.");
                return;
            }

            LogExecution le = getMethodAnnotation(LogExecution.class);
            if (null == le || !le.record())
            {
                LOG.debug("method log record closed.");
                return;
            }

            String type = le.value();
            if (StringUtils.isBlank(type))
            {
                LOG.debug("method log record value not set.");
                return;
            }

            BaseRequest request = getFirstParam();
            if (null == request.getAccessInfo())
            {
                LOG.debug("service call by internal, should not record log.");
                return;
            }

            OperateLogger logger = SpringContextHolder.getBean(OperateLogger.class);
            if (null == logger)
            {
                LOG.debug("method log record OperateLogger not implement.");
                return;
            }

            BaseResponse response = (BaseResponse)returnObj;
            TaskThreadPoolExecutor threadPool = SpringContextHolder.getBean(TaskThreadPoolExecutor.class);
            threadPool.putTask(() -> logger.log(type, request, response));
            LOG.debug("method log record put to task pool.");*/
        }
        catch (Exception e)
        {
            LOG.error("method log record execute fail.", e);
        }
    }

}

