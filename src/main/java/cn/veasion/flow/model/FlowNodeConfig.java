package cn.veasion.flow.model;

/**
 * FlowNodeConfig
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowNodeConfig extends BaseBean {

	private static final long serialVersionUID = 1L;

    private String code; // 节点
    private String name; // 节点名称
    private Integer isVirtual; // 是否虚拟节点

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(Integer isVirtual) {
        this.isVirtual = isVirtual;
    }
}
