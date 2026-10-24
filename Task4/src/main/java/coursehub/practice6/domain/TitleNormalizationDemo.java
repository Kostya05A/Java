package coursehub.practice6.domain;

// две разные исходные строки дают один search key
public class TitleNormalizationDemo {
  public static void main(String[] args) {
    CourseTitle first = new CourseTitle("  Java   для   начинающих ");
    CourseTitle second = new CourseTitle("java для начинающих");

    System.out.println("display1:   '" + first.display() + "'");
    System.out.println("display2:   '" + second.display() + "'");
    System.out.println("key1:       '" + first.searchKey() + "'");
    System.out.println("key2:       '" + second.searchKey() + "'");
    System.out.println("same key:   " + first.searchKey().equals(second.searchKey()));

    TitleSearchIndex index = new TitleSearchIndex();
    index.add(first);
    index.add(second);

    // по нормализованному запросу находятся обе строки — это и есть конфликт
    System.out.println("find ' JAVA  для начинающих ': " + index.find(" JAVA  для начинающих "));
    System.out.println("collision: " + index.hasCollision("Java для начинающих"));
  }
}
