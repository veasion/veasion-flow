package cn.veasion.flow.core;

import java.util.HashSet;
import java.util.Set;

/**
 * SimpleFlowLock
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class SimpleFlowLock implements IFlowLock {

    private final Set<String> keys = new HashSet<>();

    @Override
    public boolean tryLock(String flow, String flowCode) {
        String key = key(flow, flowCode);
        synchronized (keys) {
            if (!keys.contains(key)) {
                keys.add(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public void unlock(String flow, String flowCode) {
        String key = key(flow, flowCode);
        synchronized (keys) {
            keys.remove(key);
        }
    }

    private String key(String flow, String flowCode) {
        return flow + ";" + flowCode;
    }
}
