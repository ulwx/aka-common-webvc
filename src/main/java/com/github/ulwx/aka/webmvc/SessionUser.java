package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.user.*;

import java.util.List;
import java.util.Map;

public interface SessionUser {

    public User getUser() ;
    public String getUserName();
    public String getAccount() ;
    public String getPhoneNumber();
    public void setUser(User user);

    public UserRole[] getRoles() ;
    public void setRoles(UserRole[] roles);
    public Map<Integer, RoleType> getRoleTypesMap() ;
    public void setRoleTypesMap(Map<Integer, RoleType> roleTypesMap) ;

    public Map<Integer, RoleTypeClass> getRoleTypeClassMap() ;
    public void setRoleTypeClassMap(Map<Integer, RoleTypeClass> roleTypeClassMap);
    public List<UserRight> getRights();
    public void setRights(List<UserRight> rights) ;
    public List<UserServiceRight> getServiceRightList() ;
    public void setServiceRightList(List<UserServiceRight> serviceRightList) ;
    public Object getExtInfo() ;
    public void setExtInfo(Object extInfo) ;
    public boolean isSuperAdmin() ;
    public void setSuperAdmin(boolean superAdmin);
}
