/*
 *    Copyright 2009-2023 the original author or authors.
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
package org.apache.ibatis.logging.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * Connection proxy to add logging.
 * 数据库链接 日志代理
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @date 2024/05/12
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

  private final Connection connection;

  private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
    super(statementLog, queryStack);
    this.connection = conn;
  }

  /**
   * 反射执行代理对象的方法
   *
   * @param proxy  代理
   * @param method 方法
   * @param params params
   * @return {@link Object}
   * @throws Throwable 可丢弃
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
    try {
      //如果是Object 继承来的方法，不用处理
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, params);
      }

      if ("prepareStatement".equals(method.getName()) || "prepareCall".equals(method.getName())) {
        if (isDebugEnabled()) {
          debug(" Preparing: " + removeExtraWhitespace((String) params[0]), true);
        }
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);

        //返回的是 PreparedStatement 的日志代理对象
        return PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
      }

      if ("createStatement".equals(method.getName())) {
        Statement stmt = (Statement) method.invoke(connection, params);
        //返回的是 Statement 的日志代理对象
        return StatementLogger.newInstance(stmt, statementLog, queryStack);
      }

      return method.invoke(connection, params);
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a connection.
   *
   * @param conn
   *          the original connection
   * @param statementLog
   *          the statement log
   * @param queryStack
   *          the query stack
   *
   * @return the connection with logging
   */
  public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
    InvocationHandler handler = new ConnectionLogger(conn, statementLog, queryStack);
    ClassLoader cl = Connection.class.getClassLoader();
    return (Connection) Proxy.newProxyInstance(cl, new Class[] { Connection.class }, handler);
  }

  /**
   * return the wrapped connection.
   *
   * @return the connection
   */
  public Connection getConnection() {
    return connection;
  }

}
