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
        log.error("RequestParamException:" + e.getMessage());
        return R.fail(404, e.getMessage());
    }

    @ExceptionHandler(DataBaseAbsentException.class)
    public R<?> dataBaseAbsentException(DataBaseAbsentException e) {
        log.error("DataBaseAbsentException:" + e.getMessage());
        return R.ok("", e.getMessage());
    }

    @ExceptionHandler(NoAuthException.class)
    public R<?> NoAuthException(NoAuthException e) {
        log.error("NoAuthException:" + e.getMessage());
        return R.fail(403, e.getMessage());
    }

    @ExceptionHandler(NoLoginException.class)
    public R<?> NoLoginException(NoLoginException e) {
        log.error("NoLoginException:" + e.getMessage());
        return R.fail(403, e.getMessage());
    }

    @ExceptionHandler(DataPresentException.class)
    public R<?> DataPresentException(DataPresentException e) {
        log.error("DataPresentException:" + e.getMessage());
        return R.ok("", e.getMessage());
    }

    @ExceptionHandler(DataOperationException.class)
    public R<?> DataOperationException(DataOperationException e) {
        log.error("DataOperationException:" + e.getMessage());
        return R.fail(500, e.getMessage());
    }
}
