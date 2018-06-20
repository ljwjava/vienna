package lerrain.service.user.enm;


/**
 * 类RoleTypeEnum.java的实现描述：角色类型
 *
 *
 * @author iyb-wangguangrong 2018年5月10日 下午2:11:02
 */
public enum RoleTypeEnum {

    YUNFUENTERPRISE(1000L, "yunfu/enterprise", "云服渠道"),

    YUNFUORG(1001L, "yunfu/org", "云服部门"),

    YUNFUMEMBER(1002L, "yunfu/member", "云服子账号"),

    YUNFUBD(1003L, "yunfu/bd", "云服bd"),

    YUNFUSYSTEM(1004L, "yunfu/system", "云服管理员");
    
    private Long   id;
    private String code;

    private String name;

    private RoleTypeEnum(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static RoleTypeEnum getEnumByCode(String code) {
        for (RoleTypeEnum an : RoleTypeEnum.values()) {
            if (an.getCode().equals(code))
                return an;
        }
        return null;
    }

    public static RoleTypeEnum getEnumByName(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        for (RoleTypeEnum an : RoleTypeEnum.values()) {
            if (an.getName().equals(name)) {
                return an;
            }
        }
        throw new IllegalArgumentException("不支持的状态:" + name);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
