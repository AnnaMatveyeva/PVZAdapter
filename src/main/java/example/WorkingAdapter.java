package example;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkingAdapter {

	public static void main(String[] args) {
		WorkSchedule ws = new WorkSchedule();
		ws.day = DayOfWeek.SATURDAY;
		ws.workingHours = new Interval();
		ws.workingHours.start = LocalTime.of(8, 0);
		ws.workingHours.end = LocalTime.of(17, 0);
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
		ws2.day = DayOfWeek.SUNDAY;
		Interval wH = new Interval();
		wH.start = LocalTime.of(9, 0);
		wH.end = LocalTime.of(18, 0);
		ws2.workingHours = wH;
		Interval interval1 = new Interval();
		interval1.start = LocalTime.of(13, 0);
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
		ws1.breaks = Arrays.asList(interval);


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

	private boolean isComparedIntervals = false;

	@Override
	public String prepare(List<WorkSchedule> list) {
		StringBuilder result = new StringBuilder();
		List<WorkSchedule> sorted = list.stream().sorted(Comparator.comparing(WorkSchedule::getDay).reversed()).collect(Collectors.toList());

		List<Interval> intervals = new ArrayList<>();
		for (WorkSchedule ws : list) {
			if (!intervals.contains(ws.getWorkingHours())) {
				intervals.add(ws.getWorkingHours());
			}
		}
		Map<Interval, List<WorkSchedule>> groupByWorkingHours = new HashMap<>();
		for (Interval interval : intervals) {
			groupByWorkingHours.put(interval, new ArrayList<>());
		}
		for (WorkSchedule ws : list) {
			groupByWorkingHours.get(ws.getWorkingHours()).add(ws);
		}

		System.out.println(groupByWorkingHours.values().size());


		for (List<WorkSchedule> listSchedule : groupByWorkingHours.values()) {

			List<WorkSchedule> byBreaks = new ArrayList<>();
			List<WorkSchedule> sortedSchedule = listSchedule.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());


			Map<List<Interval>, Set<WorkSchedule>> byIntervals = new HashMap<>();
			for (int i = 0; i < sortedSchedule.size(); i++) {
				for (int j = 0; j < sortedSchedule.size(); j++) {

					if (sortedSchedule.get(i).getBreaks().containsAll(sortedSchedule.get(j).getBreaks()) && sortedSchedule.get(j).getBreaks().containsAll(sortedSchedule.get(i).getBreaks())) {
						if (byIntervals.containsKey(sortedSchedule.get(i).getBreaks())) {
							byIntervals.get(sortedSchedule.get(j).getBreaks()).add(sortedSchedule.get(j));
						} else {
							Set<WorkSchedule> listToMap = new HashSet<>();
							listToMap.add(sortedSchedule.get(j));
							byIntervals.put(sortedSchedule.get(j).getBreaks(), listToMap);
						}
					} else {
						Set<WorkSchedule> listToMap = new HashSet<>();
						listToMap.add(sortedSchedule.get(j));
						if (byIntervals.containsKey(sortedSchedule.get(j).getBreaks()) && !byIntervals.get(sortedSchedule.get(j).getBreaks()).contains(sortedSchedule.get(j)))
							byIntervals.put(sortedSchedule.get(j).getBreaks(), listToMap);
					}
				}
			}

			for (Set<WorkSchedule> workSchedules : byIntervals.values()) {

				List<WorkSchedule> byBreaksSorted = workSchedules.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
				for (WorkSchedule workSchedule : byBreaksSorted) {
					result.append(getWorkingDay(workSchedule))
							.append(",");
				}
				result.deleteCharAt(result.length() - 1)
						.append(": ")
						.append(getWorkingHours(byBreaksSorted.get(0)));
				result.append(getIntervals(byBreaksSorted.get(0)));
			}
		}
		result.append(findWeekend(list));

		return result.toString();
	}


	private String getWorkingDay(WorkSchedule ws) {
		StringBuilder result = new StringBuilder();
		result.append(ws.day.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
		return result.toString();
	}

	private String getWorkingHours(WorkSchedule ws) {
		StringBuilder result = new StringBuilder();
		result.append(ws.workingHours.start).append("-").append(ws.workingHours.end).append(",");

		return result.toString();
	}

	private String getIntervals(WorkSchedule ws) {
		StringBuilder result = new StringBuilder();
		result.append("перерыв: ");
		ws.breaks.stream().forEach(interval -> result.append(interval.start).append("-").append(interval.end).append(","));
		return result.deleteCharAt(result.length() - 1).append("<br/>").toString();
	}

	private String findWeekend(List<WorkSchedule> list) {
		List<DayOfWeek> weekend = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
		List<DayOfWeek> toRemove = list.stream().map(workSchedule -> workSchedule.day).collect(Collectors.toList());
		weekend.removeAll(toRemove);
		StringBuilder result = new StringBuilder();
		weekend.stream().forEach(dayOfWeek -> result.append(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())).append(","));
		return result.deleteCharAt(result.length() - 1).append(" - выходной").toString();
	}

}


