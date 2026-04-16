package cz.cuni.mff.mongodb_java.morphia;

abstract class T {
    public T(int a) {
        System.out.println("T int");
    }
    public T() {
        System.out.println("T empty");
    }
}

class TT extends T {
    public int a;

    public TT() {
        System.out.println("TT empty");
    }

    public TT(int a) {
        this.a = a;
        System.out.println("TT int");
    }

    public TT(int a, int b) {
        System.out.println("TT int int");
    }
}


public class MainTest {

    public static void main(String[] args) {


        /*
        List<TT> originalList = new ArrayList<TT>();
        originalList.add(new TT(1));
        originalList.add(new TT(2));
        originalList.add(new TT(3));

        List<TT> filteredList = originalList.stream()
                .filter(item -> item.a == 1)
                .collect(Collectors.toList());

        filteredList.forEach(item -> System.out.println(item.a));
*/
    }

}
