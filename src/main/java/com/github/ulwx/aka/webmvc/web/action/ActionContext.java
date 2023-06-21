package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.*;
import org.apache.log4j.Logger;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionContext {
    private static Logger logger = Logger.getLogger(ActionContext.class);
    private static ActionContext instance = new ActionContext();

    public static ActionContext getContext() {
        return instance;
    }

    public HttpSession getSession() {
        return ServletActionContext.getSession();
    }

    public void put(String k, Object v) {
        ServletActionContext.getRequest().setAttribute(k, v);
    }

    public Object get(String k) {
        return ServletActionContext.getRequest().getAttribute(k);
    }

    //private static final ThreadLocal<RequestUtils> ruLocal = new ThreadLocal<>();
    private final static String ACTIONCONTEXT_REQUEST_UTILS="ACTIONCONTEXT_REQUEST_UTILS";

    public RequestUtils getRequestUtils(HttpServletRequest request) {
        RequestUtils requestUtils=(RequestUtils)request.getAttribute(ACTIONCONTEXT_REQUEST_UTILS);
        if (requestUtils != null) {
            return requestUtils;
        }
        RequestUtils ru = new RequestUtils();
        String reqContentType = StringUtils.trim(request.getContentType());
        String queryStr = request.getQueryString();
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

            MultiValueMap<String, MultipartFile> mm = mr.getMultiFileMap();
            for (String key : mm.keySet()) {
                List<MultipartFile> list = mm.get(key);
                List<File> listFile = new ArrayList<>();
                List<String> listName = new ArrayList<>();
                List<String> listContentType = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    MultipartFile mf = list.get(i);
                    File f = null;
                    String fname = mf.getOriginalFilename();
                    String fcty = mf.getContentType();
                    try {
                        String type=FileUtils.getTypePart(fname);
                        String fileName=FileUtils.getTempDirectory()+"/"+ RandomUtils.genUUID()+"."+type;
                        f=new File(fileName);
                        mf.transferTo(f);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    listFile.add(f);
                    listName.add(fname);
                    listContentType.add(fcty);
                }
                ru.setFiles(key,
                        listFile.toArray(new File[0]),
                        listName.toArray(new String[0]),
                        listContentType.toArray(new String[0]));
            }
        }


        ru.setRequestQueryStr(queryStr);
        if (reqContentType.contains("application/json")
                    ||reqContentType.contains("text/")) {
            try {
                String bodyStr = IOUtils.toString(request.getInputStream(), "utf-8", true);
                ru.setBody(bodyStr);
            } catch (Exception e) {
                logger.error("" + e, e);
            }
        }
        Map<String, String[]> paramaterMap=request.getParameterMap();
        ru.setRequestParamMap(paramaterMap);
        ru.putAll(paramaterMap);
        request.setAttribute(ACTIONCONTEXT_REQUEST_UTILS,ru);
        return ru;

    }
}
