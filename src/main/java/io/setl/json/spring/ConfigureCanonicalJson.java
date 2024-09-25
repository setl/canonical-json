package io.setl.json.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.setl.json.jackson.JsonModule;

/**
 * Optional registration of a Jackson JsonModule.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
@Configuration
public class ConfigureCanonicalJson {

  /** New instance. */
  public ConfigureCanonicalJson() {
    // do nothing
  }


  /**
   * Register the Canonical JSON module.
   *
   * @return the module
   */
  @Bean
  @ConditionalOnExpression("${setl.json.enabled:true}")
  public com.fasterxml.jackson.databind.Module canonicalJsonModule() {
    return new JsonModule();
  }

}
