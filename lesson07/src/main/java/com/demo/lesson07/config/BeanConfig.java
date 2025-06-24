package com.demo.lesson07.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class BeanConfig {


    // 注册一个工具类
    @Bean
    @Description("根据传入国家或地区的名字，获取所在国家或地区时区的当前日期和时间")
    public Function<DateTimeRequest, String> dateTimeTools() {
        return new DateTimeTools();
    }

    /**
     * 定义一个工具类
     */
    public static class DateTimeTools implements Function<DateTimeRequest, String> {
        @Override
        public String apply(DateTimeRequest zone) {
            Map<String,String> map = new HashMap<>();
            map.put("北京","Asia/Shanghai");
            map.put("伦敦","Europe/London");
            map.put("洛杉矶","America/Los_Angeles");
            ZoneId zoneIdGMT = ZoneId.of(map.get(zone.location())==null?"Asia/Shanghai":map.get(zone.location()));
            String time = LocalDateTime.now(zoneIdGMT).toString();
            System.out.println(time);
            return time;
        }
    }

    // 看看3.2.3的参数限制，不支持原始数据类型，因此需要自定义一个POJO
    public record DateTimeRequest(String location) {}
}
