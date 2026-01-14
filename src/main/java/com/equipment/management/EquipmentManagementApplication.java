package com.equipment.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 设备管理系统启动类
 */
@SpringBootApplication
@EnableScheduling
public class EquipmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EquipmentManagementApplication.class, args);
    }
}
