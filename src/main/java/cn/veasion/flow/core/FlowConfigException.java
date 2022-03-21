package cn.veasion.flow.core;

/**
 * FlowConfigException
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowConfigException extends FlowException {

	private static final long serialVersionUID = 1L;

	public FlowConfigException(String message) {
        super(message);
    }

    public FlowConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
