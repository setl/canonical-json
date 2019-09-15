package com.pippsford.json;

import java.io.*;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;

public class CanonicalFactory extends JsonFactory {

  public CanonicalFactory() {
    // as super-class
  }


  public CanonicalFactory(ObjectCodec objectCodec) {
    super(objectCodec);
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
    if( enc != JsonEncoding.UTF8 ) {
      throw new IllegalArgumentException("Canonical encoding must be UTF-8, not " + enc);
    }
    
    
    // TODO Auto-generated method stub
    return super.createGenerator(out, enc);
  }


  @Override
  public JsonGenerator createGenerator(OutputStream out) throws IOException {
    // TODO Auto-generated method stub
    return super.createGenerator(out);
  }


  @Override
  public JsonGenerator createGenerator(Writer w) throws IOException {
    // TODO Auto-generated method stub
    return super.createGenerator(w);
  }


  @Override
  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
    if( enc != JsonEncoding.UTF8 ) {
      throw new IllegalArgumentException("Canonical encoding must be UTF-8, not " + enc);
    }
    return super.createGenerator(f, enc);
  }
}
