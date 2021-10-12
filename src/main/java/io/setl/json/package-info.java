/**
 * This package implements the creation and verification of canonical JSON.
 *
 * <p>The definition of canonical JSON was taken from the <a href="http://gibson042.github.io/canonicaljson-spec/">canonicaljson</a>
 * project. The definition is reproduced below for convenience.</p>
 *
 * <h2 id="definition">Definition</h2>
 *
 * <p>JSON text in canonical form:</p>
 * <ol>
 *   <li>MUST be encoded in <a href="https://tools.ietf.org/html/rfc3629">UTF-8</a></li>
 *   <li>MUST NOT include insignificant (i.e., inter-token) whitespace (defined in <a href="https://tools.ietf.org/html/rfc7159#section-2">section 2 of
 *   RFC 7159</a>)</li>
 *   <li>MUST order the members of all objects lexicographically by the UCS (Unicode Character Set) code points of their names
 *     <ol>
 *       <li>preserving and utilizing the code points in U+D800 through U+DFFF (inclusive) for all lone surrogates</li>
 *     </ol>
 *   </li>
 *   <li>MUST represent all integer numbers (those with a zero-valued fractional part)
 *     <ol>
 *       <li>If the integer number has thirty or less trailing zeros:
 *         <ol>
 *           <li>without a leading minus sign when the value is zero, and</li>
 *           <li>without a decimal point, and</li>
 *           <li>without an exponent, and</li>
 *           <li>without insignificant leading zeroes (as already required of all JSON numbers)</li>
 *         </ol>
 *       </li>
 *       <li>If the integer number has more than thirty trailing zeroes, use the rules for non-integer numbers. This prevents attacks by using values such as
 *       "1e+1000000", which would otherwise require a million zeros.</li>
 *     </ol>
 *   </li>
 *   <li>MUST represent all non-integer numbers in exponential notation
 *     <ol>
 *       <li>including a nonzero single-digit significand integer part, and</li>
 *       <li>including a nonempty significand fractional part, and</li>
 *       <li>including no trailing zeroes in the significand fractional part (other than as part of a “.0” required to satisfy the preceding point), and</li>
 *       <li>including a capital “E”, and</li>
 *       <li>including no plus sign in the exponent, and</li>
 *       <li>including no insignificant leading zeroes in the exponent</li>
 *     </ol>
 *   </li>
 *   <li>MUST represent all strings (including object member names) in their minimal-length UTF-8 encoding
 *     <ol>
 *       <li>avoiding escape sequences for characters except those otherwise inexpressible in JSON (U+0022 QUOTATION MARK, U+005C REVERSE SOLIDUS, and ASCII
 *       control characters U+0000 through U+001F) or UTF-8 (U+D800 through U+DFFF), and</li>
 *       <li>avoiding escape sequences for combining characters, variation selectors, and other code points that affect preceding characters, and</li>
 *       <li>using two-character escape sequences where possible for characters that require escaping:
 *         <ul>
 *           <li><code>&#92;b</code> U+0008 BACKSPACE</li>
 *           <li><code>&#92;t</code> U+0009 CHARACTER TABULATION (“tab”)</li>
 *           <li><code>&#92;n</code> U+000A LINE FEED (“newline”)</li>
 *           <li><code>&#92;f</code> U+000C FORM FEED</li>
 *           <li><code>&#92;r</code> U+000D CARRIAGE RETURN</li>
 *           <li><code>&#92;"</code> U+0022 QUOTATION MARK</li>
 *           <li><code>&#92;&#92;</code> U+005C REVERSE SOLIDUS (“backslash”), and</li>
 *         </ul>
 *       </li>
 *       <li>using six-character <code>&#92;u00xx</code> uppercase hexadecimal escape sequences for control characters that require escaping but lack a
 *       two-character sequence, and</li>
 *       <li>using six-character <code>&#92;uDxxx</code> uppercase hexadecimal escape sequences for lone surrogates</li>
 *     </ol>
 *   </li>
 * </ol>
 *
 * @author Dr Simon Greatrix
 */
package io.setl.json;