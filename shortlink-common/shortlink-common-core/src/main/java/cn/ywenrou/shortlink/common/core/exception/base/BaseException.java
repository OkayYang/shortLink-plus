package cn.ywenrou.shortlink.common.core.exception.base;

import cn.ywenrou.shortlink.common.core.enums.ErrorCodes;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.common.core.exception.RemoteException;
import cn.ywenrou.shortlink.common.core.exception.ServiceException;
import lombok.Getter;

/**
 * 抽象异常类,
 * @author xuxiaoyang
 * @since 2025-06-25
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public class BaseException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public BaseException(String errorMessage, String errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    public BaseException(ErrorCodes errorCode) {
        this.errorCode = errorCode.code();
        this.errorMessage = errorCode.message();
    }
}
