package cn.veasion.flow.core;

/**
 * FlowException
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    public FlowException(String message) {
        super(message);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
