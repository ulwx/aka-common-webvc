package com.github.ulwx.aka.webmvc;

public interface AkaConst {
    public static  final String WebMvcComponetPackage="com.github.ulwx.aka";
    public static  final String WebContextConfigName="com.github.ulwx.aka.webmvc.WebContextConfiguration";
    public static  final String WebActionAspectJFilter="*..action..*Action";
    public static  final String[] ServiceAspectJFilter=new String[]{"*..services..service..*Service"
            ,"*..services..service..*ServiceImpl"};
    public static  final String[] DaoAspectJFilter=new String[]{"*..services..dao..*Dao",
            "*..services..dao..*DaoImpl"};
    public static final String AkaWebMvcPropertiesPrefx="aka.webvc";
}
