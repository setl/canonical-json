package io.setl.json;

/**
 * Marker interface for anything that converted to text in pretty or canonical form.
 */
public interface FormattedJson {

  /**
   * Output the specification of this as a JSON structure in canonical form.
   *
   * @return the JSON structure in canonical form
   */
  String toCanonicalString();


  /**
   * Output the specification of this as a JSON structure in pretty form.
   *
   * @return the JSON structure in pretty form
   */
  String toPrettyString();

}
