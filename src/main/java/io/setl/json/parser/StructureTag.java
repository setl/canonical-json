package io.setl.json.parser;

/**
 * @author Simon Greatrix on 24/01/2020.
 */
class StructureTag {

  StructureTag parent;


  StructureTag(StructureTag parent) {
    this.parent = parent;
  }
}
