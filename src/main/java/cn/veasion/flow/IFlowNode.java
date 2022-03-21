package cn.veasion.flow;

/**
 * 流程节点接口
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public interface IFlowNode {

    void onFlow(FlowContext context) throws Exception;

    String getCode();

}
