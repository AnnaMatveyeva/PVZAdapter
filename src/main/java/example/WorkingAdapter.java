package example;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkingAdapter {

	public static void main(String[] args) {
		WorkSchedule ws = new WorkSchedule();
		ws.day = DayOfWeek.WEDNESDAY;
		ws.workingHours = new Interval();
		ws.workingHours.start = LocalTime.of(9, 0);
		ws.workingHours.end = LocalTime.of(18, 0);
		Interval intervalSu = new Interval();
		intervalSu.start = LocalTime.of(13, 30);
		intervalSu.end = LocalTime.of(14, 0);
		ws.breaks = Arrays.asList(intervalSu);


		WorkSchedule wsT = new WorkSchedule();
		wsT.day = DayOfWeek.TUESDAY;
		wsT.workingHours = new Interval();
		wsT.workingHours.start = LocalTime.of(9, 0);
		wsT.workingHours.end = LocalTime.of(18, 0);
		Interval intervalT = new Interval();
		intervalT.start = LocalTime.of(13, 30);
		intervalT.end = LocalTime.of(14, 0);
		wsT.breaks = Arrays.asList(intervalT);


		WorkSchedule ws2 = new WorkSchedule();
		ws2.day = DayOfWeek.THURSDAY;
		Interval wH = new Interval();
		wH.start = LocalTime.of(9, 0);
		wH.end = LocalTime.of(18, 0);
		ws2.workingHours = wH;
		Interval interval1 = new Interval();
		interval1.start = LocalTime.of(13, 30);
		interval1.end = LocalTime.of(14, 0);
		ws2.breaks = Arrays.asList(interval1);

		WorkSchedule ws1 = new WorkSchedule();
		ws1.day = DayOfWeek.MONDAY;
		ws1.workingHours = new Interval();
		Interval wsqI = new Interval();
		wsqI.start = LocalTime.of(9, 0);
		wsqI.end = LocalTime.of(18, 0);
		ws1.workingHours = wsqI;
		Interval interval = new Interval();
		interval.start = LocalTime.of(13, 0);
		interval.end = LocalTime.of(14, 0);
		Interval second = new Interval();
		second.start = LocalTime.of(16, 0);
		second.end = LocalTime.of(16, 10);
		ws1.breaks = Arrays.asList(interval,second);


		List<WorkSchedule> list = new ArrayList<>();

		list.add(ws);
		list.add(ws1);
		list.add(ws2);
		list.add(wsT);
		WorkScheduleAdapterImpl adapter = new WorkScheduleAdapterImpl();
		System.out.println(adapter.prepare(list));

	}
}


interface WorkScheduleAdapter {
	String prepare(List<WorkSchedule> list);
}

class WorkScheduleAdapterImpl implements WorkScheduleAdapter {

	@Override
	public String prepare(List<WorkSchedule> list) {
		StringBuilder result = new StringBuilder();
		List<WorkSchedule> sorted = list.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
		List<Interval> intervals = new LinkedList<>();
		sorted.stream().filter(workSchedule -> !intervals.contains(workSchedule.getWorkingHours())).forEach(workSchedule -> intervals.add(workSchedule.getWorkingHours()));

		Map<Interval, List<WorkSchedule>> groupByWorkingHours = new LinkedHashMap<>();
		intervals.forEach(interval -> groupByWorkingHours.put(interval, new LinkedList<>()));

		sorted.forEach(workSchedule -> groupByWorkingHours.get(workSchedule.getWorkingHours()).add(workSchedule));

		for (List<WorkSchedule> listSchedule : groupByWorkingHours.values()) {
			List<WorkSchedule> sortedSchedule = listSchedule.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
			Map<List<Interval>, Set<WorkSchedule>> byIntervals = new LinkedHashMap<>();

			for (int i = 0; i < sortedSchedule.size(); i++) {
				for (WorkSchedule workSchedule : sortedSchedule) {

					if (sortedSchedule.get(i).getBreaks().containsAll(workSchedule.getBreaks()) && workSchedule.getBreaks().containsAll(sortedSchedule.get(i).getBreaks())) {
						if (byIntervals.containsKey(sortedSchedule.get(i).getBreaks())) {
							byIntervals.get(workSchedule.getBreaks()).add(workSchedule);
						} else {
							Set<WorkSchedule> listToMap = new LinkedHashSet<>();
							listToMap.add(workSchedule);
							byIntervals.put(workSchedule.getBreaks(), listToMap);
						}
					} else {
						Set<WorkSchedule> listToMap = new LinkedHashSet<>();
						listToMap.add(workSchedule);
						if (byIntervals.containsKey(workSchedule.getBreaks()) && !byIntervals.get(workSchedule.getBreaks()).contains(workSchedule))
							byIntervals.put(workSchedule.getBreaks(), listToMap);
					}
				}
			}

			byIntervals.values().forEach(workSchedules -> {
				List<WorkSchedule> byBreaksSorted = workSchedules.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
				if (isInOrder(byBreaksSorted)) {
					result.append(getWorkingDay(byBreaksSorted.get(0))).append("-").append(getWorkingDay(byBreaksSorted.get(byBreaksSorted.size() - 1)));
				} else {
					byBreaksSorted.forEach(workSchedule -> result.append(getWorkingDay(workSchedule))
							.append(","));
					result.deleteCharAt(result.length() - 1);
				}
				result.append(": ")
						.append(getWorkingHours(byBreaksSorted.get(0)));
				result.append(getIntervals(byBreaksSorted.get(0)));
			});
		}
		result.append(findWeekend(list));

		return result.toString();
	}

	private boolean isInOrder(List<WorkSchedule> byBreaksSorted) {
		if (byBreaksSorted.size() < 3)
			return false;
		int order = byBreaksSorted.get(0).getDay().ordinal();
		for (int i = 1; i < byBreaksSorted.size(); i++) {
			if (byBreaksSorted.get(i).getDay().ordinal() == order + 1) {
				order++;
			} else return false;
		}
		return true;
	}

	private String getWorkingDay(WorkSchedule ws) {
		return ws.getDay().getDisplayName(TextStyle.SHORT, Locale.getDefault());
	}

	private String getWorkingHours(WorkSchedule ws) {
		return ws.getWorkingHours().getStart() + "-" + ws.getWorkingHours().getEnd() + ",";
	}

	private String getIntervals(WorkSchedule ws) {
		String result = "перерыв: " + ws.getBreaks().stream().map(interval -> interval.getStart() + "-" + interval.getEnd()).collect(Collectors.joining(","));
		return result + "<br/>";
	}

	private String findWeekend(List<WorkSchedule> list) {
		List<DayOfWeek> weekend = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
		List<DayOfWeek> toRemove = list.stream().map(WorkSchedule::getDay).collect(Collectors.toList());
		weekend.removeAll(toRemove);
		StringBuilder result = new StringBuilder();
		weekend.forEach(dayOfWeek -> result.append(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())).append(","));
		return result.deleteCharAt(result.length() - 1).append(" - выходной").toString();
	}
}
