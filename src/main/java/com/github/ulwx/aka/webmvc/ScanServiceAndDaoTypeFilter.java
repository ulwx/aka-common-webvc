package com.github.ulwx.aka.webmvc;

import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

public class ScanServiceAndDaoTypeFilter implements TypeFilter {

    private  ResourceLoader resourceLoader;
    private AssignableTypeFilter akaDBSupportFilter;
    private AssignableTypeFilter akaServiceSupportFilter;
    private AspectJTypeFilter[] aspectDaoFilter;
    private AspectJTypeFilter[] aspectServiceFilter;

    public ScanServiceAndDaoTypeFilter(ResourceLoader resourceLoader){
        this.resourceLoader=resourceLoader;
        akaDBSupportFilter=new AssignableTypeFilter(AkaDaoSupport.class);
        akaServiceSupportFilter=new AssignableTypeFilter(AkaServiceSupport.class);
        aspectDaoFilter=new AspectJTypeFilter[AkaConst.DaoAspectJFilter.length];
        aspectServiceFilter=new AspectJTypeFilter[AkaConst.ServiceAspectJFilter.length];

        for(int i=0; i<AkaConst.DaoAspectJFilter.length; i++){
            aspectDaoFilter[i]=new AspectJTypeFilter(AkaConst.DaoAspectJFilter[i],this.resourceLoader.getClassLoader());
        }
        for(int i=0; i<AkaConst.DaoAspectJFilter.length; i++){
            aspectServiceFilter[i]=new AspectJTypeFilter(AkaConst.ServiceAspectJFilter[i],this.resourceLoader.getClassLoader());
        }
    }
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

        if(akaDBSupportFilter.match(metadataReader, metadataReaderFactory)) {
            for (int i = 0; i < aspectDaoFilter.length; i++) {
                if (aspectDaoFilter[i].match(metadataReader, metadataReaderFactory)) {
                    return true;
                }
            }
        }
        if(akaServiceSupportFilter.match(metadataReader, metadataReaderFactory)) {
            for (int i = 0; i < aspectServiceFilter.length; i++) {
                if (aspectServiceFilter[i].match(metadataReader, metadataReaderFactory)) {
                    return true;
                }
            }
        }

        return false;
    }
}
