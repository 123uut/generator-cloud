package com.lyh.maker.model.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.lyh.maker.generator.validator.MetaArgValidator;

/**
 * 单例模式创建元信息类，并加载json配置信息
 */
public class MetaInfoManager {

    private static volatile MetaInfo metaInfo;


    public static MetaInfo getSingleton(){
        if(metaInfo == null) {
            synchronized (MetaInfoManager.class){
                if(metaInfo == null){
                    metaInfo = initByJson();
                }
            }
        }
        return metaInfo;
    }

    private static MetaInfo initByJson(){
        String jsonString = ResourceUtil.readUtf8Str("springboot-init.json");
        MetaInfo bean = JSONUtil.toBean(jsonString, MetaInfo.class);
        MetaArgValidator.doValidAndFill(bean);
        return bean;
    }
}
