package folletto.toyproject.global.config;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasbinConfig {

    @Bean
    public Enforcer enforcer() {
        // 역슬래시 대신 슬래시 사용
        return new Enforcer("C:/dev/toy-project/src/main/resources/casbin/model.conf", "C:/dev/toy-project/src/main/resources/casbin/policy.csv");
    }
}