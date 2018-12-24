package com.ziroom.connect.mqtt.processor;

import com.ziroom.connect.mqtt.model.GatewayOnlineReq;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.nutz.json.Json;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 15:58
 */
public class MqttMessageProcessor implements MqttProcessor{

    private String topic;
    private MqttMessage message;

    @Override
    public MqttProcessor process(String topic, MqttMessage message) {
        this.topic = topic;
        this.message = message;
        return this;
    }

    @Override
    public SourceRecord[] gerRecords(String kafkaTopic) {
        String msg = message.toString();
        GatewayOnlineReq req = Json.fromJson(GatewayOnlineReq.class, msg);
        return new SourceRecord[]{new SourceRecord(null,null,kafkaTopic,null,
                Schema.STRING_SCHEMA,req.token,Schema.STRING_SCHEMA,msg)};
    }
}
