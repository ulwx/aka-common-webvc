package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.dbutils.spring.multids.GroupDecider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.*;


@ConfigurationProperties(AkaConst.AkaWebMvcPropertiesPrefx)
public class AkaWebMvcProperties implements InitializingBean {
    @NestedConfigurationProperty
    private Map<String, NameSpace> namespaces;
    @NestedConfigurationProperty
    private Map<String,String> globalViews;
    private String indexUrl;

    @NestedConfigurationProperty
    private DS dsConfig;

    public DS getDsConfig() {
        return dsConfig;
    }

    public void setDsConfig(DS dsConfig) {
        this.dsConfig = dsConfig;
    }

    public Map<String, NameSpace> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, NameSpace> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, String> getGlobalViews() {
        return globalViews;
    }

    public void setGlobalViews(Map<String, String> globalViews) {
        this.globalViews = globalViews;
    }


    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if(namespaces!=null){
            for(String key:namespaces.keySet()){
                namespaces.get(key).setName(key);
            }
        }
    }

    public static class NameSpace{
        private String name;
        private String packageName;
        private LinkedHashMap<String,Map<String,String>> urlMaps;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public LinkedHashMap<String, Map<String, String>> getUrlMaps() {
            return urlMaps;
        }

        public void setUrlMaps(LinkedHashMap<String, Map<String, String>> urlMaps) {
            this.urlMaps = urlMaps;
        }
    }

    public static class DebugFilter{
        private Boolean enable;
        private String username;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    public static class AccessFilter{
        private List<String> accessPlugins=new ArrayList<>();
        private LinkedHashSet notFilterUrls;

        public List<String> getAccessPlugins() {
            return accessPlugins;
        }

        public void setAccessPlugins(List<String> accessPlugins) {
            this.accessPlugins = accessPlugins;
        }

        public LinkedHashSet getNotFilterUrls() {
            return notFilterUrls;
        }

        public void setNotFilterUrls(LinkedHashSet notFilterUrls) {
            this.notFilterUrls = notFilterUrls;
        }
    }
    public static class ServiceImpl{
        private String logService;
        private String userInfoService;

        public String getLogService() {
            return logService;
        }

        public void setLogService(String logService) {
            this.logService = logService;
        }

        public String getUserInfoService() {
            return userInfoService;
        }

        public void setUserInfoService(String userInfoService) {
            this.userInfoService = userInfoService;
        }
    }


    public static class DS{
        private String deciderStrategy= DeciderStrategy.NONE;
        private String akaDynamicDataSourceName="NONE";
        private String groupLoadBalancer= GroupDecider.Random;

        public static class DeciderStrategy{
            public final static String PARENT_DIR_NAME="PARENT_DIR_NAME";
            public final static String NONE="NONE";
        }
        public String getDeciderStrategy() {
            return deciderStrategy;
        }

        public void setDeciderStrategy(String deciderStrategy) {
            this.deciderStrategy = deciderStrategy;
        }

        public String getAkaDynamicDataSourceName() {
            return akaDynamicDataSourceName;
        }

        public void setAkaDynamicDataSourceName(String akaDynamicDataSourceName) {
            this.akaDynamicDataSourceName = akaDynamicDataSourceName;
        }

        public String getGroupLoadBalancer() {
            return groupLoadBalancer;
        }

        public void setGroupLoadBalancer(String groupLoadBalancer) {
            this.groupLoadBalancer = groupLoadBalancer;
        }
    }


}
