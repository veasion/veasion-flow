package cn.veasion.flow;

import cn.veasion.flow.core.FlowException;
import cn.veasion.flow.core.FlowNodeCore;
import cn.veasion.flow.core.IFlowLock;
import cn.veasion.flow.core.IFlowService;
import cn.veasion.flow.core.IScriptExecutor;
import cn.veasion.flow.core.JavascriptScriptExecutor;
import cn.veasion.flow.core.SimpleFlowLock;
import cn.veasion.flow.model.FlowConfig;
import cn.veasion.flow.model.FlowNextConfig;
import cn.veasion.flow.model.FlowNextNode;
import cn.veasion.flow.model.FlowNodeConfig;
import cn.veasion.flow.model.FlowRun;
import cn.veasion.flow.model.FlowRunStatusEnum;
import cn.veasion.flow.model.FlowRunTrack;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FlowManager
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class FlowManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowManager.class);

    private IFlowLock lock;
    private IFlowService flowService;
    private FlowNodeCore flowNodeCore;
    private ExecutorService executor;
    private IScriptExecutor scriptExecutor;
    private boolean lazyLoadFlowConfig;
    public static final Integer YES = 1;

    public FlowManager(IFlowService flowService, boolean lazyLoadFlowConfig) {
        this(flowService, null, null, lazyLoadFlowConfig);
    }

    public FlowManager(IFlowService flowService, IFlowLock lock, ExecutorService executor, boolean lazyLoadFlowConfig) {
        if (lock == null) {
            lock = new SimpleFlowLock();
        }
        if (executor == null) {
            int poolSize = Runtime.getRuntime().availableProcessors();
            if (poolSize < 2) {
                poolSize = 2;
            }
            executor = new ThreadPoolExecutor(poolSize, poolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        }
        this.lock = lock;
        this.executor = executor;
        this.lazyLoadFlowConfig = lazyLoadFlowConfig;
        this.flowService = Objects.requireNonNull(flowService);
        this.scriptExecutor = new JavascriptScriptExecutor();
        this.flowNodeCore = new FlowNodeCore(flowService);
        if (!this.lazyLoadFlowConfig) {
            this.flowNodeCore.reload();
        }
    }

    public Future<FlowContext> startFlow(FlowIn flowIn) {
        return startFlow(flowIn, null);
    }

    public Future<FlowContext> startFlow(FlowIn flowIn, FlowContext parent) {
        return executor.submit(() -> runFlow(flowIn, parent));
    }

    public FlowContext startFlowSync(FlowIn flowIn) {
        return startFlowSync(flowIn, null);
    }

    public FlowContext startFlowSync(FlowIn flowIn, FlowContext parent) {
        return runFlow(flowIn, parent);
    }

    private FlowContext runFlow(FlowIn flowIn, FlowContext parent) {
        if (lazyLoadFlowConfig && !flowNodeCore.isLoaded()) {
            flowNodeCore.reload();
        }
        String flow = flowIn.getFlow();
        String flowCode = flowIn.getFlowCode();
        if (!lock.tryLock(flow, flowCode)) {
            throw new FlowException(String.format("flow: %s, flowCode: %s tryLock fail.", flow, flowCode));
        }
        try {
            FlowContext context = new FlowContext(flowCode);
            context.setParent(parent);
            doFlow(context, flowIn);
            return context;
        } finally {
            lock.unlock(flow, flowCode);
        }
    }

    private void doFlow(FlowContext context, FlowIn flowIn) {
        FlowConfig flowConfig = flowNodeCore.getFlowConfig(flowIn.getFlow());
        if (flowConfig == null) {
            throw new FlowException(String.format("flow: %s Not Found.", flowIn.getFlow()));
        }
        FlowNextNode startNode = flowConfig.getStartNode();
        if (startNode == null) {
            throw new FlowException(String.format("flow: %s startNode Not Found.", flowIn.getFlow()));
        }
        FlowRun flowRun = flowService.queryFlowRun(flowIn.getFlow(), flowIn.getFlowCode());
        if (flowIn.isBasedLastRun() && flowRun != null) {
            // 基于上一次运行
            if (!FlowRunStatusEnum.INIT.equalsStatus(flowRun.getStatus())) {
                startNode = getStartNode(context, flowRun);
                if (startNode == null) {
                    return;
                }
            }
        }
        try {
            // 初始化脚本
            scriptExecutor.beforeFlow(context);
            // 运行流程节点
            runFlowNextNode(context, startNode, flowRun);
        } catch (Exception e) {
            FlowNextNode errorNode = flowConfig.getErrorNode();
            if (errorNode != null) {
                try {
                    runFlowNextNode(context, errorNode, null);
                } catch (Exception ee) {
                    LOGGER.error("运行错误流程节点异常！flow: {}, flowCode: {}", flowIn.getFlow(), flowIn.getFlowCode(), ee);
                    updateRunStatus(context, flowIn.getFlow(), flowIn.getFlowCode(), FlowRunStatusEnum.SUSPEND);
                    return;
                }
            }
            LOGGER.error("运行流程节点异常！flow: {}, flowCode: {}", flowIn.getFlow(), flowIn.getFlowCode(), e);
            updateRunStatus(context, flowIn.getFlow(), flowIn.getFlowCode(), FlowRunStatusEnum.ERROR);
        } finally {
            scriptExecutor.afterFlow(context);
        }
    }

    private void updateRunStatus(FlowContext context, String flow, String flowCode, FlowRunStatusEnum statusEnum) {
        FlowRun flowRun = flowService.queryFlowRun(flow, flowCode);
        if (flowRun != null) {
            FlowRun update = new FlowRun();
            update.setId(flowRun.getId());
            update.setStatus(statusEnum.getStatus());
            update.setUpdateTime(new Date());
            if (context != null) {
                update.setRunData(context.convertRunData());
            }
            flowService.updateFlowRun(update);
        }
    }

    private FlowNextNode getStartNode(FlowContext context, FlowRun flowRun) {
        FlowRunStatusEnum statusEnum = FlowRunStatusEnum.of(flowRun.getStatus());
        if (FlowRunStatusEnum.FINISH.equals(statusEnum)) {
            throw new FlowException("该流程已结束运行");
        }
        FlowContext lastContext = FlowContext.convertFlowContext(flowRun.getRunData());
        if (lastContext != null) {
            FlowContext.copy(lastContext, context);
        }
        List<FlowNextNode> nodes = flowNodeCore.getNodes(flowRun.getFlow(), flowRun.getNode());
        if (nodes == null || nodes.isEmpty()) {
            // 节点消失，暂停
            LOGGER.warn("flow: {}, node: {} 流程节点未找到", flowRun.getFlow(), flowRun.getNode());
            flowRun.setStatus(FlowRunStatusEnum.SUSPEND.getStatus());
            flowService.updateFlowRun(flowRun);
            return null;
        }
        return nodes.get(0);
    }

    private void runFlowNextNode(FlowContext context, FlowNextNode startNode, FlowRun flowRun) throws Exception {
        if (flowRun == null) {
            flowRun = getFlowRun(context, startNode);
        }
        context.setFlowRun(flowRun);
        FlowNextNode nextNode = startNode;
        while (true) {
            context.next();
            FlowNodeConfig node = nextNode.getNode();
            FlowNextConfig flowNextConfig = nextNode.getFlowNextConfig();
            String onBefore = flowNextConfig.getOnBefore();
            String onAfter = flowNextConfig.getOnAfter();
            long timeMillis = System.currentTimeMillis();
            if (onBefore != null && !"".equals(onBefore)) {
                scriptExecutor.execute(context, onBefore);
            }
            if (!YES.equals(node.getIsVirtual())) {
                IFlowNode flowNode = nextNode.getFlowNode();
                if (flowNode == null) {
                    // 未找到节点代码实现，暂停
                    flowRun.setStatus(FlowRunStatusEnum.SUSPEND.getStatus());
                    LOGGER.warn("{} 节点未找到对应实现", node.getCode());
                    break;
                }
                context.getTrackMap().clear();
                // 执行流程节点
                flowNode.onFlow(context);
            }
            if (onAfter != null && !"".equals(onAfter)) {
                scriptExecutor.execute(context, onAfter);
            }
            // 记录运行 track
            recordTrack(context, flowRun, flowNextConfig, System.currentTimeMillis() - timeMillis);
            if (!nextNode.hasNext()) {
                // 没有下一个节点，流程结束
                flowRun.setStatus(FlowRunStatusEnum.FINISH.getStatus());
                break;
            }
            List<FlowNextNode> nextNodes = flowNodeCore.getNodes(flowNextConfig.getFlow(), flowNextConfig.getNode());
            if (nextNodes == null || nextNodes.isEmpty()) {
                // 未找到节点，流程配置有问题，暂停
                flowRun.setStatus(FlowRunStatusEnum.SUSPEND.getStatus());
                LOGGER.warn("未找到节点配置 flow: {}, node: {}", flowNextConfig.getFlow(), flowNextConfig.getNode());
                break;
            }
            if (context.suspend || (nextNode = getNextNode(context, nextNodes)) == null) {
                // 未匹配下一个节点，暂停
                flowRun.setStatus(FlowRunStatusEnum.SUSPEND.getStatus());
                LOGGER.warn("flow: {}, node: {} 未匹配下一个节点", flowNextConfig.getFlow(), flowNextConfig.getNode());
                break;
            }
        }
        flowRun.setUpdateTime(new Date());
        flowService.updateFlowRun(flowRun);
    }

    private FlowNextNode getNextNode(FlowContext context, List<FlowNextNode> flowNextNodes) {
        FlowNextNode defaultNode = null;
        for (FlowNextNode flowNextNode : flowNextNodes) {
            FlowNextConfig flowNextConfig = flowNextNode.getFlowNextConfig();
            String cond = flowNextConfig.getCond();
            if (cond != null && !"".equals(cond)) {
                Object result = scriptExecutor.execute(context, cond);
                if (result != null && "true".equalsIgnoreCase(String.valueOf(result))) {
                    defaultNode = flowNextNode;
                    break;
                }
            } else {
                defaultNode = flowNextNode;
            }
        }
        if (defaultNode == null) {
            return null;
        }
        FlowNextConfig flowNextConfig = defaultNode.getFlowNextConfig();
        FlowNextNode result = flowNodeCore.getNode(flowNextConfig.getNextFlow(), flowNextConfig.getNextNode());
        if (result == null) {
            LOGGER.warn("未找到节点配置 flow: {}, node: {}", flowNextConfig.getNextFlow(), flowNextConfig.getNextNode());
        }
        return result;
    }

    private void recordTrack(FlowContext context, FlowRun flowRun, FlowNextConfig flowNextConfig, long timeMillis) {
        flowRun.setNode(flowNextConfig.getNode());
        flowRun.setStatus(FlowRunStatusEnum.NORMAL.getStatus());
        flowRun.setRunData(context.convertRunData());
        flowRun.setUpdateTime(new Date());
        flowService.updateFlowRun(flowRun);
        FlowRunTrack flowRunTrack = new FlowRunTrack();
        flowRunTrack.setExecTimeMillis(timeMillis);
        flowRunTrack.setFlow(flowNextConfig.getFlow());
        flowRunTrack.setNode(flowNextConfig.getNode());
        flowRunTrack.setFlowCode(context.getFlowCode());
        Map<String, Object> trackMap = context.getTrackMap();
        if (!trackMap.isEmpty()) {
            flowRunTrack.setTrackData(JSONObject.toJSONString(trackMap));
        }
        flowRunTrack.setCreateTime(new Date());
        flowService.saveFlowRunTrack(flowRunTrack);
    }

    private FlowRun getFlowRun(FlowContext context, FlowNextNode node) {
        FlowRun flowRun = new FlowRun();
        flowRun.setFlow(node.getFlowNextConfig().getFlow());
        flowRun.setNode(node.getFlowNextConfig().getNode());
        flowRun.setFlowCode(context.getFlowCode());
        flowRun.setRunData(context.convertRunData());
        flowRun.setStatus(FlowRunStatusEnum.INIT.getStatus());
        flowRun.setCreateTime(new Date());
        flowService.saveFlowRun(flowRun);
        return flowRun;
    }

    public void setLock(IFlowLock lock) {
        this.lock = Objects.requireNonNull(lock);
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    public void setScriptExecutor(IScriptExecutor scriptExecutor) {
        this.scriptExecutor = Objects.requireNonNull(scriptExecutor);
    }

    public IFlowService getFlowService() {
        return flowService;
    }

}
