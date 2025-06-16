package com.github.ulwx.aka.webmvc.user;

public class RoleType {
    //角色代码
    private Integer code;
    private String name;
    //一般对应部门的编码
    private Integer roleTypeClassCode;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleTypeClassCode() {
        return roleTypeClassCode;
    }

    public void setRoleTypeClassCode(Integer roleTypeClassCode) {
        this.roleTypeClassCode = roleTypeClassCode;
    }
}
