package com.tms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Storage storage = new Storage();
    private String frontendUrl = "http://localhost:5173";

    @Getter @Setter
    public static class Jwt {
        private String secret;
        private long accessExpiryMs = 900000;
        private long refreshExpiryMs = 604800000;
    }

    @Getter @Setter
    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";
    }

    @Getter @Setter
    public static class Storage {
        private String type = "local";
        private String localPath = "./uploads";
        private String s3Bucket;
        private String s3Region = "us-east-1";
    }
}
