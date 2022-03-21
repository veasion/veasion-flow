package cn.veasion.flow.core;

import cn.veasion.flow.FlowContext;

/**
 * IScriptExecutor
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public interface IScriptExecutor {

    void beforeFlow(FlowContext context);

    Object execute(FlowContext context, String script) throws FlowConfigException;

    void afterFlow(FlowContext context);

}
