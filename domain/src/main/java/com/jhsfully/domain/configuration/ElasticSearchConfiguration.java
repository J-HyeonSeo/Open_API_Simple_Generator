package com.jhsfully.domain.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfiguration extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.server}")
  private String elasticSearchServer;

  @SuppressWarnings("NullableProblems")
  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(elasticSearchServer)
        .build();
  }

}