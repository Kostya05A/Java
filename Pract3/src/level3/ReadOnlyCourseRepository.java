package src.level3;

public interface ReadOnlyCourseRepository extends ReadableCourseRepository {
    // Не наследует save() - не обещает невозможного
}