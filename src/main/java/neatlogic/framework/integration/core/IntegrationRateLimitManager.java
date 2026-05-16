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

package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.exception.integration.IntegrationRateLimitConfigInvalidException;
import neatlogic.framework.exception.integration.IntegrationRateLimitExceededException;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationRateLimitVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.Date;

@Service
@RootConfiguration
public class IntegrationRateLimitManager {

    private static IntegrationMapper integrationMapper;

    @Autowired
    public IntegrationRateLimitManager(IntegrationMapper _integrationMapper) {
        integrationMapper = _integrationMapper;
    }

    public static void acquire(IntegrationVo integrationVo) {
        if (integrationVo == null || integrationVo.getConfig() == null) {
            return;
        }
        JSONObject otherConfig = integrationVo.getConfig().getJSONObject("other");
        if (otherConfig == null) {
            return;
        }
        Integer interval = otherConfig.getInteger("rateLimitIntervalSeconds");
        Integer count = otherConfig.getInteger("rateLimitCount");
        if (isNegative(interval) || isNegative(count)) {
            throw new IntegrationRateLimitConfigInvalidException();
        }
        boolean hasInterval = isPositive(interval);
        boolean hasCount = isPositive(count);
        if (!hasInterval && !hasCount) {
            return;
        }
        if (!hasInterval || !hasCount) {
            throw new IntegrationRateLimitConfigInvalidException();
        }
        String integrationUuid = integrationVo.getUuidWithoutGenerate();
        if (StringUtils.isBlank(integrationUuid)) {
            return;
        }
        TransactionStatus transactionStatus = TransactionUtil.openTx();
        try {
            integrationMapper.insertIntegrationRateLimitIfAbsent(integrationUuid);
            IntegrationRateLimitVo rateLimitVo = integrationMapper.getIntegrationRateLimitForUpdate(integrationUuid);
            Date now = integrationMapper.getDatabaseCurrentTime();
            if (rateLimitVo == null || rateLimitVo.getWindowStartTime() == null || rateLimitVo.getCounter() == null) {
                integrationMapper.resetIntegrationRateLimit(integrationUuid, now);
                TransactionUtil.commitTx(transactionStatus);
                return;
            }
            long elapsed = now.getTime() - rateLimitVo.getWindowStartTime().getTime();
            if (elapsed >= interval * 1000L) {
                integrationMapper.resetIntegrationRateLimit(integrationUuid, now);
                TransactionUtil.commitTx(transactionStatus);
                return;
            }
            if (rateLimitVo.getCounter() >= count) {
                TransactionUtil.rollbackTx(transactionStatus);
                throw new IntegrationRateLimitExceededException(integrationVo.getName(), interval, count);
            }
            integrationMapper.increaseIntegrationRateLimitCounter(integrationUuid);
            TransactionUtil.commitTx(transactionStatus);
        } catch (IntegrationRateLimitExceededException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            TransactionUtil.rollbackTx(transactionStatus);
            throw ex;
        }
    }

    private static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    private static boolean isNegative(Integer value) {
        return value != null && value < 0;
    }
}
