package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @Author: mcj
 * @Description: json文件导出
 * @DateTime: 2025/2/13 17:19
 **/
@Component
public class JsonExport extends AbstractFileExport{

    @Override
    protected List<Question> processData(List<Question> list) {
        System.out.println("处理文件内容");
        return list;
    }

    @Override
    protected List<Question> readFileContext(File file) {
        String jsonStr = FileUtil.readUtf8String(file);
        return JSONUtil.toBean(jsonStr, new TypeReference<List<Question>>() {
        }, false);
    }

    @Override
    public String supportFileType() {
        return FileTypeEnum.JSON.getType();
    }
}
