package cn.veasion.flow.chart;

import cn.veasion.flow.TestFlowService;
import cn.veasion.flow.core.IFlowService;

/**
 * MermaidTest
 *
 * @author luozhuowei
 * @date 2020/10/19
 */
public class MermaidTest {

    public static void main(String[] args) {
        String flow = "SO";
        String flowCode = null;
        IFlowService flowService = new TestFlowService();
        // String html = MermaidHelper.getChartHtml(flowService, flow, flowCode);
        // System.out.println(html);
        String mermaid = MermaidHelper.getFlowChartCode(flowService, flow, flowCode);
        System.out.println(mermaid);
    }

}
