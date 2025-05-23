package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.CTime;
import io.swagger.v3.oas.annotations.media.Schema;

public class CbResult<T> {

    @Schema( description = "请求id，全局唯一")
    private String requestId="";
    @Schema( description = "时间戳")
    private String timestamp= CTime.formatWholeAllDate();
    @Schema( description = "对应的请求路径")
    private String path="";
    //"状态，200表示成功， -100表示失败
    @Schema(name = "status", description = "状态码,1表示成功， 0表示失败")
    private Integer status = Status.SUC;
    @Schema(name = "error", description = "错误码，只有status=0时，error才有意义")
    private Integer error = ErrorCode.NO_ERROR;
    @Schema(name = "message", description = "提示性信息")
    private String message = "成功";
    @Schema(name = "src", description = "来源，由业务自定义")
    private String src = "";
    @Schema(name = "data", description = "承载的数据")
    private T data;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static CbResult of(int status, int errorCode, String message, Object data) {
        CbResult resultJson = new CbResult();
        resultJson.status = status;
        resultJson.error = errorCode;
        resultJson.message = message;
        resultJson.data = data;
        return resultJson;

    }

}
