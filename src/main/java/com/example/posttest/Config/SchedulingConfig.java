package com.example.posttest.Config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Controller;

//@Configuration
//@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler threadPoolTaskExecutor=new ThreadPoolTaskScheduler();


        threadPoolTaskExecutor.setPoolSize(2);
        threadPoolTaskExecutor.setThreadNamePrefix("my-custom-");
        threadPoolTaskExecutor.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskExecutor);

    // web socket 사용과정에서 websocket config 를 사용하니까 대충 spring  의 스레드풀에 message borker라는
        //스레드풀이 작동해서 덧씌워지는걸로 보임. 그래서 이 schedulingconfig없이스케쥴링을 썻을떄 message broker라는
        //이름으로 멀티스레드도 작동한거고 그래서 여기서 새로이 스케쥴링만을 위한 새로운 스레드풀을 정의해서 사용하면
        //전용 스레드풀에서만 꺼내쓴다.
    }
}
