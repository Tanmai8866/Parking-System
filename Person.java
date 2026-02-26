public abstract class Person {
    protected String name;
    protected String password;

    public Person(String n, String p) {
        this.name = n;
        this.password = p;
    }

    public abstract void displayInfo();
}
