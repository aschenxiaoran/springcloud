package hx.springclouds.streams.hystrix;


import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ThreadLocalConfiguration {

    @Autowired(required = false)
    private ThreadLocalAwareStrategy exsitingConcurrencyStrategy;

    @PostConstruct
    public void init(){
        HystrixPlugins plugins = HystrixPlugins.getInstance();
        HystrixEventNotifier eventNotifier= plugins.getEventNotifier();
        HystrixMetricsPublisher metricsPublisher= plugins.getMetricsPublisher();
        HystrixPropertiesStrategy propertiesStrategy= plugins.getPropertiesStrategy();
        HystrixCommandExecutionHook commandExecutionHook= plugins.getCommandExecutionHook();

        HystrixPlugins.reset();

        plugins.registerConcurrencyStrategy(new ThreadLocalAwareStrategy(exsitingConcurrencyStrategy));
        plugins.registerEventNotifier(eventNotifier);
        plugins.registerMetricsPublisher(metricsPublisher);
        plugins.registerPropertiesStrategy(propertiesStrategy);
        plugins.registerCommandExecutionHook(commandExecutionHook);
    }
}
