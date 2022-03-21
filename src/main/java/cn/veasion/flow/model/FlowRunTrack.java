package cn.veasion.flow.model;

/**
 * FlowRunTrack
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowRunTrack extends BaseBean {

	private static final long serialVersionUID = 1L;

    private String flow; // 流程
    private String flowCode; // 流程编码
    private String node; // 节点
    private String trackData; // 跟踪数据
    private Long execTimeMillis; // 执行时间(毫秒)

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getTrackData() {
        return trackData;
    }

    public void setTrackData(String trackData) {
        this.trackData = trackData;
    }

    public Long getExecTimeMillis() {
        return execTimeMillis;
    }

    public void setExecTimeMillis(Long execTimeMillis) {
        this.execTimeMillis = execTimeMillis;
    }
}
