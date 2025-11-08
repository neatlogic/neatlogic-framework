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

package neatlogic.framework.filter;

import neatlogic.framework.asynchronization.queue.NeatLogicUniqueBlockingQueue;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.dao.mapper.UserSessionContentMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.UserSessionContentVo;
import neatlogic.framework.dto.UserSessionVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class InsertUserSessionThread {
    @Resource
    UserSessionMapper userSessionMapper;
    @Resource
    UserSessionContentMapper userSessionContentMapper;
    private static final Logger logger = LoggerFactory.getLogger(InsertUserSessionThread.class);
    private static final NeatLogicUniqueBlockingQueue<UserSessionVo> blockingQueue = new NeatLogicUniqueBlockingQueue<>(50000);

    @PostConstruct
    public void init() {
        Thread t = new Thread(new NeatLogicThread("INSERT-USER-SESSION-MANAGER") {
            @Override
            protected void execute() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UserSessionVo userSessionVo = blockingQueue.take();
                        userSessionMapper.insertUserSession(userSessionVo.getUserUuid(), userSessionVo.getTokenHash(), userSessionVo.getTokenCreateTime(), userSessionVo.getAuthInfoHash());
                        userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(userSessionVo.getTokenHash(), userSessionVo.getToken()));
                        if (StringUtils.isNotBlank(userSessionVo.getAuthInfoHash())) {
                            userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(userSessionVo.getAuthInfoHash(), userSessionVo.getAuthInfoStr()));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void addInsertUserSession(UserSessionVo userSessionVo) {
        blockingQueue.offer(userSessionVo);
    }

    public static int getSize(){
        return blockingQueue.size();
    }
}
