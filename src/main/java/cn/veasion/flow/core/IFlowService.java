package cn.veasion.flow.core;

import cn.veasion.flow.IFlowNode;
import cn.veasion.flow.model.FlowDefaultConfig;
import cn.veasion.flow.model.FlowNextConfig;
import cn.veasion.flow.model.FlowNodeConfig;
import cn.veasion.flow.model.FlowRun;
import cn.veasion.flow.model.FlowRunTrack;

import java.util.List;

/**
 * IFlowService
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public interface IFlowService {

    List<FlowDefaultConfig> queryFlowDefaultConfig();

    List<FlowNextConfig> queryFlowNextConfig();

    List<FlowNodeConfig> queryFlowNodeConfig();

    FlowRun queryFlowRun(String flow, String flowCode);

    List<FlowRunTrack> queryFlowRunTrack(String flow, String flowCode);

    void saveFlowRun(FlowRun flowRun);

    void updateFlowRun(FlowRun flowRun);

    void saveFlowRunTrack(FlowRunTrack flowRunTrack);

    IFlowNode getFlowNode(String code);

}
