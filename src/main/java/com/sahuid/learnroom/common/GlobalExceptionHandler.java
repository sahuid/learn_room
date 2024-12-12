package com.sahuid.learnroom.common;

import com.sahuid.learnroom.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RequestParamException.class)
    public R<?> requestParamExceptionHandler(RequestParamException e) {
        log.error("RequestParamException:请求参数错误");
        return R.fail(404, "请求参数错误");
    }

    @ExceptionHandler(DataBaseAbsentException.class)
    public R<?> dataBaseAbsentException(DataBaseAbsentException e) {
        log.error("DataBaseAbsentException:数据库不存在错误");
        return R.ok("", "数据不存在");
    }

    @ExceptionHandler(NoAuthException.class)
    public R<?> NoAuthException(NoAuthException e) {
        log.error("NoAuthException:当前用户没有权限");
        return R.fail(403, "当前用户没有权限");
    }

    @ExceptionHandler(NoLoginException.class)
    public R<?> NoLoginException(NoLoginException e) {
        log.error("NoLoginException:当前用户没有登陆");
        return R.fail(403, "当前用户没有登陆");
    }

    @ExceptionHandler(DataPresentException.class)
    public R<?> DataPresentException(DataPresentException e) {
        log.error("DataPresentException:数据已经存在");
        return R.ok("", "数据已经存在");
    }
}
