package com.ziroom.connect.mqtt.processor;

import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 15:56
 */
public interface MqttProcessor {

    MqttProcessor process(String topic, MqttMessage message);

    SourceRecord[] gerRecords(String kafkaTopic);

}
