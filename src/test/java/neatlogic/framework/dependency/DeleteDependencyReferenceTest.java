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

package neatlogic.framework.dependency;

import neatlogic.framework.dao.plugin.CompressHandler;
import neatlogic.framework.dependency.dao.mapper.DependencyMapper;
import neatlogic.framework.dependency.dto.DependencyVo;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** 检查真实 MyBatis 映射，避免单条解除误用按下游清理的历史语义。 */
public class DeleteDependencyReferenceTest {
    /** 上下游可以复用相同的 ID，精确删除必须同时约束类型以及两个端点。 */
    @Test
    public void exactDeleteUsesAllThreePartsOfTheReference() throws Exception {
        Configuration configuration = new Configuration();
        configuration.getTypeAliasRegistry().registerAlias("CompressHandler", CompressHandler.class);
        String path = "neatlogic/framework/dependency/dao/mapper/DependencyMapper.xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(stream);
            new XMLMapperBuilder(stream, configuration, path, configuration.getSqlFragments()).parse();
        }
        DependencyVo relation = new DependencyVo("handler", "document", "origin");
        BoundSql bound = configuration.getMappedStatement(DependencyMapper.class.getName() + ".deleteDependencyReference").getBoundSql(relation);
        assertEquals("DELETE FROM `dependency` WHERE `type` = ? AND `from` = ? AND `to` = ?",
                bound.getSql().replaceAll("\\s+", " ").trim());
        assertEquals(Arrays.asList("type", "from", "to"), bound.getParameterMappings().stream()
                .map(ParameterMapping::getProperty).collect(Collectors.toList()));
    }
}
