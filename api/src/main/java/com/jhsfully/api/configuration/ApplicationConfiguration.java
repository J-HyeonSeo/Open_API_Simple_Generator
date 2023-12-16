package com.jhsfully.api.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EnableRedisRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EnableElasticsearchRepositories(basePackages = {"com.jhsfully.domain.repository"})
@EntityScan(basePackages = {"com.jhsfully.domain.entity"})
@EnableJpaAuditing
public class ApplicationConfiguration {

  @Bean
  public DefaultOAuth2UserService defaultOAuth2UserService(){
    return new DefaultOAuth2UserService();
  }

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
