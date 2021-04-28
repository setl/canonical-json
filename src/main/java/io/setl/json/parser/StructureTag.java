package io.setl.json.parser;

/**
 * Representation of the current parse path into a JSON document.
 *
 * @author Simon Greatrix on 24/01/2020.
 */
class StructureTag {

  final StructureTag parent;


  StructureTag(StructureTag parent) {
    this.parent = parent;
  }

}
