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
package org.apache.ibatis.mapping;

/**
 * 语句类型
 *
 * @author Clinton Begin
 *
 * @date 2024/05/12
 */
public enum StatementType {

  /**
   * 基本类型，用于执行不带参数的SQL语句
   */
  STATEMENT,

  /**
   * 用于执行带参数的SQL语句。 使用PreparedStatement可以防止SQL注入攻击，并且可以提高性能，因为它允许数据库对SQL语句进行预编译
   */
  PREPARED,

  /**
   * 存储过程
   */
  CALLABLE

}
