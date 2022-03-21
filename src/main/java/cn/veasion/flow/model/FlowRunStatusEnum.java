package cn.veasion.flow.model;

/**
 * FlowRunStatusEnum
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public enum FlowRunStatusEnum {

    /**
     * 初始化
     */
    INIT(0),

    /**
     * 正常
     */
    NORMAL(1),

    /**
     * 结束
     */
    FINISH(2),

    /**
     * 暂停
     */
    SUSPEND(3),

    /**
     * 错误
     */
    ERROR(4);

    private Integer status;

    FlowRunStatusEnum(Integer status) {
        this.status = status;
    }

    public static boolean canRunFlow(Integer status) {
        FlowRunStatusEnum statusEnum = of(status);
        if (statusEnum == null) {
            return false;
        }
        return statusEnum == INIT || statusEnum == NORMAL || statusEnum == SUSPEND;
    }

    public static FlowRunStatusEnum of(Integer status) {
        for (FlowRunStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public boolean equalsStatus(Integer status) {
        return this.status.equals(status);
    }
}
