package io.setl.json.pointer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import javax.json.JsonArray;
import javax.json.JsonPointer;

import org.junit.Test;

import io.setl.json.CJArray;
import io.setl.json.builder.ArrayBuilder;
import io.setl.json.exception.PointerIndexException;
import io.setl.json.io.ReaderFactory;

/**
 * @author Simon Greatrix on 27/01/2020.
 */
public class ArrayPathTest {

  JsonArray array = new ArrayBuilder().add(1).add(new ArrayBuilder().add(0).add(1)).build();


  @Test
  public void containsPointer() {
    JsonExtendedPointer pointer = PointerFactory.create("/2/1");
    JsonExtendedPointer pointer2 = PointerFactory.create("/2/1");
    assertTrue(pointer.isParentOf(pointer2));
    pointer2 = PointerFactory.create("/2/1/a/b");
    assertTrue(pointer.isParentOf(pointer2));
    assertFalse(pointer2.isParentOf(pointer));
    pointer2 = PointerFactory.create("/2/a/b");
    assertFalse(pointer.isParentOf(pointer2));
    pointer2 = PointerFactory.create("/2/a");
    assertFalse(pointer.isParentOf(pointer2));
    pointer2 = PointerFactory.create("");
    assertFalse(pointer.isParentOf(pointer2));
  }


  @Test
  public void containsValue() {
    JsonPointer pointer = PointerFactory.create("/2/1");
    assertFalse(pointer.containsValue(array));
  }


  @Test
  public void copy() {
    JsonArray array = new ReaderFactory().createReader(new StringReader("[1,[0,{\"a\":true}]]")).readArray();
    JsonExtendedPointer pointer = PointerFactory.create("/1/1/a");
    JsonArray out = new CJArray();
    pointer.copy(array, out);
    assertEquals("[null,[null,{\"a\":true}]]", out.toString());

    array = new ReaderFactory().createReader(new StringReader("[1,{\"1\":{\"a\":true}}]")).readArray();
    pointer = PointerFactory.create("/1/1/a");
    out = new CJArray();
    pointer.copy(array, out);
    assertEquals("[null,{\"1\":{\"a\":true}}]", out.toString());

    pointer = PointerFactory.create("/4/1/a");
    out = new CJArray();
    pointer.copy(array, out);
    assertEquals("[null,null,null,null,null]", out.toString());
  }


  @Test(expected = PointerIndexException.class)
  public void getValue() {
    JsonPointer pointer = PointerFactory.create("/2/foo");
    pointer.getValue(array);
  }

}
