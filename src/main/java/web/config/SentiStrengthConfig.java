package web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.ac.wlv.sentistrength.SentiStrength;

@Configuration
public class SentiStrengthConfig {

  @Bean()
  @Scope("request")
  public SentiStrength sentiStrength() {
    return new SentiStrength();
  }
}
