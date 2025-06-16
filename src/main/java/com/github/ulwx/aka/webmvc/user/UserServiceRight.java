package com.github.ulwx.aka.webmvc.user;

public class UserServiceRight {
    private String serviceRightCode;
    private String serviceRightName;
    private Integer pageId;
    private String pageName;
    private String pageMatchURL;

    public String getServiceRightCode() {
        return serviceRightCode;
    }

    public void setServiceRightCode(String serviceRightCode) {
        this.serviceRightCode = serviceRightCode;
    }

    public String getServiceRightName() {
        return serviceRightName;
    }

    public void setServiceRightName(String serviceRightName) {
        this.serviceRightName = serviceRightName;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageMatchURL() {
        return pageMatchURL;
    }

    public void setPageMatchURL(String pageMatchURL) {
        this.pageMatchURL = pageMatchURL;
    }
}
