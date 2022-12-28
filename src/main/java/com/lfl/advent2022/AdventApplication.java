package com.lfl.advent2022;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.impl.utility.Iterate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class AdventApplication implements CommandLineRunner {
    private static final String day = "25";
    private static final String input = "day" + day + ".txt";
    private static final String inputOverride = null;

    private LinesConsumer service;

    public AdventApplication(ApplicationContext context) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(LinesConsumer.class));
        Set<BeanDefinition> days = provider.findCandidateComponents("com.lfl");
        BeanDefinition beanDefinition = Iterate.detect(days, bean -> bean.getBeanClassName().endsWith("Day" + day));

        try {
            Class<?> serviceClass = context.getClassLoader().loadClass(beanDefinition.getBeanClassName());
            Constructor<?> constructor = serviceClass.getConstructor();
            service = LinesConsumer.class.cast(constructor.newInstance());
        } catch (Exception e) {
            List<String> dayNames = Iterate.collect(days, BeanDefinition::getBeanClassName)
                    .stream()
                    .map(name -> name.substring(name.lastIndexOf(".") + 1))
                    .toList();
            log.error("Error while trying to find Day" + day + ". The set of recognised is " + dayNames, e);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdventApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        Instant start = Instant.now();
        log.info("Start run");

        if (service != null) {
            service.consume(LinesConsumer.readAllInput(Optional.ofNullable(inputOverride).orElse(input)));
        }

        log.info("End run");
        Instant end = Instant.now();
        log.info("Elapsed time = {}ms", Duration.between(start, end).toMillis());
    }
}
