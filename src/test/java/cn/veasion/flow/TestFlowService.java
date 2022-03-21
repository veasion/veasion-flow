package cn.veasion.flow;

import cn.veasion.flow.core.IFlowService;
import cn.veasion.flow.model.BaseBean;
import cn.veasion.flow.model.FlowDefaultConfig;
import cn.veasion.flow.model.FlowNextConfig;
import cn.veasion.flow.model.FlowNodeConfig;
import cn.veasion.flow.model.FlowRun;
import cn.veasion.flow.model.FlowRunTrack;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TestFlowService
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class TestFlowService implements IFlowService {

    private static final String CONFIG_PATH = "/data.json";

    private static final JSONObject config;
    private static final Map<String, IFlowNode> registers;

    static {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(TestFlowService.class.getResource(CONFIG_PATH).toURI()));
            String text = new String(bytes, StandardCharsets.UTF_8);
            config = JSONObject.parseObject(text);
            registers = new HashMap<>();
            for (IFlowNode flowNode : FlowNode.getFlowNodes()) {
                registers.put(flowNode.getCode(), flowNode);
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化流程配置失败", e);
        }
    }

    private Map<String, FlowRun> flowRunMap = new ConcurrentHashMap<>();
    private Map<String, List<FlowRunTrack>> flowRunTrackMap = new ConcurrentHashMap<>();

    @Override
    public List<FlowDefaultConfig> queryFlowDefaultConfig() {
        List<FlowDefaultConfig> list = config.getJSONArray("defaultConfig").toJavaList(FlowDefaultConfig.class);
        return batchSetId(list);
    }

    @Override
    public List<FlowNextConfig> queryFlowNextConfig() {
        List<FlowNextConfig> list = config.getJSONArray("nextConfig").toJavaList(FlowNextConfig.class);
        return batchSetId(list);
    }

    @Override
    public List<FlowNodeConfig> queryFlowNodeConfig() {
        List<FlowNodeConfig> list = config.getJSONArray("nodeConfig").toJavaList(FlowNodeConfig.class);
        return batchSetId(list);
    }

    @Override
    public FlowRun queryFlowRun(String flow, String flowCode) {
        return flowRunMap.get(key(flow, flowCode));
    }

    @Override
    public List<FlowRunTrack> queryFlowRunTrack(String flow, String flowCode) {
        return flowRunTrackMap.get(key(flow, flowCode));
    }

    @Override
    public void saveFlowRun(FlowRun flowRun) {
        String key = key(flowRun.getFlow(), flowRun.getFlowCode());
        flowRunMap.put(key, flowRun);
    }

    @Override
    public void updateFlowRun(FlowRun flowRun) {
        String key = key(flowRun.getFlow(), flowRun.getFlowCode());
        FlowRun flowRunOld = flowRunMap.get(key);
        if (flowRun.getStatus() != null) {
            flowRunOld.setStatus(flowRun.getStatus());
        }
        if (flowRun.getNode() != null) {
            flowRunOld.setNode(flowRun.getNode());
        }
        if (flowRun.getRunData() != null) {
            flowRunOld.setRunData(flowRun.getRunData());
        }
        if (flowRun.getFlow() != null) {
            flowRunOld.setFlow(flowRun.getFlow());
        }
        if (flowRun.getFlowCode() != null) {
            flowRunOld.setFlowCode(flowRun.getFlowCode());
        }
    }

    @Override
    public void saveFlowRunTrack(FlowRunTrack flowRunTrack) {
        String key = key(flowRunTrack.getFlow(), flowRunTrack.getFlowCode());
        flowRunTrackMap.compute(key, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(flowRunTrack);
            return v;
        });
    }

    @Override
    public IFlowNode getFlowNode(String code) {
        final IFlowNode node = registers.get(code);
        if (node == null) {
            return null;
        }
        // 代理流程节点
        return new IFlowNode() {
            @Override
            public void onFlow(FlowContext context) throws Exception {
                System.out.println("==> " + getCode());
                node.onFlow(context);
            }

            @Override
            public String getCode() {
                return node.getCode();
            }
        };
    }

    private String key(String var1, String var2) {
        return var1 + ";" + var2;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> batchSetId(List<? extends BaseBean> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setId((long) i);
        }
        return (List<T>) list;
    }
}
