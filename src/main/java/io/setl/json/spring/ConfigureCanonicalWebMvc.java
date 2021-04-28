package io.setl.json.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.setl.json.jackson.CanonicalFactory;
import io.setl.json.jackson.JsonModule;


/**
 * Configure handling of JSON via this library.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class ConfigureCanonicalWebMvc {

  /**
   * Configure the Web MVC to generate JSON responses in canonical form.
   *
   * @return the configurer
   */
  @Bean
  @ConditionalOnExpression("${setl.json.enabled:true}")
  public WebMvcConfigurer canonicalWebMvcConfigurer(
      @Autowired(required = false) Jackson2ObjectMapperBuilder mapperBuilder
  ) {
    if (mapperBuilder == null) {
      ObjectMapper mapper = new ObjectMapper(new CanonicalFactory());
      mapper.findAndRegisterModules();
      return new CanonicalWebMvcConfigurer(mapper);
    }

    mapperBuilder.factory(new CanonicalFactory());
    ObjectMapper mapper = mapperBuilder.build();

    // We cannot add a module to the builder, only over-ride what is already configured, nor can we discover what modules are already specified, so the only way
    // not to break the configuration is to manually add our module after building the mapper.
    JsonModule myModule = new JsonModule();
    if (!mapper.getRegisteredModuleIds().contains(myModule.getTypeId())) {
      mapper.registerModule(myModule);
    }

    return new CanonicalWebMvcConfigurer(mapper);
  }

}