package com.thn.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SpecificationLibProperties.class)
public class SpecificationLibConfiguration {

}
