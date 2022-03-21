package cn.veasion.flow.model;

/**
 * FlowRun
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowRun extends BaseBean {

	private static final long serialVersionUID = 1L;

    private String flow; // 流程
    private String flowCode; // 流程编码
    private String node; // 当前节点
    private String runData; // 运行数据
    private Integer status; // 流程状态：1正常，2结束，3暂停，4错误

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

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getRunData() {
        return runData;
    }

    public void setRunData(String runData) {
        this.runData = runData;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
