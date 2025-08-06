package cn.ywenrou.shortlink.common.core.exception;


import cn.ywenrou.shortlink.common.core.enums.ErrorCodes;
import cn.ywenrou.shortlink.common.core.exception.base.BaseException;


/**
 * 客户端异常
 * @author xuxiaoyang
 * @since 2025-06-25
 */
public class ClientException extends BaseException {

    public ClientException(ErrorCodes errorCode) {
        super(errorCode);
    }
    public ClientException(String message, String errorCode) {
        super(message, errorCode);
    }
    public ClientException(String message){
        super(message, ErrorCodes.CLIENT_ERROR.code());
    }
    public ClientException(){
        super(ErrorCodes.CLIENT_ERROR);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
