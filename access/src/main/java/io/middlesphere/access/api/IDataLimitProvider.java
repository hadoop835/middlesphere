package io.middlesphere.access.api;

import io.middlesphere.access.datalimit.DataLimitDatabase;

import java.util.Map;

/**
 * @author Administrator
 */
public interface IDataLimitProvider {

    /**
     * 根据用户ID查询数据权限
     * @param userId
     * @return
     */
    DataLimitDatabase  queryByUserId(String userId);

    /**
     * 查询登录信息
     * @param userId
     * @return
     */
    Map<String,Object> getLoginInfoByUserId(String userId);


}
