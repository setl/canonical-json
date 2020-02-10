package io.setl.json.patch.path;

public class RootPath implements Path {
    /**
     * The singleton root of a path.
     */
    public static Path ROOT = new RootPath();

    @Override
    public Path getParent() {
        return null;
    }
}
