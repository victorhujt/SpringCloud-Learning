package com.didispace;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author 翟永超
 * @create 2017/4/15.
 * @blog http://blog.didispace.com
 */
@RestController
public class DcController {

    @Autowired
    ConsumerService consumerService;

    @GetMapping("/consumer")
    public String dc() {
        return consumerService.consumer();
    }

    @Service
    class ConsumerService {

        @Autowired
        RestTemplate restTemplate;

        @HystrixCommand(fallbackMethod = "fallback",
                defaultFallback = "defaultFallback",
                groupKey="ConsumerService", commandKey = "consumer",
                commandProperties  ={
                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
                @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "2")
        })
        public String consumer() {
            return restTemplate.getForObject("http://eureka-client/dc", String.class);
        }

        public String fallback() {
            return "fallbck";
        }

        public String defaultFallback() {
            return "defaultFallback";
        }

    }

}
