package io.setl.json.spring;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Enable JSON input and output.
 *
 * @author Simon Greatrix on 12/02/2020.
 */
class CanonicalWebMvcConfigurer implements WebMvcConfigurer {

  final ObjectMapper mapper;


  public CanonicalWebMvcConfigurer(ObjectMapper mapper) {
    this.mapper = mapper;
  }


  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingJackson2HttpMessageConverter myConverter = new MappingJackson2HttpMessageConverter(mapper);
    int index = -1;
    for (int i = 0; i < converters.size(); i++) {
      if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
        index = i;
        break;
      }
    }

    if (index == -1) {
      converters.add(myConverter);
    } else {
      converters.set(index, myConverter);
    }
  }

}
