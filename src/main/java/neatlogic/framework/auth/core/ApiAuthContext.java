/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.restful.annotation.AuthUser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * 保存当前线程同步 API 执行链中的系统用户鉴权状态。
 * 普通 ThreadLocal 不会随 NeatLogicThread 复制，避免接口豁免扩散到异步任务。
 */
public final class ApiAuthContext {
    private static final ThreadLocal<Deque<AuthState>> AUTH_STATE_STACK = new ThreadLocal<>();

    /**
     * 工具类不允许实例化。
     */
    private ApiAuthContext() {
    }

    /**
     * 进入一个 API 执行作用域，并根据目标接口的 AuthUser 注解计算当前系统用户是否豁免。
     *
     * @param apiClass 实际 API 类型
     */
    public static void enter(Class<?> apiClass) {
        UserContext userContext = UserContext.get();
        String systemUserUuid = null;
        boolean isExempt = false;
        if (userContext != null && SystemUserFactory.getSystemUserByUser(userContext.getUserUuid()) != null) {
            systemUserUuid = userContext.getUserUuid();
            if (apiClass != null) {
                AuthUser[] authUsers = apiClass.getAnnotationsByType(AuthUser.class);
                for (AuthUser authUser : authUsers) {
                    if (Objects.equals(authUser.value().getUserUuid(), systemUserUuid)) {
                        isExempt = true;
                        break;
                    }
                }
            }
        }
        Deque<AuthState> authStateStack = AUTH_STATE_STACK.get();
        if (authStateStack == null) {
            authStateStack = new ArrayDeque<>();
            AUTH_STATE_STACK.set(authStateStack);
        }
        authStateStack.push(new AuthState(systemUserUuid, isExempt));
    }

    /**
     * 退出当前 API 执行作用域，并恢复嵌套调用前的鉴权状态。
     */
    public static void exit() {
        Deque<AuthState> authStateStack = AUTH_STATE_STACK.get();
        if (authStateStack == null || authStateStack.isEmpty()) {
            return;
        }
        authStateStack.pop();
        if (authStateStack.isEmpty()) {
            AUTH_STATE_STACK.remove();
        }
    }

    /**
     * 清除当前线程的全部 API 鉴权状态。
     * 用于请求结束时兜底清理，正常的嵌套 API 返回仍应调用 exit() 恢复外层状态。
     */
    public static void release() {
        AUTH_STATE_STACK.remove();
    }

    /**
     * 判断指定系统用户在当前 API 作用域中是否允许跳过权限校验。
     * 非 API 场景保持系统用户原有放行行为，普通用户的接口作用域不改变显式用户校验语义。
     *
     * @param userUuid 待校验用户 UUID
     * @return 是否允许按系统用户身份直接放行
     */
    public static boolean shouldBypassSystemUserAuth(String userUuid) {
        if (SystemUserFactory.getSystemUserByUser(userUuid) == null) {
            return false;
        }
        Deque<AuthState> authStateStack = AUTH_STATE_STACK.get();
        if (authStateStack == null || authStateStack.isEmpty()) {
            return true;
        }
        AuthState authState = authStateStack.peek();
        if (authState.systemUserUuid == null) {
            return true;
        }
        return authState.isExempt && Objects.equals(authState.systemUserUuid, userUuid);
    }

    /**
     * 判断当前系统用户是否已被当前接口显式豁免。
     *
     * @return 当前接口是否豁免当前系统用户
     */
    public static boolean isCurrentSystemUserExempt() {
        Deque<AuthState> authStateStack = AUTH_STATE_STACK.get();
        if (authStateStack == null || authStateStack.isEmpty()) {
            return false;
        }
        AuthState authState = authStateStack.peek();
        return authState.systemUserUuid != null && authState.isExempt;
    }

    /**
     * 单层 API 调用对应的系统用户及豁免结果。
     */
    private static class AuthState {
        private final String systemUserUuid;
        private final boolean isExempt;

        /**
         * 创建单层 API 调用状态。
         */
        private AuthState(String systemUserUuid, boolean isExempt) {
            this.systemUserUuid = systemUserUuid;
            this.isExempt = isExempt;
        }
    }
}
