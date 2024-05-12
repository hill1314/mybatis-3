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
package org.apache.ibatis.reflection;

/**
 * 反射器工厂 （为了复用创建的反射对象）
 *
 * @author huleilei9
 *
 * @date 2024/05/12
 */
public interface ReflectorFactory {

  /**
   * 是否启用了类缓存
   *
   * @return boolean
   */
  boolean isClassCacheEnabled();

  /**
   * 设置类缓存已启用
   *
   * @param classCacheEnabled
   *          类缓存已启用
   */
  void setClassCacheEnabled(boolean classCacheEnabled);

  /**
   * 根据类查找
   *
   * @param type
   *          类型
   *
   * @return {@link Reflector}
   */
  Reflector findForClass(Class<?> type);
}
