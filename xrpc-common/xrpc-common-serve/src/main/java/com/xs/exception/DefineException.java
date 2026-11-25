package com.xs.exception;


public class DefineException extends RuntimeException{

    private static final long serialVersionUID = -8925474679417692604L;

    protected   String  code;



    public DefineException(String code, String message, Throwable cause) {
        super(message,cause);
        this.code=code;
    }

    public DefineException(String code, String message) {
        super(message);
        this.code=code;
    }

    public DefineException(String code, Throwable cause) {
        super(cause);
        this.code=code;
    }

    public DefineException(String code) {
        super(code);
        this.code=code;
    }

    public DefineException() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
