package io.setl.json.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 15/01/2020.
 */
public class MutableLocationTest {

  @Test
  public void test() {
    MutableLocation l = new MutableLocation();
    assertEquals(0, l.getStreamOffset());
    l.update('a');
    l.update('b');
    l.update('c');
    assertEquals(3, l.getStreamOffset());
    assertEquals(3, l.getColumnNumber());
    assertEquals(1, l.getLineNumber());

    l.update('\r');
    l.update('\n');

    assertEquals(5, l.getStreamOffset());
    assertEquals(0, l.getColumnNumber());
    assertEquals(2, l.getLineNumber());

    l.update('\n');
    l.update('a');

    assertEquals(7, l.getStreamOffset());
    assertEquals(1, l.getColumnNumber());
    assertEquals(3, l.getLineNumber());

    l.update('\t');
    assertEquals(8, l.getStreamOffset());
    assertEquals(9, l.getColumnNumber());

    l.update('1');
    l.update('2');
    l.update('3');
    l.update('4');
    l.update('5');
    l.update('6');
    l.update('7');
    assertEquals(15, l.getStreamOffset());
    assertEquals(16, l.getColumnNumber());

    l.update('\t');
    assertEquals(16, l.getStreamOffset());
    assertEquals(17, l.getColumnNumber());
  }


  @Test
  public void testSetters() {
    MutableLocation l = new MutableLocation();
    l.setLineNumber(200);
    l.setColumnNumber(100);
    l.setStreamOffset(300);
    assertEquals(300, l.getStreamOffset());
    assertEquals(200, l.getLineNumber());
    assertEquals(100, l.getColumnNumber());

    assertEquals("Location(columnNumber=100, lineNumber=200, streamOffset=300)", l.toString());
  }

}