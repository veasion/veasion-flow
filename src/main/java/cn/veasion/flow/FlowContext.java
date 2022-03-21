package cn.veasion.flow;

import cn.veasion.flow.model.FlowRun;
import com.alibaba.fastjson.JSONObject;

import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程上下文
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowContext {

    public static final String NEXT_KEY = "next";

    private String flowCode;
    private FlowRun flowRun;
    private FlowContext parent;
    private ScriptContext scriptContext;
    private Map<String, Object> trackMap = new HashMap<>();
    private Map<String, Object> data = new ConcurrentHashMap<>();

    public FlowContext(String flowCode) {
        this.flowCode = flowCode;
    }

    public void next() {
        data.put(NEXT_KEY, "default");
    }

    public void nextYes() {
        this.set(NEXT_KEY, "yes");
    }

    public void nextNo() {
        this.set(NEXT_KEY, "no");
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object get(String key) {
        Object value = data.get(key);
        return value == null && parent != null ? parent.get(key) : value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.get(key);
    }

    public void set(String key, Object value) {
        data.put(key, value);
        trackMap.put(key, value);
    }

    public String getFlowCode() {
        return flowCode;
    }

    public FlowRun getFlowRun() {
        return flowRun;
    }

    public void setFlowRun(FlowRun flowRun) {
        this.flowRun = flowRun;
    }

    public void setParent(FlowContext parent) {
        this.parent = parent;
    }

    public FlowContext getParent() {
        return parent;
    }

    public ScriptContext getScriptContext() {
        return scriptContext;
    }

    public void setScriptContext(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public Map<String, Object> getTrackMap() {
        return trackMap;
    }

    public static String convertRunData(FlowContext context) {
        JSONObject json = new JSONObject();
        json.put("flowCode", context.flowCode);
        json.put("data", context.data);
        if (context.parent != null) {
            json.put("parent", context.parent);
        }
        return json.toJSONString();
    }

    public static FlowContext convertFlowContext(String runData) {
        JSONObject json = JSONObject.parseObject(runData);
        String flowCode = json.getString("flowCode");
        FlowContext context = new FlowContext(flowCode);
        JSONObject data = json.getJSONObject("data");
        for (String key : data.keySet()) {
            context.set(key, data.get(key));
        }
        if (json.containsKey("parent")) {
            context.parent = convertFlowContext(json.getString("parent"));
        }
        return context;
    }

    public static void copy(FlowContext source, FlowContext target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        target.parent = source.parent;
        target.flowCode = source.flowCode;
        for (String key : source.data.keySet()) {
            target.data.put(key, source.data.get(key));
        }
    }

}
