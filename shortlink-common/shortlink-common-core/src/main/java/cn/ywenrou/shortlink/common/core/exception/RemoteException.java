package cn.ywenrou.shortlink.common.core.exception;


import cn.ywenrou.shortlink.common.core.enums.ErrorCodes;
import cn.ywenrou.shortlink.common.core.exception.base.BaseException;

/**
 * 远程服务调用异常
 * @author xuxiaoyang
 * @since 2025-06-25
 */
public class RemoteException extends BaseException {

    public RemoteException(ErrorCodes errorCode) {
        super(errorCode);
    }
    public RemoteException(String message, String errorCode) {
        super(message, errorCode);
    }
    public RemoteException(String message){
        super(message, ErrorCodes.REMOTE_ERROR.code());
    }
    public RemoteException(){
        super(ErrorCodes.REMOTE_ERROR);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}