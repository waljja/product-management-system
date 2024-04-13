package com.example.productkanbanapi.config;

import com.example.productkanbanapi.job.save313NotRecInfoJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QuartzConfig
 *
 * @author 丁国钊
 * @date 2023-04-28-9:16
 */
@Configuration
public class QuartzConfig {

    /**
     * 保存已过313未收货UID信息任务
     *
     * @return
     */
    @Bean
    public JobDetail save313NotRecInfoDetail() {
        return JobBuilder.newJob(save313NotRecInfoJob.class).withIdentity("save313NotRecInfoJob").storeDurably().build();
    }

    @Bean
    public Trigger save313NotRecInfoTrigger() {
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 44 16 * * ? ");
        // 30分钟获取一次313过账数据
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/30 * * * ? ");
        return TriggerBuilder
                .newTrigger()
                .forJob(save313NotRecInfoDetail())
                .withIdentity("save313NotRecInfoJob")
                .withSchedule(scheduleBuilder)
                .build();
    }

}
