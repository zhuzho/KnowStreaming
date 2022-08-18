package com.xiaojukeji.know.streaming.km.task.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.BrokerMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.task.AbstractClusterPhyDispatchTask;
import com.xiaojukeji.know.streaming.km.task.service.TaskThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Task(name = "BrokerMetricCollectorTask",
        description = "Broker指标采集任务,",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class BrokerMetricCollectorTask extends AbstractClusterPhyDispatchTask {
    private static final ILog log = LogFactory.getLog(BrokerMetricCollectorTask.class);

    @Autowired
    private BrokerMetricCollector brokerMetricCollector;

    @Autowired
    private TaskThreadPoolService taskThreadPoolService;

    @Override
    public TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        taskThreadPoolService.submitHeavenTask(
                String.format("TaskName=%s clusterPhyId=%d", this.taskName, clusterPhy.getId()),
                100000,
                () -> brokerMetricCollector.collectMetrics(clusterPhy)
        );

        return TaskResult.SUCCESS;
    }
}
