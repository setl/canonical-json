package io.setl.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

import io.setl.json.exception.NotJsonException;
import io.setl.json.primitive.CJFalse;
import io.setl.json.primitive.CJNull;
import io.setl.json.primitive.CJString;
import io.setl.json.primitive.CJTrue;
import io.setl.json.primitive.numbers.CJNumber;

/** Helper for casting and creating Canonicals. */
class CanonicalCreator {

  /** The creators that can be used to create a Canonical from an object. */
  private static final List<CreateOp> CREATORS;



  /** An operation to create a Canonical from an object. */
  abstract static class CreateOp {

    /**
     * Create a Canonical from the value.
     *
     * @param value the value
     *
     * @return the Canonical
     */
    abstract Canonical create(Object value);


    /**
     * Test if this operation can be applied to the value.
     *
     * @param value the value
     *
     * @return true if it can be applied
     */
    abstract boolean test(Object value);

  }


  /**
   * Create a Canonical from a JsonValue. If at all possible, the original object is returned.
   *
   * @param value the value
   *
   * @return the Canonical
   */
  static Canonical cast(JsonValue value) {
    if (value == null) {
      return CJNull.NULL;
    }
    if (value instanceof Canonical) {
      return (Canonical) value;
    }
    switch (value.getValueType()) {
      case ARRAY:
        return CJArray.asArray(value.asJsonArray());
      case FALSE:
        return CJFalse.FALSE;
      case NUMBER:
        return CJNumber.castUnsafe(((JsonNumber) value).numberValue());
      case NULL:
        return CJNull.NULL;
      case OBJECT:
        return CJObject.asJObject(value.asJsonObject());
      case STRING:
        return CJString.create(((JsonString) value).getString());
      case TRUE:
        return CJTrue.TRUE;
      default:
        throw new NotJsonException("Unknown Json Value type:" + value.getValueType());
    }
  }


  /**
   * Do the best effort conversion of any object to a Canonical, creating a new Primitive to represent the values where appropriate.
   *
   * @param value the value
   *
   * @return the Canonical
   */
  static Canonical create(Object value) {
    for (CreateOp op : CREATORS) {
      if (op.test(value)) {
        return op.create(value);
      }
    }
    throw new NotJsonException(value);
  }

  static {
    CREATORS = List.of(
        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJNull.NULL;
          }


          @Override
          boolean test(Object value) {
            return value == null;
          }
        },

        new CreateOp() {
          Canonical create(Object value) {
            return ((Canonical) value).copy();
          }


          @Override
          boolean test(Object value) {
            return value instanceof Canonical;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return cast((JsonValue) value);
          }


          @Override
          boolean test(Object value) {
            return (value instanceof JsonValue);
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return ((Boolean) value).booleanValue() ? CJTrue.TRUE : CJFalse.FALSE;
          }


          @Override
          boolean test(Object value) {
            return (value instanceof Boolean);
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return ((AtomicBoolean) value).get() ? CJTrue.TRUE : CJFalse.FALSE;
          }


          @Override
          boolean test(Object value) {
            return value instanceof AtomicBoolean;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJString.create((String) value);
          }


          @Override
          boolean test(Object value) {
            return value instanceof String;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJNumber.castUnsafe((Number) value);
          }


          @Override
          boolean test(Object value) {
            return value instanceof Number;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJArray.asArray((Collection<?>) value);
          }


          @Override
          boolean test(Object value) {
            return value instanceof Collection<?>;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJObject.asJObject((Map<?, ?>) value);
          }


          @Override
          boolean test(Object value) {
            return value instanceof Map<?, ?>;
          }
        },

        new CreateOp() {
          @Override
          Canonical create(Object value) {
            return CJArray.asArrayFromArray(value);
          }


          @Override
          boolean test(Object value) {
            return value.getClass().isArray();
          }
        }
    );
  }
}
