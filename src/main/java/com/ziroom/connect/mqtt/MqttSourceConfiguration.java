package com.ziroom.connect.mqtt;

import com.ziroom.connect.mqtt.constant.MqttSourceConstant;
import com.ziroom.connect.mqtt.processor.MqttMessageProcessor;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 14:50
 */
public class MqttSourceConfiguration extends AbstractConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttSourceConfiguration.class);

    public static final ConfigDef config = new ConfigDef();

    static {
        config.define(MqttSourceConstant.KAFKA_TOPIC, ConfigDef.Type.STRING, "zlink-device-online",
                        ConfigDef.Importance.LOW, "Kafka topic")
                .define(MqttSourceConstant.MQTT_CLIENT_ID, ConfigDef.Type.STRING, "zlink-default",
                        ConfigDef.Importance.MEDIUM, "mqtt client id to use")
                .define(MqttSourceConstant.MQTT_CLEAN_SESSION, ConfigDef.Type.BOOLEAN, true,
                        ConfigDef.Importance.HIGH,"use clean session in connection?")
                .define(MqttSourceConstant.MQTT_CONNECTION_TIMEOUT, ConfigDef.Type.INT, 30,
                        ConfigDef.Importance.LOW,"connection timeout to use")
                .define(MqttSourceConstant.MQTT_KEEP_ALIVE_INTERVAL, ConfigDef.Type.INT, 30,
                        ConfigDef.Importance.LOW,"keepalive interval to use")
                .define(MqttSourceConstant.MQTT_SERVER_URIS, ConfigDef.Type.STRING,"tcp://localhost:1883",
                        ConfigDef.Importance.HIGH,"mqtt server to connect to")
                .define(MqttSourceConstant.MQTT_TOPIC, ConfigDef.Type.STRING, "/com/ziroom/iot/zgateway/online",
                        ConfigDef.Importance.HIGH,"mqtt topic to subscribe")
                .define(MqttSourceConstant.MQTT_QUALITY_OF_SERVICE, ConfigDef.Type.INT, 1,
                        ConfigDef.Importance.LOW,"mqtt qos to use")
                .define(MqttSourceConstant.MQTT_SSL_CA_CERT, ConfigDef.Type.STRING, null,
                        ConfigDef.Importance.LOW,"CA cert file to use if using ssl")
                .define(MqttSourceConstant.MQTT_SSL_CERT, ConfigDef.Type.STRING, null,
                        ConfigDef.Importance.LOW,"cert file to use if using ssl")
                .define(MqttSourceConstant.MQTT_SSL_PRIV_KEY, ConfigDef.Type.STRING, null,
                        ConfigDef.Importance.LOW,"cert priv key to use if using ssl")
                .define(MqttSourceConstant.MQTT_USERNAME, ConfigDef.Type.STRING, "zlink-controller",
                        ConfigDef.Importance.MEDIUM,"username to authenticate to mqtt broker")
                .define(MqttSourceConstant.MQTT_PASSWORD, ConfigDef.Type.STRING, "47d98483da",
                        ConfigDef.Importance.MEDIUM,"password to authenticate to mqtt broker")
                .define(MqttSourceConstant.MESSAGE_PROCESSOR, ConfigDef.Type.CLASS,MqttMessageProcessor.class,
                        ConfigDef.Importance.HIGH,"message processor to use")
                .define(MqttSourceConstant.MQTT_AUTOMATIC_RECONNECT,ConfigDef.Type.BOOLEAN,true,
                        ConfigDef.Importance.MEDIUM,"reconnect to mqtt server?");
    }

    public MqttSourceConfiguration(Map<String, String> properties) {
        super(config, properties);
        logger.info("initialize properties");
    }


}
