package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTitleTest {
    @Test
    void keepsDisplayValueAndBuildsNormalizedSearchKey() {
        CourseTitle title = new CourseTitle("  Java   для   начинающих ");

        assertEquals("  Java   для   начинающих ", title.display());
        assertEquals("java для начинающих", title.searchKey());
    }

    @Test
    void normalizesUnicodeAndWhitespace() {
        String fullwidth = "\uFF2A\uFF21\uFF36\uFF21\u3000\uFF11\uFF10\uFF11";

        assertEquals("java 101", CourseTitle.searchKeyOf(fullwidth));
        assertEquals("java для начинающих",
                CourseTitle.searchKeyOf("JAVA\n\tдля   начинающих"));
    }

    @Test
    void caseFoldingUsesFixedLocale() {
        assertEquals("i", CourseTitle.searchKeyOf("I"));
    }

    @Test
    void blankTitleHasEmptySearchKey() {
        assertEquals("", CourseTitle.searchKeyOf(" \t\n "));
    }

    @Test
    void rejectsNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> new CourseTitle(null));
    }

    @Test
    void indexFindsBothDifferentSourceTitlesByOneKey() {
        CourseTitle first = new CourseTitle("  Java   для   начинающих ");
        CourseTitle second = new CourseTitle("java для начинающих");
        TitleSearchIndex index = new TitleSearchIndex();
        index.add(first);
        index.add(second);

        assertEquals(2, index.find(" JAVA  для начинающих ").size());
        assertTrue(index.hasCollision("Java для начинающих"));
        assertTrue(index.find("Python").isEmpty());
    }
}
