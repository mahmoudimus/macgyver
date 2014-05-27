package io.macgyver.core;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Simple wrapper to start server.
 * 
 * @author rschoening
 * 
 */

@Configuration
@ComponentScan(basePackages = { "io.macgyver.config",
		"io.macgyver.plugin.config", "io.macgyver.core.config" })
@EnableAutoConfiguration
public class ServerMain {

	static Logger logger = org.slf4j.LoggerFactory.getLogger(ServerMain.class);

	public static void main(String[] args) throws Exception {

	
		System.setProperty("spring.gsp.reloadingEnabled","true");
		System.setProperty("spring.gsp.templateRoots","file:./web/templates,classpath:/templates");
		
		Bootstrap.getInstance();
		Bootstrap.getInstance();

		SpringApplication app = new SpringApplication(ServerMain.class);
		app.setShowBanner(false);
		ConfigurableApplicationContext ctx = app.run(args);

		Environment env = ctx.getEnvironment();

		logger.info("Spring environment: {}", env);

	}


}
