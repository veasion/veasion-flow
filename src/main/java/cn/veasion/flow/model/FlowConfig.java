package cn.veasion.flow.model;

import java.io.Serializable;

/**
 * FlowConfig
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowConfig implements Serializable {

	private static final long serialVersionUID = 1L;

    private String flow;
    private FlowNextNode startNode;
    private FlowNextNode errorNode;
    private FlowDefaultConfig defaultConfig;

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public FlowNextNode getStartNode() {
        return startNode;
    }

    public void setStartNode(FlowNextNode startNode) {
        this.startNode = startNode;
    }

    public FlowNextNode getErrorNode() {
        return errorNode;
    }

    public void setErrorNode(FlowNextNode errorNode) {
        this.errorNode = errorNode;
    }

    public FlowDefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(FlowDefaultConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
}
