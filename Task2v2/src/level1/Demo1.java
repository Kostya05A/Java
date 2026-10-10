import java.util.Scanner;

public class Demo1 {
    public static void method1(){
        System.out.println("Метод 1");
        method2();
    }

    public static void method2(){
        System.out.println("Метод 2");
        method3();
    }

    public static void method3(){
        System.out.println("Метод 3");
        method4();
    }

    public static void method4(){
        throw new RuntimeException("ВЫЗОВ ИСКЛЮЧЕНИЙ");   
    }

    public static void main (String[] args) {
        try{
            method1();
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("В работе..."); 
    }
}