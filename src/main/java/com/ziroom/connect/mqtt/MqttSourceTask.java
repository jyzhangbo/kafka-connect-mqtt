package com.ziroom.connect.mqtt;

import com.ziroom.connect.mqtt.constant.MqttSourceConstant;
import com.ziroom.connect.mqtt.processor.MqttMessageProcessor;
import com.ziroom.connect.mqtt.processor.MqttProcessor;
import com.ziroom.connect.mqtt.util.SslUtils;
import com.ziroom.connect.mqtt.util.VersionUtil;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 14:35
 */
public class MqttSourceTask extends SourceTask implements MqttCallbackExtended {

    private static final Logger logger = LoggerFactory.getLogger(MqttSourceTask.class);

    MqttSourceConfiguration configuration;
    MqttConnectOptions mqttConnectOptions;
    MqttAsyncClient mqttClient;
    BlockingQueue<MqttProcessor> mQueue = new LinkedBlockingQueue<>();

    String kafkaTopic;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        logger.info("Start a MqttSourceTask");
        configuration = new MqttSourceConfiguration(props);

        kafkaTopic = configuration.getString(MqttSourceConstant.KAFKA_TOPIC);

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(configuration.getString(MqttSourceConstant.MQTT_USERNAME));
        mqttConnectOptions.setPassword(configuration.getString(MqttSourceConstant.MQTT_PASSWORD).toCharArray());

        mqttConnectOptions.setCleanSession(configuration.getBoolean(MqttSourceConstant.MQTT_CLEAN_SESSION));
        mqttConnectOptions.setKeepAliveInterval(configuration.getInt(MqttSourceConstant.MQTT_KEEP_ALIVE_INTERVAL));
        mqttConnectOptions.setConnectionTimeout(configuration.getInt(MqttSourceConstant.MQTT_CONNECTION_TIMEOUT));
        mqttConnectOptions.setAutomaticReconnect(configuration.getBoolean(MqttSourceConstant.MQTT_AUTOMATIC_RECONNECT));
        mqttConnectOptions.setServerURIs(configuration.getString(MqttSourceConstant.MQTT_SERVER_URIS).split(","));

        String sslCa = configuration.getString(MqttSourceConstant.MQTT_SSL_CA_CERT);
        String sslCert = configuration.getString(MqttSourceConstant.MQTT_SSL_CERT);
        String sslPrivateKey = configuration.getString(MqttSourceConstant.MQTT_SSL_PRIV_KEY);
        try {
            mqttConnectOptions.setSocketFactory(SslUtils.getSocketFactory(sslCa,sslCert,sslPrivateKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String clientId = configuration.getString(MqttSourceConstant.MQTT_CLIENT_ID);
        try {
            mqttClient = new MqttAsyncClient("tcp://127.0.0.1:1883", clientId + InetAddress.getLocalHost().getHostAddress(), new MemoryPersistence());

            //定义回调
            mqttClient.setCallback(this);

            mqttClient.connect(mqttConnectOptions).waitForCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String topic = configuration.getString(MqttSourceConstant.MQTT_TOPIC);
        Integer qos = configuration.getInt(MqttSourceConstant.MQTT_QUALITY_OF_SERVICE);

        try {
            mqttClient.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        MqttProcessor message = mQueue.take();
        Collections.addAll(records, message.gerRecords(kafkaTopic));
        return records;
    }

    @Override
    public void stop() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }
    @Override
    public void connectionLost(Throwable cause) {

    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.info("mqtt receive message:"+message.toString());
        mQueue.add(configuration.getConfiguredInstance(MqttSourceConstant.MESSAGE_PROCESSOR,MqttProcessor.class)
                .process(topic,message));
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
