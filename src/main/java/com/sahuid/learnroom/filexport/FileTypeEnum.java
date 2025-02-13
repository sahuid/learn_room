package com.sahuid.learnroom.filexport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: mcj
 * @Description: 文件类型枚举
 * @DateTime: 2025/2/13 18:15
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {
    JSON("json"),
    CSV("csv")
    ;

    private String type;
}
