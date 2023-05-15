
<%@page import="com.ulwx.tool.StringUtils" %>
<%@page import="java.io.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    File file = (File) request.getAttribute(SessionKey.DownloadKey);
    String fileName = (String) request.getAttribute(SessionKey.DownloadName);

    if (file != null) {
        InputStream in = null;
        OutputStream os = response.getOutputStream();
        try {
            response.setContentType("application/x-download");
            response.setContentLengthLong(file.length());
            if (StringUtils.isEmpty(fileName)) {
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "utf-8"));
            } else {
                response.setHeader("Content-disposition", "attachment; filename=" + new String(URLDecoder.decode(fileName, "utf-8").getBytes(), "ISO-8859-1"));
            }
            //Set-Cookie: fileDownload=true; path=/
            Cookie cookie = new Cookie("fileDownload", "true");
            cookie.setPath("/");
            response.addCookie(cookie);
            in = new BufferedInputStream(new FileInputStream(file));
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            in = null;
        }
    }
%>