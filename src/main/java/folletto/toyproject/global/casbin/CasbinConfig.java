package folletto.toyproject.global.casbin;

import java.io.IOException;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class CasbinConfig {

    private final ResourceLoader resourceLoader;

    public CasbinConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Enforcer enforcer() throws IOException {
        Resource modelResource = resourceLoader.getResource("classpath:casbin/model.conf");
        Resource policyResource = resourceLoader.getResource("classpath:casbin/policy.csv");

        return new Enforcer(modelResource.getFile().getAbsolutePath(), policyResource.getFile().getAbsolutePath());
    }
}