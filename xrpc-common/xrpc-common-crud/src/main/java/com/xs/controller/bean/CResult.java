package com.xs.controller.bean;

public class CResult {

    //返回数据json格式封装
    private Object code = 200 ;//状态码
    private String message = "";//消息
    private Object result;//数据对象


    private Long total;//数据总数

    public CResult( ) {
    }

    public CResult(Object result, Long total){
        this.result = result;
        this.total = total;
    }
    public CResult(Object result) {
        this.result = result;
    }

    public CResult(Integer code) {
        this.setCode(code);
        if(code==999){
            this.message = "操作失败";
        }

    }

    public CResult(Integer code, String message) {
        this.setCode(code);
        this.message = message;
    }

    public CResult(Integer code, String message, Object result) {
        this.setCode(code);
        this.message = message;
        this.result = result;
    }
    public Long getTotal() { return total; }

    public void setTotal(Long total) { this.total = total; }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


}
