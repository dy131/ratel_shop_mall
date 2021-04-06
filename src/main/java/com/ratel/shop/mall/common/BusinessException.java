
package com.ratel.shop.mall.common;

public class BusinessException extends RuntimeException {

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public static void fail(String message) {
        throw new BusinessException(message);
    }

}
