package cz.cuni.mff.mongodb_java.morphia;

import java.time.LocalDate;

abstract class T {
    public T(int a) {
        System.out.println("T int");
    }
    public T() {
        System.out.println("T empty");
    }
}

class TT extends T {
    public TT() {
        System.out.println("TT empty");
    }

    public TT(int a) {
        System.out.println("TT int");
    }

    public TT(int a, int b) {
        System.out.println("TT int int");
    }
}


public class MainTest {

    public static void main(String[] args) {

        System.out.println(
                LocalDate.parse("1996-01-02")
        );

    }

}
