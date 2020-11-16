package codedriver.framework.lock.dao.mapper;

import codedriver.framework.dto.ConfigVo;

public interface LockMapper {

    public int insertLock(String id);

    public String getLockByIdForUpdate(String id);

    public String getLockById(String id);
}
