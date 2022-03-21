package cn.veasion.flow.chart;

import cn.veasion.flow.TestFlowService;
import cn.veasion.flow.core.IFlowService;

/**
 * FlowChartTest
 *
 * @author luozhuowei
 * @date 2020/10/19
 */
public class FlowChartTest {

    public static void main(String[] args) {
        String flow = "SO";
        String flowCode = null;
        IFlowService flowService = new TestFlowService();
        String flowChart = FlowChartHelper.getFlowChartCode(flowService, flow, flowCode);
        System.out.println(flowChart);
    }

}
