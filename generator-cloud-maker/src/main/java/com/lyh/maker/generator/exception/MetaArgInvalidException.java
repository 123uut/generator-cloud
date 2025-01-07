package com.lyh.maker.generator.exception;

/**
 * 元信息参数校验异常类
 */
public class MetaArgInvalidException extends RuntimeException{
        public MetaArgInvalidException(String message) {
            super(message);
        }

        public MetaArgInvalidException(String message, Throwable cause) {
            super(message, cause);
        }
}
