/*
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.logging;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class LogFactory {

  /**
   * Marker to be used by logging implementations that support markers.
   */
  public static final String MARKER = "MYBATIS";

  private static final ReentrantLock lock = new ReentrantLock();
  /**
   * 日志构造函数
   */
  private static Constructor<? extends Log> logConstructor;

  static {
    // 初始化日志工厂
    // 在用户没有指定日志实现、或 指定的日志实现加载异常的情况下，使用下面的优先级 来创建日志实现
    // 优先级 从上到下，找到第一个可以成功加载的日志实现类，成功后 后面的不再加载
    tryImplementation(LogFactory::useSlf4jLogging);
    tryImplementation(LogFactory::useCommonsLogging);
    tryImplementation(LogFactory::useLog4J2Logging);
    tryImplementation(LogFactory::useLog4JLogging);
    tryImplementation(LogFactory::useJdkLogging);
    tryImplementation(LogFactory::useNoLogging);
  }

  private LogFactory() {
    // disable construction
  }

  public static Log getLog(Class<?> clazz) {
    return getLog(clazz.getName());
  }

  public static Log getLog(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }

  /**
   * 使用指定的日志实现
   *
   * @param clazz
   *          拍手
   */
  public static void useCustomLogging(Class<? extends Log> clazz) {
    setImplementation(clazz);
  }

  public static void useSlf4jLogging() {
    setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
  }

  public static void useCommonsLogging() {
    setImplementation(org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class);
  }

  /**
   * @deprecated Since 3.5.9 - See https://github.com/mybatis/mybatis-3/issues/1223. This method will remove future.
   */
  @Deprecated
  public static void useLog4JLogging() {
    setImplementation(org.apache.ibatis.logging.log4j.Log4jImpl.class);
  }

  public static void useLog4J2Logging() {
    setImplementation(org.apache.ibatis.logging.log4j2.Log4j2Impl.class);
  }

  public static void useJdkLogging() {
    setImplementation(org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class);
  }

  public static void useStdOutLogging() {
    setImplementation(org.apache.ibatis.logging.stdout.StdOutImpl.class);
  }

  public static void useNoLogging() {
    setImplementation(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
  }

  private static void tryImplementation(Runnable runnable) {
    // logConstructor 为空 才进行加载
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
        // 加载失败了，没关系，继续下一个
      }
    }
  }

  /**
   * 设置日志接口实现
   *
   * @param implClass
   *          实现类班
   */
  private static void setImplementation(Class<? extends Log> implClass) {
    lock.lock();
    try {
      Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
      Log log = candidate.newInstance(LogFactory.class.getName());
      if (log.isDebugEnabled()) {
        // 打印 具体实现类
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    } finally {
      lock.unlock();
    }
  }

}
