package com.jhsfully.api.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

//@Configuration
public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration {

  @Value("${spring.elasticsearch.server")
  private String elasticSearchServer;

  @Override
  public RestHighLevelClient elasticsearchClient() {
    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .connectedTo(elasticSearchServer)
        .build();
    return RestClients.create(clientConfiguration).rest();
  }
}
