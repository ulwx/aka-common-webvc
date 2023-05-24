package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.IOUtils;
import com.ulwx.tool.RequestUtils;
import com.ulwx.tool.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String, Object[]> ruParms = new HashMap<>();
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
                    try {
                        f = mf.getResource().getFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String fname = mf.getOriginalFilename();
                    String fcty = mf.getContentType();
                    listFile.add(f);
                    listName.add(fname);
                    listContentType.add(fcty);
                }

                ruParms.put(key, listFile.toArray(new File[0]));
                ruParms.put(key + "FileName", listName.toArray(new String[0]));
                ruParms.put(key + "ContentType", listContentType.toArray(new String[0]));
            }
        }

        RequestUtils ru = new RequestUtils(ruParms);
        if (reqContentType.contains("application/json")) {
            try {
                String bodyStr = IOUtils.toString(request.getInputStream(), "utf-8", true);
                ru.setString(RequestUtils.REQUEST_BODY_STR, bodyStr);
            } catch (Exception e) {
                logger.error("" + e, e);
            }
        }
        Map<String, String[]> paramaterMap=request.getParameterMap();
        ru.setRequestParamMap(paramaterMap);
        ruParms.putAll(paramaterMap);
        request.setAttribute(ACTIONCONTEXT_REQUEST_UTILS,ru);
        return ru;

    }
}
