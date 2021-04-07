package tech.nermindedovic.springcloudwordcounter;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.function.Function;

@SpringBootApplication
public class SpringCloudWordCounterApplication {

    @Bean
    public Function<KStream<String, String>, KStream<String, Long>> process() {
        return input -> input
                .filter((key, val) -> val.contains(","))
                .mapValues((ValueMapper<String, String>) String::toLowerCase)
                .flatMapValues((key,val) -> Arrays.asList(val.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count(Materialized.as("cloudCountsStore"))
                .toStream()
                .peek((key,val) -> System.out.println(key + " " + val));
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringCloudWordCounterApplication.class, args);
    }

}
