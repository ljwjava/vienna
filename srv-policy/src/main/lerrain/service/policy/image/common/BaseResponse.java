package lerrain.service.policy.image.common;

import java.io.Serializable;

import org.slf4j.helpers.MessageFormatter;

public class BaseResponse<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 176582589070298468L;

    private boolean           isSuccess        = false;

    private int               errorCode        = 0;

    private String            errorMsg         = "";

    private T                 result;

    public BaseResponse() {
        super();
    }

    public BaseResponse(T result) {
        super();
        this.isSuccess = true;
        this.result = result;
    }

    public BaseResponse<T> setSuccess(T result) {
        this.isSuccess = true;
        this.result = result;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setErrorMsg(String format, Object... arguments) {
        if (format == null || arguments == null) {
            this.errorMsg = format;
        } else {
            this.errorMsg = MessageFormatter.arrayFormat(format, arguments).getMessage();
        }
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean getIsSuccess() {
        return this.isSuccess;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return this.isSuccess + "," + this.errorMsg + "," + this.errorCode + "," + this.result;
    }

    public BaseResponse(boolean isSuccess, String errorMsg) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

}
