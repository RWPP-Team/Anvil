package fixture;

public class Scope {
    public int ab;
    public int abc;
    public int A;
    public int a1;

    public static class Nested {
        public int a;
    }
}

interface Parent {
    void a();
}

class Child implements Parent {
    public void a() {}
}
