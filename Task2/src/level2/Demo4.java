package level2;

import java.io.IOException;

public class Demo4 {

    public static void main(String[] args) {
        System.out.println("=== Задание 4. try-catch-finally ===");
        System.out.println();

        System.out.println("--- 1. Обычный случай: try -> catch -> finally ---");
        scenario1();

        System.out.println();
        System.out.println("--- 2. return в try: finally всё равно выполняется ---");
        scenario2();

        System.out.println();
        System.out.println("--- 3. throw в try, finally БЕЗ return: исключение пробрасывается ---");
        scenario3();

        System.out.println();
        System.out.println("--- 4. ВОЗВРАТ ИЗ FINALLY ПОДАВЛЯЕТ ИСКЛЮЧЕНИЕ (главный трюк задания) ---");
        scenario4();

        System.out.println();
        System.out.println("--- 5. То же самое, но return в finally подменяет и значение ---");
        scenario5();

        System.out.println();
        System.out.println("--- 6. Своё исключение из finally перебивает исходное ---");
        scenario6();
    }

    private static void scenario1() {
        try {
            trace("try");
            int value = 10 / 2;
            trace("try: результат " + value);
        } catch (ArithmeticException ex) {
            trace("catch: " + ex.getMessage());
        } finally {
            trace("finally");
        }
    }

    private static void scenario2() {
        String result = returnFromTry();
        trace("вызванный метод вернул: " + result);
    }

    private static String returnFromTry() {
        try {
            trace("try: возвращаю 'из try'");
            return "из try";
        } finally {

            trace("finally: выполнился, несмотря на return в try");
        }
    }

    private static void scenario3() {
        try {
            throwFromTry();
        } catch (IllegalStateException ex) {
            trace("вызывающий поймал: " + ex.getMessage());
        }
    }

    private static void throwFromTry() {
        try {
            trace("try: бросаю");
            throw new IllegalStateException("ошибка из try");
        } finally {
            trace("finally: выполнился, исключение не тронул");
        }
    }

    private static void scenario4() {
        boolean сработалCatch = false;
        try {
            throwFromTryButReturnInFinally();
        } catch (IllegalStateException ex) {
            сработалCatch = true;
            trace("ЭТОТ БЛОК НЕ ВЫВОДИТСЯ: " + ex.getMessage());
        }
        trace("catch сработал? " + сработалCatch);
        trace("программа жива и считает, что всё хорошо - вот чем опасен return из finally");
    }

    private static void throwFromTryButReturnInFinally() {
        try {
            trace("try: бросаю, но это будет скрыто");
            throw new IllegalStateException("это исключение никто не увидит");
        } finally {
            trace("finally: return подавляет исключение выше по стеку");
            return;
        }
    }

    private static void scenario5() {
        String result = methodWithReturnInFinally(42);
        trace("передал 42, получил: \"" + result + "\"");
    }

    private static String methodWithReturnInFinally(int hours) {
        try {
            if (hours > 40) {
                throw new IllegalArgumentException("слишком много часов");
            }
            return "ок: " + hours;
        } finally {
            return "ok";
        }
    }

    private static void scenario6() {
        try {
            try {
                trace("try: бросаю исходное");
                throw new IllegalStateException("исходная ошибка");
            } finally {
                trace("finally: бросаю свою ошибку");
                throw new IOException("ошибка при освобождении ресурса");
            }
        } catch (Exception ex) {
            trace("поймали: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            trace("а исходная ошибка? " + (ex.getCause() == null ? "ПОТЕРЯНА" : "сохранена как cause"));
        }
    }

    private static void trace(String message) {
        System.out.println("    " + message);
    }
}
