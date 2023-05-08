package com.learn.bookgenie;

import com.learn.bookgenie.connection.DataStaxAstraProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookGenieApplication {

	@Value("${spring.main.data.cassandra.keyspace-name}")
	private String keyspaceName;

	@Value("${spring.main.data.cassandra.username}")
	private String cassandraUsername;

	@Value("${spring.main.data.cassandra.password}")
	private String cassandraPassword;


	public static void main(String[] args) {
		SpringApplication.run(BookGenieApplication.class, args);
	}

//	@RequestMapping("/user")
//	public String user(@AuthenticationPrincipal OAuth2User principal) {
//		System.out.println(principal.getAttributes());
//		return principal.getAttribute("name");
//	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withAuthCredentials(cassandraUsername, cassandraPassword).withKeyspace(keyspaceName).withCloudSecureConnectBundle(bundle);
	}
}
