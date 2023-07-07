package neatlogic.module.framework.tenantinit;

import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceSchemaMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.tenantinit.TenantInitBase;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CreateDataWarehouseTableInitHandler  extends TenantInitBase {
    @Resource
    DataWarehouseDataSourceMapper dataSourceMapper;

    @Resource
    private DataWarehouseDataSourceSchemaMapper dataSourceSchemaMapper;

    @Override
    public String getName() {
        return "nmft.createdatawarehousetableinithandler.getname";
    }

    @Override
    public void execute() {
        List<DataSourceVo> datasourceVos = dataSourceMapper.getAllDataSource();
        for(DataSourceVo dataSourceVo : datasourceVos){
            dataSourceSchemaMapper.createDataSourceTable(dataSourceVo);
        }

    }

    @Override
    public int sort() {
        return 0;
    }
}
