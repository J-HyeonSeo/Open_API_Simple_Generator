package com.jhsfully.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EnableRedisRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EnableElasticsearchRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EntityScan(basePackages = {"com.jhsfully.domain.entity"})
@EnableJpaAuditing
public class ApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }
}
