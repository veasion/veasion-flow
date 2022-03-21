package cn.veasion.flow;

import cn.veasion.flow.core.FlowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * FlowNode
 *
 * @author luozhuowei
 * @date 2020/10/19
 */
public enum FlowNode {

    IS_PAY(context -> {
        context.set("so", new HashMap<String, Object>());
        String flowCode = context.getFlowCode();
        if ("no".equals(flowCode)) {
            context.nextNo();
        } else if ("none".equals(flowCode) && context.get("none") == null) {
            context.set("none", 1);
            context.next();
        } else if ("error".equals(flowCode)) {
            throw new FlowException("异常回滚测试");
        } else {
            context.nextYes();
        }
    }),

    WAIT_PAY(context -> {
        System.out.println("等待支付...");
    }),

    PAY_DONE(context -> {
        Map<String, Object> map = context.getData("so");
        map.put("switch", 1);
        context.getTrackMap().put("so", map);
    }),

    SO_ERROR(context -> {
        System.err.println("运行错误节点...");
    });

    private Consumer<FlowContext> consumer;

    FlowNode(Consumer<FlowContext> consumer) {
        this.consumer = consumer;
    }

    public static List<IFlowNode> getFlowNodes() {
        List<IFlowNode> list = new ArrayList<>();
        for (FlowNode value : values()) {
            list.add(new IFlowNode() {
                @Override
                public void onFlow(FlowContext context) {
                    if (value.consumer != null) {
                        value.consumer.accept(context);
                    }
                }

                @Override
                public String getCode() {
                    return value.name();
                }
            });
        }
        return list;
    }

}
