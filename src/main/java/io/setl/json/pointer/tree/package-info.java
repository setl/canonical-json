/**
 * The motivation for this extension to the standard javax.json API is to enable the enforcement of a policy of what can be done with patches and what parts of
 * a JSON structure a user may legitimately create.
 *
 * <p>A user may create a structure if it is wholly contained within the PointerTree that they have create permission for.</p>
 *
 * <p>A user may apply a patch to a structure if all updated paths are in the PointerTree that they have update permission for.</p>
 *
 * <p>A user may read the parts of a structure that their PointerTree can copy for them.</p>
 *
 * @author Simon Greatrix on 18/02/2020.
 */
package io.setl.json.pointer.tree;