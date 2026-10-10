package level2;

public class Demo6 {

    public static void main(String[] args) {
        System.out.println("=== Задание 6. Граница перевода исключения ===");
        CourseImportService importer = new CourseImportService();

        System.out.println();
        System.out.println("--- 1. Низкоуровневый парсер бросает NumberFormatException (это " +
                "не наша ответственность) ---");
        try {
            RawNumberParser.parseHours("сорок");
        } catch (NumberFormatException ex) {
            System.out.println("  тип:     " + ex.getClass().getSimpleName());
            System.out.println("  текст:   " + ex.getMessage());
            System.out.println("  => из этого текста НЕПОНЯТНО: какая строка, какое поле, что делать");
        }

        System.out.println();
        System.out.println("--- 2. Уровень импорта ловит ТОЛЬКО NumberFormatException ---");
        try {
            importer.parseHours(3, "JAVA-101;Основы Java;сорок", "сорок");
        } catch (CourseImportException ex) {
            System.out.println("  тип:           " + ex.getClass().getSimpleName());
            System.out.println("  recordNumber:  " + ex.recordNumber());
            System.out.println("  rawRecord:     " + ex.rawRecord());
            System.out.println("  getMessage():  " + ex.getMessage());
            System.out.println("  cause:         " + ex.getCause().getClass().getSimpleName());
        }

        System.out.println();
        System.out.println("--- 3. Причина сохранена: цепочка читается целиком ---");
        try {
            importer.parseHours(3, "JAVA-101;Основы Java;сорок", "сорок");
        } catch (CourseImportException ex) {
            Throwable current = ex;
            int depth = 0;
            while (current != null) {
                System.out.println("  [" + depth + "] " + current.getClass().getName()
                        + ": " + current.getMessage());
                current = current.getCause();
                depth++;
            }
            System.out.println("  Причина не потеряна - можно посмотреть и исходный stack trace");
        }

        System.out.println();
        System.out.println("--- 4. Успешный случай: исключения нет ---");
        try {
            int hours = importer.parseHours(1, "JAVA-101;Основы Java;40", "40");
            System.out.println("  разобрано: " + hours + " ч");
        } catch (CourseImportException ex) {
            System.out.println("  НЕОЖИДАННО: " + ex.getMessage());
        }

        System.out.println();
        System.out.println("--- 5. НЕ ловим Exception целиком: чужая ошибка пролетает насквозь ---");
        System.out.println("  Мы ловим только NumberFormatException.");
        System.out.println("  Если внутри случится NullPointerException или IOException -");
        System.out.println("  он НЕ будет пойман и НЕ будет обёрнут. Это правильно:");
        System.out.println("  ловец Exception спрятал бы баг от себя и от разработчика.");
        try {
            importer.parseHours(1, "JAVA-101;Основы Java;", null);
        } catch (CourseImportException ex) {
            System.out.println("  поймано как CourseImportException (NumberFormatException/NPE): " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.out.println("  пролетело насквозь: " + ex.getClass().getSimpleName()
                    + " - его ловить нельзя, это баг программиста");
        }
    }
}
