package io.middlesphere.access.interceptor;

import io.middlesphere.access.api.IDataLimitProvider;
import io.middlesphere.access.context.DataLimitContext;
import io.middlesphere.access.datalimit.DataLimitDatabase;
import io.middlesphere.access.service.IDataLimitService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 *
 * @author Administrator
 */
public class DataLimitInterceptor implements HandlerInterceptor {

    private IDataLimitService dataLimitService;

    public DataLimitInterceptor(IDataLimitService dataLimitService){
          this.dataLimitService = dataLimitService;
    }
    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        //根据组织查询
        //根据部门查询
        //根据创建人
        DataLimitDatabase dataLimitDatabase = this.dataLimitService.queryByUserId("1");
        if(ObjectUtils.isNotEmpty(dataLimitDatabase)){
            DataLimitContext.setDataLimitContext(dataLimitDatabase);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
         DataLimitContext.clear();
    }
}
