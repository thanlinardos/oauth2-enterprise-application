package com.thanlinardos.resource_server.model;

import com.thanlinardos.resource_server.misc.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class Interval implements Comparable<Interval> {

    public static final LocalDate MIN_DATE = LocalDate.MIN;
    public static final LocalDate MAX_DATE = LocalDate.MAX;
    public static final Comparator<Interval> INTERVAL_COMPARATOR = Comparator.comparing(Interval::getStartNullAsMin).thenComparing(Interval::getEndNullAsMax);

    @Nullable
    private LocalDate start;
    @Nullable
    private LocalDate end;

    public LocalDate getStartNullAsMin() {
        return start == null ? MIN_DATE : start;
    }

    public LocalDate getEndNullAsMax() {
        return end == null ? MAX_DATE : end;
    }

    public Interval getStartEndAsNull() {
        start = start == MIN_DATE ? null : start;
        end = end == MAX_DATE ? null : end;
        return this;
    }

    private static Collection<Pair<LocalDate, Integer>> getAllDates(Collection<Interval> intervals) {
        return intervals.stream()
                .map(interval -> List.of(Pair.of(interval.getStartNullAsMin(), 1), Pair.of(interval.getEndNullAsMax(), -1)))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(Comparator.comparing(Pair::getFirst))
                .toList();
    }

    public static Collection<Interval> split(Collection<Interval> intervals) {
        List<Interval> sortedIntervals = intervals.stream()
                .distinct()
                .sorted()
                .toList();
        Map<Integer, Interval> idToInterval = IntStream.range(0, sortedIntervals.size())
                .mapToObj(i -> Map.entry(i + 1, sortedIntervals.get(i)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Collection<Pair<LocalDate, Integer>> allDates = getAllDates(sortedIntervals);
        return IntStream.range(1, sortedIntervals.size() + 1)
                .mapToObj(i -> List.of(
                        Map.entry(i, idToInterval.get(i).getStartNullAsMin()),
                        Map.entry(i * (-1), idToInterval.get(i).getEndNullAsMax())
                ))
                .flatMap(Collection::stream)
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> Map.entry(getIdOrOppositeIdInterval(idToInterval, entry.getKey()), entry))
                .map(entry -> mapToNextContainedInterval(entry, allDates))
                .map(Interval::getStartEndAsNull)
                .distinct()
                .toList();
    }

    private static Interval getIdOrOppositeIdInterval(Map<Integer, Interval> idToInterval, Integer id) {
        return idToInterval.getOrDefault(id, idToInterval.get(id * (-1)));
    }

    private static Interval mapToNextContainedInterval(Map.Entry<Interval, Map.Entry<Integer, LocalDate>> entry, Collection<Pair<LocalDate, Integer>> allDates) {
        LocalDate currentDate = entry.getValue().getValue();
        Stream<Pair<LocalDate, Integer>> localDateStream = allDates.stream()
                .filter(date -> DateUtils.isNotEqualTo(date.getFirst(), currentDate))
                .filter(date -> entry.getKey().containsNullAsMax(date.getFirst()));
        Pair<LocalDate, Integer> foundEndPair;
        if (entry.getValue().getKey() > 0) {
            assert entry.getKey().getEnd() != null;
            foundEndPair = localDateStream
                    .min(Comparator.comparing(Pair::getFirst))
                    .orElse(Pair.of(entry.getKey().getEnd(), -1));
        } else {
            assert entry.getKey().getStart() != null;
            foundEndPair = localDateStream
                    .max(Comparator.comparing(Pair::getFirst))
                    .orElse(Pair.of(entry.getKey().getStart(), 1));
        }
        LocalDate foundEnd = foundEndPair.getSecond() > 0 ? foundEndPair.getFirst().minusDays(1L) : foundEndPair.getFirst().plusDays(1L);
        if (DateUtils.isBefore(foundEnd, currentDate)) {
            return new Interval(foundEnd, currentDate);
        } else {
            return new Interval(currentDate, foundEnd);
        }
    }

    public boolean containsNullAsMin(@Nullable LocalDate date) {
        LocalDate nonNullDate = date == null ? MIN_DATE : date;
        return (start == null || !nonNullDate.isBefore(start)) && (end == null || !nonNullDate.isAfter(end));
    }

    public boolean containsNullAsMax(@Nullable LocalDate date) {
        LocalDate nonNullDate = date == null ? MAX_DATE : date;
        return (start == null || !nonNullDate.isBefore(start)) && (end == null || !nonNullDate.isAfter(end));
    }

    public boolean contains(@NotNull LocalDate date) {
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    @Override
    public int compareTo(@NotNull Interval interval) {
        return INTERVAL_COMPARATOR.compare(this, interval);
    }
}
