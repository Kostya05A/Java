package task2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class TopicIndex {
    private TopicIndex() {
    }

    public static List<String> uniqueTopics(List<CourseWithModules> courses) {
        return courses.stream()
                .flatMap(course -> course.modules().stream())
                .flatMap(module -> module.topics().stream())
                .map(TopicNormalizer::normalize)
                .filter(topic -> !topic.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    public static Map<String, List<String>> reverseIndex(List<CourseWithModules> courses) {
        Map<String, List<String>> index = courses.stream()
                .flatMap(course -> course.modules().stream()
                        .flatMap(module -> module.topics().stream())
                        .map(TopicNormalizer::normalize)
                        .filter(topic -> !topic.isEmpty())
                        .distinct()
                        .map(topic -> Map.entry(topic, course.code())))
                .distinct()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        TreeMap::new,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.collectingAndThen(
                                        Collectors.toCollection(TreeSet::new),
                                        set -> List.copyOf(set)))));
        return Collections.unmodifiableMap(index);
    }
}
