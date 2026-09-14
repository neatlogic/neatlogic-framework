/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.globallock;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.globallock.dao.mapper.GlobalLockOperationMapper;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import java.lang.reflect.*;
import java.util.*;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.globallock.constvalue.GlobalLockOperationStatus;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/** 使用隔离数据库验证用户绑定、单次执行权及进度提交。 */
public class GlobalLockOperationManagerTest {
    private GlobalLockOperationManager manager;
    private JdbcTemplate jdbc;
    private GlobalLockI18nFixture i18n;
    @Before public void setup() throws Exception {
        i18n = new GlobalLockI18nFixture();
        UserContext.init((UserContext)null).setUserUuid("tester");
        JdbcDataSource source=new JdbcDataSource();source.setURL("jdbc:h2:mem:"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1");
        jdbc=new JdbcTemplate(source);new TransactionUtil(new DataSourceTransactionManager(source));
        jdbc.execute("CREATE TABLE operation(id VARCHAR PRIMARY KEY,owner VARCHAR,lock_id BIGINT,action VARCHAR,state VARCHAR,content VARCHAR)");
        GlobalLockOperationMapper mapper=(GlobalLockOperationMapper)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{GlobalLockOperationMapper.class},(proxy,method,args)->{
            switch(method.getName()) {
                case "purge": return null;
                case "insert": jdbc.update("INSERT INTO operation VALUES(?,?,?,?,'prepared',?)",args[0],args[1],args[2],args[3],args[4]); return null;
                case "get":
                    List<Map<String,Object>> list=jdbc.query("SELECT * FROM operation WHERE id=? AND owner=?",(rs,n)->{
                        Map<String,Object> row=new HashMap<>(); row.put("content",rs.getString("content"));row.put("state",rs.getString("state"));row.put("lockId",rs.getLong("lock_id"));row.put("action",rs.getString("action"));row.put("updatedAt",System.currentTimeMillis());return row;
                    },args[0],args[1]); return list.isEmpty()?null:list.get(0);
                case "claim": return jdbc.update("UPDATE operation SET state='running' WHERE id=? AND owner=? AND lock_id=? AND action=? AND state='prepared'",args[0],args[1],args[2],args[3]);
                case "update": jdbc.update("UPDATE operation SET state=?,content=? WHERE id=?",((GlobalLockOperationStatus)args[1]).getValue(),args[2],args[0]);return null;
                default: return null;
            }
        });
        manager=new GlobalLockOperationManager();Field f=manager.getClass().getDeclaredField("mapper");f.setAccessible(true);f.set(manager,mapper);
    }
    @After public void cleanup() throws Exception {UserContext.get().release(); if (i18n != null) i18n.close();}
    @Test public void operationCanOnlyBeClaimedOnceAndIsUserBound(){
        String id=manager.prepare(123L,"unlock").getString("operationId");
        assertTrue(manager.claim(id,123L,"unlock"));assertFalse(manager.claim(id,123L,"unlock"));
        UserContext.get().setUserUuid("another");assertNull(manager.get(id));
    }
    /** 区分当前用户的绑定冲突与不可访问记录，并保留请求标识。 */
    @Test public void invalidOperationExplainsBindingAndAvailability() {
        String id = manager.prepare(123L, "unlock").getString("operationId");
        try { manager.claim(id, 456L, "cancel"); fail("expected binding mismatch"); }
        catch (ParamIrregularException ex) {
            LoggerFactory.getLogger(getClass()).error("Expected operation binding rejection", ex);
            for (String value : new String[]{id, "123", "456", "unlock", "cancel", "不匹配"}) assertTrue(ex.getMessage(), ex.getMessage().contains(value));
        }
        UserContext.get().setUserUuid("another");
        try { manager.claim(id, 456L, "cancel"); fail("expected unavailable operation"); }
        catch (ParamIrregularException ex) {
            LoggerFactory.getLogger(getClass()).error("Expected unavailable operation rejection", ex);
            assertTrue(ex.getMessage().contains(id)); assertTrue(ex.getMessage().contains("无权访问"));
            assertFalse(ex.getMessage().contains("123"));
        }
    }
    @Test public void releaseProgressRollsBackWithItsTransaction(){
        JSONObject data=manager.prepare(123L,"unlock");String id=data.getString("operationId");manager.claim(id,123L,"unlock");
        TransactionStatus tx=TransactionUtil.openTx();data.put("releaseStatus","released");manager.committedRelease(data);TransactionUtil.rollbackTx(tx);
        assertEquals("pending",manager.get(id).getString("releaseStatus"));
        tx=TransactionUtil.openTx();manager.committedRelease(data);TransactionUtil.commitTx(tx);
        assertEquals("released",manager.get(id).getString("releaseStatus"));
    }
    /** 同一持久化进度按查询语言翻译，不能保存上一次请求的文案。 */
    @Test public void progressTextUsesQueryLanguageWithoutPersistingTranslation() {
        JSONObject progress = manager.prepare(123L, "unlock");
        String id = progress.getString("operationId");
        assertEquals("等待释放", progress.getString("releaseStatusText"));
        manager.save(progress, GlobalLockOperationStatus.PREPARED);
        assertFalse(jdbc.queryForObject("SELECT content FROM operation WHERE id=?", String.class, id).contains("releaseStatusText"));
        Locale.setDefault(Locale.ENGLISH);
        assertEquals("Pending Release", manager.get(id).getString("releaseStatusText"));
        Locale.setDefault(Locale.CHINESE);
        assertEquals("等待释放", manager.get(id).getString("releaseStatusText"));
    }
}
