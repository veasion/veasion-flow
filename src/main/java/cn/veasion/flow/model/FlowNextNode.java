package cn.veasion.flow.model;

import cn.veasion.flow.FlowUtils;
import cn.veasion.flow.IFlowNode;

import java.io.Serializable;

/**
 * FlowNextNode
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowNextNode implements Serializable {

	private static final long serialVersionUID = 1L;

    private FlowNodeConfig node;
    private IFlowNode flowNode;
    private FlowNextConfig flowNextConfig;

    public boolean hasNext() {
        return FlowUtils.hasText(flowNextConfig.getNextFlow()) && FlowUtils.hasText(flowNextConfig.getNextNode());
    }

    public FlowNodeConfig getNode() {
        return node;
    }

    public void setNode(FlowNodeConfig node) {
        this.node = node;
    }

    public IFlowNode getFlowNode() {
        return flowNode;
    }

    public void setFlowNode(IFlowNode flowNode) {
        this.flowNode = flowNode;
    }

    public FlowNextConfig getFlowNextConfig() {
        return flowNextConfig;
    }

    public void setFlowNextConfig(FlowNextConfig flowNextConfig) {
        this.flowNextConfig = flowNextConfig;
    }
}
