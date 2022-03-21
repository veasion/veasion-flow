package cn.veasion.flow.core;

import cn.veasion.flow.FlowContext;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.util.Map;

/**
 * JavascriptScriptExecutor
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class JavascriptScriptExecutor implements IScriptExecutor {

    private ScriptEngine engine;

    public JavascriptScriptExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("javascript");
    }

    @Override
    public void beforeFlow(FlowContext context) {
        ScriptContext scriptContext = context.getScriptContext();
        if (scriptContext == null) {
            scriptContext = new SimpleScriptContext();
            context.setScriptContext(scriptContext);
        }
        scriptContext.setAttribute("ctx", context, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public Object execute(FlowContext context, String script) throws FlowConfigException {
        ScriptContext scriptContext = context.getScriptContext();
        final Map<String, Object> data = context.getData();
        for (String key : data.keySet()) {
            scriptContext.setAttribute(key, data.get(key), ScriptContext.ENGINE_SCOPE);
        }
        try {
            return this.engine.eval(script, scriptContext);
        } catch (Exception e) {
            throw new FlowConfigException("执行脚本[" + script + "]发生错误", e);
        }
    }

    @Override
    public void afterFlow(FlowContext context) {
        context.setScriptContext(null);
    }
}
