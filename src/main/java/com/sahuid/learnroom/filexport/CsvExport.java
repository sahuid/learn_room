package com.sahuid.learnroom.filexport;

import com.sahuid.learnroom.model.entity.Question;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @Author: mcj
 * @Description: csv 文件导出
 * @DateTime: 2025/2/13 17:33
 **/
@Component
public class CsvExport extends AbstractFileExport{

    @Override
    protected List<Question> processData(List<Question> list) {
        System.out.println("处理 csv 文件");
        return null;
    }

    @Override
    protected List<Question> readFileContext(File file) {
        System.out.println("读取 csv 文件");
        return null;
    }

    @Override
    public String supportFileType() {
        return FileTypeEnum.CSV.getType();
    }
}
