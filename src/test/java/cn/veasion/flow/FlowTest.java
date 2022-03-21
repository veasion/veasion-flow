package cn.veasion.flow;

import cn.veasion.flow.core.IFlowService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * FlowTest
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowTest {

    public static void main(String[] args) {
        FlowManager flowManager = new FlowManager(new TestFlowService(), true);
        // 运行错误节点
        errorNodeTest(flowManager);
        // 基于上一次运行
        basedLastRunTest(flowManager);
        // 流程条件：未支付
        flowManager.startFlowSync(new FlowIn("SO", "no"));
        // 流程条件：已支付
        flowManager.startFlowSync(new FlowIn("SO", String.valueOf(System.currentTimeMillis())));
    }

    private static void errorNodeTest(FlowManager flowManager) {
        try {
            flowManager.startFlowSync(new FlowIn("SO", "error"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void basedLastRunTest(FlowManager flowManager) {
        IFlowService flowService = flowManager.getFlowService();
        flowManager.startFlowSync(new FlowIn("SO", "none"));
        System.out.println(JSONObject.toJSONString(flowService.queryFlowRun("SO", "none"), SerializerFeature.PrettyFormat));
        flowManager.startFlowSync(new FlowIn("SO", "none"));
        System.out.println(JSONObject.toJSONString(flowService.queryFlowRun("SO", "none"), SerializerFeature.PrettyFormat));
        System.out.println(JSONObject.toJSONString(flowService.queryFlowRunTrack("SO", "none")));
    }
}
