package io.middlesphere.federation.util;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */
public final class FastjsonObjectMapper {
    private  static  final Logger  LOGGER = LoggerFactory.getLogger(FastjsonObjectMapper.class);
    private FastjsonObjectMapper(){}

    /**
     *
     * @param object
     * @return
     * @param <T>
     */
    public static  <T> String  toJSONString(T object) {
        return JSON.toJSONString(object);
    }

}
