package cn.veasion.flow.core;

/**
 * IFlowLock
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public interface IFlowLock {

    boolean tryLock(String flow, String flowCode);

    void unlock(String flow, String flowCode);

}
