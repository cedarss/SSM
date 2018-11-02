package com.xhkj.common.utils;


import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONUtil;

public class ObjectUtil {

    public static String mapToStringAll(Map<String, String[]> paramMap){

        if (paramMap == null) {
            return "";
        }
        Map<String, Object> params = new HashMap<>(16);
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {

            String key = param.getKey();
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.put(key, paramValue);
        }
        return JSONUtil.toJsonStr(params);
    }
}
