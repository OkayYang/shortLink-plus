package cn.ywenrou.shortlink.common.core.exception;


import cn.ywenrou.shortlink.common.core.enums.ErrorCodes;
import cn.ywenrou.shortlink.common.core.exception.base.BaseException;



/**
 * 服务端异常
 * @author xuxiaoyang
 * @since 2025-06-25
 */
public class ServiceException extends BaseException {

    public ServiceException(ErrorCodes errorCode) {
        super(errorCode);
    }
    public ServiceException(String message, String errorCode) {
        super(message, errorCode);
    }
    public ServiceException(String message){
        super(message,ErrorCodes.SERVICE_ERROR.code() );
    }
    public ServiceException(){
        super(ErrorCodes.SERVICE_ERROR);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

