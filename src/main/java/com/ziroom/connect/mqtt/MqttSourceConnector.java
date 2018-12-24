package com.ziroom.connect.mqtt;

import com.ziroom.connect.mqtt.util.VersionUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 14:32
 */
public class MqttSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(MqttSourceConnector.class);

    MqttSourceConfiguration configuration;
    private Map<String, String> props;

    @Override
    public void start(Map<String, String> map) {
        log.info("Start a MqttSourceConnector");
        configuration = new MqttSourceConfiguration(map);
        this.props = map;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MqttSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> taskProps = new HashMap<>(props);
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
        log.info("Stop the MqttSourceConnector");
    }

    @Override
    public ConfigDef config() {
        return MqttSourceConfiguration.config;
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }
}
