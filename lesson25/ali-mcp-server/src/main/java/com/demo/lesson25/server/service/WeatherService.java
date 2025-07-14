package com.demo.lesson25.server.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String adcode = "adcode";

    @Tool(description = "获取中国城市的天气情况")
    public String getWeatherForecastByCity(@ToolParam(description = "城市名称") String city) {
        // 获取城市的adcode
        String result = restTemplate.getForObject("https://restapi.amap.com/v3/geocode/geo?address="+city+"&key=ef3cbaef8f0965c6205f56e0ff00ceb4", String.class);
        // 这里为了方便简单处理一下字符串获取adcode，正式的话需要解析json格式
        int startIndex = result.indexOf(adcode);
        String code = result.substring(startIndex+adcode.length()+3,result.indexOf(",",startIndex)-1);
        // 通过城市的adcode，进行获取天气预报
        result = restTemplate.getForObject("https://restapi.amap.com/v3/weather/weatherInfo?extensions=all&key=ef3cbaef8f0965c6205f56e0ff00ceb4&city="+code, String.class);
        return result;
    }

}
