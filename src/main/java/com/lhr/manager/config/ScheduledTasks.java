package com.lhr.manager.config;

import com.lhr.manager.util.SQLiteHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduledTasks {

    @Resource
    SQLiteHelper sqLiteHelper;

    @Scheduled(cron = "0 0 0 * * ?") // 每天零点 清除
    public void clearShares() {
        sqLiteHelper.clearShares();
        sqLiteHelper.clearVersions();
    }
}
