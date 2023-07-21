package com.github.ulwx.aka.webmvc;


public enum ProcessorStatus {
    Start(0),
    OnBeforeComplete(1),
    onActionComplete(2),
    OnAfterComplete(3),
    OnExceptionComplete(4),
    //处理异常时出现错误
    OnExceptionHasError(5);
    private int status;

    ProcessorStatus(int status){
        this.status=status;
    }

    public int value(){
        return status;
    }
    public static ProcessorStatus valueOf(int status){
        ProcessorStatus[] values= ProcessorStatus.values();
        for(int i=0; i<values.length; i++){
            if(values[i].status==status){
                return values[i];
            }
        }
        return null;
    }

}
