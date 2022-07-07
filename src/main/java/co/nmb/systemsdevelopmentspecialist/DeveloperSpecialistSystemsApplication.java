package co.nmb.systemsdevelopmentspecialist;

import co.nmb.systemsdevelopmentspecialist.configs.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties({FileStorageProperties.class})
@EnableWebSecurity
public class DeveloperSpecialistSystemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeveloperSpecialistSystemsApplication.class, args);
    }

}
