package br.com.barbearia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

@Configuration
@EnableMongoHttpSession(collectionName = "sessoes", maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
}
