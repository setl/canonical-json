package com.pippsford.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Simon Greatrix on 16/09/2019.
 */
public class BaseGenerator extends JsonGenerator {

  @Override
  public JsonGenerator setCodec(ObjectCodec oc) {
    return null;
  }


  @Override
  public ObjectCodec getCodec() {
    return null;
  }


  @Override
  public Version version() {
    return null;
  }


  @Override
  public JsonGenerator enable(Feature f) {
    return null;
  }


  @Override
  public JsonGenerator disable(Feature f) {
    return null;
  }


  @Override
  public boolean isEnabled(Feature f) {
    return false;
  }


  @Override
  public int getFeatureMask() {
    return 0;
  }


  @Override
  public JsonGenerator setFeatureMask(int values) {
    return null;
  }


  @Override
  public JsonGenerator useDefaultPrettyPrinter() {
    return null;
  }


  @Override
  public void writeStartArray() throws IOException {

  }


  @Override
  public void writeEndArray() throws IOException {

  }


  @Override
  public void writeStartObject() throws IOException {

  }


  @Override
  public void writeEndObject() throws IOException {

  }


  @Override
  public void writeFieldName(String name) throws IOException {

  }


  @Override
  public void writeFieldName(SerializableString name) throws IOException {

  }


  @Override
  public void writeString(String text) throws IOException {

  }


  @Override
  public void writeString(char[] text, int offset, int len) throws IOException {

  }


  @Override
  public void writeString(SerializableString text) throws IOException {

  }


  @Override
  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {

  }


  @Override
  public void writeUTF8String(byte[] text, int offset, int length) throws IOException {

  }


  @Override
  public void writeRaw(String text) throws IOException {

  }


  @Override
  public void writeRaw(String text, int offset, int len) throws IOException {

  }


  @Override
  public void writeRaw(char[] text, int offset, int len) throws IOException {

  }


  @Override
  public void writeRaw(char c) throws IOException {

  }


  @Override
  public void writeRawValue(String text) throws IOException {

  }


  @Override
  public void writeRawValue(String text, int offset, int len) throws IOException {

  }


  @Override
  public void writeRawValue(char[] text, int offset, int len) throws IOException {

  }


  @Override
  public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {

  }


  @Override
  public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
    return 0;
  }


  @Override
  public void writeNumber(int v) throws IOException {

  }


  @Override
  public void writeNumber(long v) throws IOException {

  }


  @Override
  public void writeNumber(BigInteger v) throws IOException {

  }


  @Override
  public void writeNumber(double v) throws IOException {

  }


  @Override
  public void writeNumber(float v) throws IOException {

  }


  @Override
  public void writeNumber(BigDecimal v) throws IOException {

  }


  @Override
  public void writeNumber(String encodedValue) throws IOException {

  }


  @Override
  public void writeBoolean(boolean state) throws IOException {

  }


  @Override
  public void writeNull() throws IOException {

  }


  @Override
  public void writeObject(Object pojo) throws IOException {

  }


  @Override
  public void writeTree(TreeNode rootNode) throws IOException {

  }


  @Override
  public JsonStreamContext getOutputContext() {
    return null;
  }


  @Override
  public void flush() throws IOException {

  }


  @Override
  public boolean isClosed() {
    return false;
  }


  @Override
  public void close() throws IOException {

  }
}
