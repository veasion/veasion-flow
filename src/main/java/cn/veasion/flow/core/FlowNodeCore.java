package cn.veasion.flow.core;

import cn.veasion.flow.FlowManager;
import cn.veasion.flow.model.FlowConfig;
import cn.veasion.flow.model.FlowDefaultConfig;
import cn.veasion.flow.model.FlowNextConfig;
import cn.veasion.flow.model.FlowNextNode;
import cn.veasion.flow.model.FlowNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FlowNodeCore
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowNodeCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowNodeCore.class);

    private IFlowService flowService;
    private Map<String, FlowConfig> flowConfigMap;
    private Map<String, FlowNodeConfig> flowNodeConfigMap;
    private Map<String, List<FlowNextConfig>> flowNextConfigsMap;

    public FlowNodeCore(IFlowService flowService) {
        this.flowService = flowService;
    }

    public FlowConfig getFlowConfig(String flow) {
        if (flowConfigMap == null) {
            throw new FlowException("Flow Config Not loaded.");
        }
        return flowConfigMap.get(flow);
    }

    public FlowNextNode getNode(String flow, String node) {
        List<FlowNextNode> nodes = getNodes(flow, node);
        return nodes != null && nodes.size() > 0 ? nodes.get(0) : null;
    }

    public List<FlowNextNode> getNodes(String flow, String node) {
        if (!flowNodeConfigMap.containsKey(node)) {
            return null;
        }
        List<FlowNextConfig> flowNextConfigs = flowNextConfigsMap.get(getFlowNextKey(flow, node));
        if (flowNextConfigs == null || flowNextConfigs.isEmpty()) {
            return null;
        }
        List<FlowNextNode> list = new ArrayList<>(flowNextConfigs.size());
        for (FlowNextConfig flowNextConfig : flowNextConfigs) {
            list.add(buildFlowNextNode(flow, node, flowNextConfig));
        }
        return list;
    }

    public boolean isLoaded() {
        return flowConfigMap != null;
    }

    public synchronized void reload() {
        flowConfigMap = new HashMap<>();
        flowNextConfigsMap = new HashMap<>();
        List<FlowNodeConfig> flowNodeConfigs = flowService.queryFlowNodeConfig();
        List<FlowDefaultConfig> flowDefaultConfigs = flowService.queryFlowDefaultConfig();
        List<FlowNextConfig> flowNextConfigs = flowService.queryFlowNextConfig();
        flowNodeConfigMap = flowNodeConfigs.stream().collect(Collectors.toMap(FlowNodeConfig::getCode, o -> o, (v1, v2) -> v1));
        Map<String, FlowDefaultConfig> flowDefaultConfigMap = flowDefaultConfigs.stream().collect(Collectors.toMap(FlowDefaultConfig::getFlow, o -> o, (v1, v2) -> v1));
        for (FlowNextConfig flowNextConfig : flowNextConfigs) {
            String key = getFlowNextKey(flowNextConfig.getFlow(), flowNextConfig.getNode());
            List<FlowNextConfig> list = flowNextConfigsMap.getOrDefault(key, new ArrayList<>());
            list.add(flowNextConfig);
            flowNextConfigsMap.put(key, list);
        }
        for (FlowNextConfig flowNextConfig : flowNextConfigs) {
            String key = getFlowNextKey(flowNextConfig.getNextFlow(), flowNextConfig.getNextNode());
            if (!flowNextConfigsMap.containsKey(key)) {
                FlowNextConfig nextConfig = new FlowNextConfig();
                nextConfig.setFlow(flowNextConfig.getNextFlow());
                nextConfig.setNode(flowNextConfig.getNextNode());
                flowNextConfigsMap.put(key, Collections.singletonList(nextConfig));
            }
        }
        for (String flow : flowDefaultConfigMap.keySet()) {
            FlowDefaultConfig defaultConfig = flowDefaultConfigMap.get(flow);
            String startNode = defaultConfig.getStartNode();
            String errorNode = defaultConfig.getErrorNode();
            FlowConfig flowConfig = new FlowConfig();
            flowConfig.setFlow(flow);
            if (startNode != null && !"".equals(startNode)) {
                List<FlowNextConfig> startNodes = flowNextConfigsMap.get(getFlowNextKey(flow, startNode));
                if (startNodes == null || startNodes.isEmpty()) {
                    throw new FlowConfigException(String.format("flow: %s startNode: %s Not Found.", flow, startNode));
                } else if (startNodes.size() > 1) {
                    LOGGER.warn("{} 流程开始节点数量 {} > 1", flow, startNodes.size());
                }
                flowConfig.setStartNode(buildFlowNextNode(flow, startNode, startNodes.get(0)));
            }
            if (errorNode != null && !"".equals(errorNode)) {
                List<FlowNextConfig> errorNodes = flowNextConfigsMap.get(getFlowNextKey(flow, errorNode));
                if (!flowNodeConfigMap.containsKey(errorNode)) {
                    throw new FlowConfigException(String.format("flow: %s errorNode: %s Not Found.", flow, errorNode));
                }
                flowConfig.setErrorNode(buildFlowNextNode(flow, errorNode, errorNodes != null && errorNodes.size() > 0 ? errorNodes.get(0) : null));
            }
            flowConfig.setDefaultConfig(defaultConfig);
            flowConfigMap.put(flow, flowConfig);
        }
    }

    private FlowNextNode buildFlowNextNode(String flow, String node, FlowNextConfig flowNextConfig) {
        FlowNodeConfig nodeConfig = flowNodeConfigMap.get(node);
        if (nodeConfig == null) {
            return null;
        }
        if (flowNextConfig == null) {
            flowNextConfig = new FlowNextConfig();
            flowNextConfig.setNode(node);
            flowNextConfig.setFlow(flow);
        }
        FlowNextNode nextNode = new FlowNextNode();
        nextNode.setNode(nodeConfig);
        nextNode.setFlowNextConfig(flowNextConfig);
        if (!FlowManager.YES.equals(nodeConfig.getIsVirtual())) {
            nextNode.setFlowNode(flowService.getFlowNode(node));
        }
        return nextNode;
    }

    private String getFlowNextKey(String flow, String node) {
        return flow + ";" + node;
    }

}
