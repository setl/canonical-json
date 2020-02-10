package io.setl.json.patch.path;

import io.setl.json.pointer.JPointer;

import java.util.Objects;

public class ArrayPath implements Path {

    private final Path parent;
    private final int index;

    public ArrayPath(Path parent, int index) {
        this.parent = parent;
        this.index = index;
    }

    @Override
    public Path getParent() {
        return parent;
    }

    public JPointer pointer(JPointerBuilder builder) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayPath)) return false;
        ArrayPath arrayPath = (ArrayPath) o;
        return parent.equals(arrayPath.parent);
    }

    @Override
    public int hashCode() {
        return parent.hashCode() *31 + 1031;
    }
}
