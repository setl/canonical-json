package io.setl.json.patch.path;

import java.util.Objects;

public class ObjectPath implements Path {
    private final String key;
    private final Path parent;

    public ObjectPath(Path parent, String key) {
        this.key = key;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectPath)) return false;
        ObjectPath that = (ObjectPath) o;
        return key.equals(that.key) &&
                parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, parent);
    }

    @Override
    public Path getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return parent.toString() + "/" + key;
    }
}
