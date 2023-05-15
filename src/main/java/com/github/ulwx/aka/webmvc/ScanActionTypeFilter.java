package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

public class ScanActionTypeFilter implements TypeFilter {

    private ResourceLoader resourceLoader;
    private AssignableTypeFilter actionFilter;
    private AspectJTypeFilter aspectActionFilter;
    public ScanActionTypeFilter(ResourceLoader resourceLoader){
        this.resourceLoader=resourceLoader;
        actionFilter=new AssignableTypeFilter(ActionSupport.class);
        aspectActionFilter= new AspectJTypeFilter(AkaConst.WebActionAspectJFilter, this.resourceLoader.getClassLoader());

    }
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

        if(actionFilter.match(metadataReader,metadataReaderFactory)
                && aspectActionFilter.match(metadataReader,metadataReaderFactory)) {

            return true;
        }

        return false;
    }
}
