package com.ziroom.connect.mqtt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Author:zhangbo
 * @Date:2018/12/21 14:42
 */
public class VersionUtil {

    private static final Logger logger = LoggerFactory.getLogger(VersionUtil.class);
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(VersionUtil.class.getResourceAsStream("/application.properties"));
        }catch (Exception e){
            logger.warn("can not load the properties");
        }
    }

    public static String getVersion(){
        return properties.getProperty("project.version");
    }

}
