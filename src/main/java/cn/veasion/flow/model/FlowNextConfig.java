package cn.veasion.flow.model;

/**
 * FlowNextConfig
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowNextConfig extends BaseBean {

	private static final long serialVersionUID = 1L;

    private String flow; // 流程
    private String node; // 节点
    private String nextFlow; // 下一个流程
    private String nextNode; // 下一个流程节点
    private String cond; // 条件（脚本）
    private String onBefore;  // 节点前执行（脚本）
    private String onAfter;  // 节点后执行（脚本）

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getNextFlow() {
        return nextFlow;
    }

    public void setNextFlow(String nextFlow) {
        this.nextFlow = nextFlow;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getOnBefore() {
        return onBefore;
    }

    public void setOnBefore(String onBefore) {
        this.onBefore = onBefore;
    }

    public String getOnAfter() {
        return onAfter;
    }

    public void setOnAfter(String onAfter) {
        this.onAfter = onAfter;
    }
}
