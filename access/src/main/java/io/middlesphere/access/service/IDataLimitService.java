package io.middlesphere.access.service;

import io.middlesphere.access.datalimit.DataLimitDatabase;

/**
 * @author Administrator
 */
public interface IDataLimitService {

    /**
     * 根据用户ID查询数据权限
     * @param userId
     * @return
     */
    DataLimitDatabase  queryByUserId(String userId);
}
