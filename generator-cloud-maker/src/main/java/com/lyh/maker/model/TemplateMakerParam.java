package com.lyh.maker.model;

import com.lyh.maker.filter.config.TemplateMakerFileConfig;
import com.lyh.maker.filter.config.TemplateMakerModelConfig;
import com.lyh.maker.filter.config.TemplateMakerOutputConfig;
import com.lyh.maker.model.meta.MetaInfo;
import lombok.Data;

@Data
public class TemplateMakerParam {

    private Long id;

    private MetaInfo meta = new MetaInfo();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

}




