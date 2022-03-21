package cn.veasion.flow;

/**
 * FlowIn
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowIn {

    private String flow; // 流程
    private String flowCode; // 流程编码
    private boolean depends; // 运行依赖流程
    private boolean basedLastRun; // 基于上一次运行

    public FlowIn(String flow, String flowCode) {
        this(flow, flowCode, true, true);
    }

    public FlowIn(String flow, String flowCode, boolean depends, boolean basedLastRun) {
        this.flow = flow;
        this.flowCode = flowCode;
        this.depends = depends;
        this.basedLastRun = basedLastRun;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public boolean isDepends() {
        return depends;
    }

    public void setDepends(boolean depends) {
        this.depends = depends;
    }

    public boolean isBasedLastRun() {
        return basedLastRun;
    }

    public void setBasedLastRun(boolean basedLastRun) {
        this.basedLastRun = basedLastRun;
    }
}
