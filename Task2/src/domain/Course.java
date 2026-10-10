package domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Course {

    public static final int MIN_DURATION_HOURS = 10;

    public static final int MAX_DURATION_HOURS = 40;

    private final CourseCode code;
    private final String title;
    private final int capacity;

    private CourseStatus status = CourseStatus.OPEN;
    private int completedHours;
    private final Set<CourseCode> prerequisites = new LinkedHashSet<>();
    private final Set<Long> enrolledStudentIds = new LinkedHashSet<>();
    private final List<Long> waitlist = new ArrayList<>();

    public Course(CourseCode code, String title, int capacity) {
        if (code == null) {
            throw new IllegalArgumentException("код курса обязателен");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("название курса обязательно");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("вместимость должна быть положительной");
        }
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    public static Course of(String code, String title, int capacity) {
        return new Course(CourseCode.of(code), title, capacity);
    }

    public void addPrerequisite(CourseCode prerequisite) {
        prerequisites.add(prerequisite);
    }

    public void completeHours(int hours) {
        if (hours < MIN_DURATION_HOURS) {
            throw new IllegalArgumentException(
                    "длительность " + hours + " ч меньше минимума " + MIN_DURATION_HOURS + " ч");
        }
        if (hours > MAX_DURATION_HOURS) {
            throw new IllegalArgumentException(
                    "длительность " + hours + " ч больше максимума " + MAX_DURATION_HOURS + " ч");
        }
        this.completedHours = hours;
    }

    public int completedHours() {
        return completedHours;
    }

    public boolean isCompleted() {
        return completedHours > 0;
    }

    public void open() {
        this.status = CourseStatus.OPEN;
    }

    public void close() {
        this.status = CourseStatus.CLOSED;
    }

    public CourseStatus status() {
        return status;
    }

    public int freeSeats() {
        return capacity - enrolledStudentIds.size();
    }

    public boolean hasFreeSeat() {
        return freeSeats() > 0;
    }

    public boolean isEnrolled(long studentId) {
        return enrolledStudentIds.contains(studentId);
    }

    public boolean hasPrerequisite(CourseCode prerequisite) {
        return prerequisites.contains(prerequisite);
    }

    public Set<CourseCode> prerequisites() {
        return Set.copyOf(prerequisites);
    }

    public int enrolledCount() {
        return enrolledStudentIds.size();
    }

    public List<Long> waitlist() {
        return List.copyOf(waitlist);
    }

    public boolean isWaitlisted(long studentId) {
        return waitlist.contains(studentId);
    }

    public void enroll(long studentId) {
        if (status == CourseStatus.CLOSED) {
            throw new IllegalStateException("курс " + code + " закрыт");
        }
        if (!hasFreeSeat()) {
            throw new IllegalStateException("в курсе " + code + " нет свободных мест");
        }
        if (isEnrolled(studentId)) {
            throw new IllegalStateException("студент " + studentId + " уже зачислен в " + code);
        }
        enrolledStudentIds.add(studentId);
    }

    public void addToWaitlist(long studentId) {
        if (!waitlist.contains(studentId)) {
            waitlist.add(studentId);
        }
    }

    public boolean removeStudent(long studentId) {
        waitlist.remove(studentId);
        return enrolledStudentIds.remove(studentId);
    }

    public CourseCode code() {
        return code;
    }

    public String title() {
        return title;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Course[" + code + " '" + title + "', status=" + status
                + ", capacity=" + capacity
                + ", enrolled=" + enrolledStudentIds.size()
                + ", completedHours=" + completedHours + "]";
    }
}
