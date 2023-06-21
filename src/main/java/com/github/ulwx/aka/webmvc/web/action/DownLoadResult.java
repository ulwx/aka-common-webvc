package com.github.ulwx.aka.webmvc.web.action;

import java.io.File;

public class DownLoadResult implements Result{

    private File file;
    private String fileName;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ResultType getType() {
        return ResultType.download;
    }
}
