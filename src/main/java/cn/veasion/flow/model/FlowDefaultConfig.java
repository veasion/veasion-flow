package cn.veasion.flow.model;

/**
 * FlowDefaultConfig
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowDefaultConfig extends BaseBean {

	private static final long serialVersionUID = 1L;

    private String flow; // 流程
    private String startNode; // 开始节点
    private String errorNode; // 错误节点

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public String getErrorNode() {
        return errorNode;
    }

    public void setErrorNode(String errorNode) {
        this.errorNode = errorNode;
    }
}
