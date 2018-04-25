package com.songlea.demo.cloud.admin.monitor.config;

import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * spring boot admin监控提醒配置
 */
@Configuration
@EnableScheduling
public class NotifierConfiguration {

    private Notifier notifier;

    public NotifierConfiguration() {
    }

    @Autowired
    public NotifierConfiguration(Notifier notifier) {
        Assert.notNull(notifier, "NotifierConfiguration.notifier must be not null");
        this.notifier = notifier;
    }

    @Bean
    @Primary
    public RemindingNotifier remindingNotifier() {
        RemindingNotifier notifier = new RemindingNotifier(filteringNotifier());
        // 默认情况下,提醒每10分钟发送一次,即这个时间窗口内不重复报警
        notifier.setReminderPeriod(TimeUnit.MINUTES.toMillis(10));
        // 设定监控服务状态，状态改变为给定值的时候提醒,默认为DOWN,OFFLINE
        notifier.setReminderStatuses(new String[]{"DOWN", "OFFLINE", "UP"});
        return notifier;
    }

    @Scheduled(fixedRate = 60_000L)
    public void remind() {
        remindingNotifier().sendReminders();
    }

    @Bean
    public FilteringNotifier filteringNotifier() {
        return new FilteringNotifier(notifier);
    }
}
