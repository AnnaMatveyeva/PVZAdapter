package example.schedule;

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

		WorkSchedule wsS = new WorkSchedule();
		wsS.day = DayOfWeek.SATURDAY;
		wsS.workingHours = new Interval();
		wsS.workingHours.start = LocalTime.of(9, 0);
		wsS.workingHours.end = LocalTime.of(18, 0);
		Interval intervalSuS = new Interval();
		intervalSuS.start = LocalTime.of(13, 30);
		intervalSuS.end = LocalTime.of(14, 0);
		wsS.breaks = Arrays.asList(intervalSuS);



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

		WorkSchedule wsR = new WorkSchedule();
		wsR.day = DayOfWeek.SUNDAY;
		wsR.workingHours = new Interval();
		wsR.workingHours.start = LocalTime.of(9, 0);
		wsR.workingHours.end = LocalTime.of(18, 0);
		Interval intervalR = new Interval();
		intervalR.start = LocalTime.of(13, 30);
		intervalR.end = LocalTime.of(14, 0);
		wsR.breaks = Arrays.asList(intervalR);


		WorkSchedule ws2 = new WorkSchedule();
		ws2.day = DayOfWeek.FRIDAY;
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


		WorkSchedule ws3 = new WorkSchedule();
		ws3.day = DayOfWeek.THURSDAY;
		Interval wHH = new Interval();
		wHH.start = LocalTime.of(9, 0);
		wHH.end = LocalTime.of(18, 0);
		ws3.workingHours = wHH;
		Interval interval3 = new Interval();
		interval3.start = LocalTime.of(13, 30);
		interval3.end = LocalTime.of(14, 0);
		ws3.breaks = Arrays.asList(interval3);


		List<WorkSchedule> list = new ArrayList<>();

		list.add(ws);
		list.add(ws1);
		list.add(ws2);
		list.add(wsT);
		list.add(ws3);
		list.add(wsR);
		list.add(wsS);
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
		Map<Interval, List<WorkSchedule>> groupByWorkingHours = getIntervalListMap(list);

		for (List<WorkSchedule> listSchedule : groupByWorkingHours.values()) {

			List<WorkSchedule> sortedSchedule = listSchedule.stream()
					.sorted(Comparator.comparing(WorkSchedule::getDay))
					.collect(Collectors.toList());

			Map<List<Interval>, Set<WorkSchedule>> byIntervals = getBreaksListMap(sortedSchedule);

			result.append(buildWorkingDaysString(byIntervals));
		}
		result.append(buildWeekendString(list));

		return result.toString();
	}

	private StringBuilder buildWorkingDaysString(Map<List<Interval>, Set<WorkSchedule>> byIntervals) {
		StringBuilder result = new StringBuilder();
		byIntervals.values().forEach(workSchedules -> {
			List<WorkSchedule> byBreaksSorted = workSchedules.stream()
					.sorted(Comparator.comparing(WorkSchedule::getDay))
					.collect(Collectors.toList());

			result.append(buildDaysString(byBreaksSorted))
					.append(": ")
					.append(getWorkingHours(byBreaksSorted.get(0)))
					.append(getIntervals(byBreaksSorted.get(0)))
					.append("</br>");
		});

		return result;
	}


	private Map<List<Interval>, Set<WorkSchedule>> getBreaksListMap(List<WorkSchedule> sortedSchedule) {
		Map<List<Interval>, Set<WorkSchedule>> byIntervals = new LinkedHashMap<>();
		for (int i = 0; i < sortedSchedule.size(); i++) {
			int finalI = i;

			sortedSchedule.forEach(workSchedule -> {
				List<Interval> breaks = sortedSchedule.get(finalI).getBreaks();

				if (isIntervalListsSimilar(breaks, workSchedule.getBreaks())) {
					if (byIntervals.containsKey(breaks)) {
						Set<WorkSchedule> byInterval = byIntervals.get(workSchedule.getBreaks());
						byInterval.add(workSchedule);
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
			});
		}
		return byIntervals;
	}

	private boolean isIntervalListsSimilar(List<Interval> o1, List<Interval> o2) {
		return o1.containsAll(o2) && o2.containsAll(o1);
	}

	private static Map<Interval, List<WorkSchedule>> getIntervalListMap(List<WorkSchedule> list) {
		List<WorkSchedule> sorted = list.stream()
				.sorted(Comparator.comparing(WorkSchedule::getDay))
				.collect(Collectors.toList());

		List<Interval> intervals = new LinkedList<>();
		for (WorkSchedule schedule : sorted) {
			if (!intervals.contains(schedule.getWorkingHours())) {
				intervals.add(schedule.getWorkingHours());
			}
		}
		Map<Interval, List<WorkSchedule>> groupByWorkingHours = new LinkedHashMap<>();
		for (Interval interval : intervals) {
			groupByWorkingHours.put(interval, new LinkedList<>());
		}

		sorted.forEach(workSchedule -> {
					final List<WorkSchedule> workSchedules = groupByWorkingHours.get(workSchedule.getWorkingHours());
					workSchedules.add(workSchedule);
				}
		);
		return groupByWorkingHours;
	}

	private String buildDaysString(List<WorkSchedule> byBreaksSorted) {
		StringBuilder result = new StringBuilder();
		List<WorkSchedule> rest = new ArrayList<>(byBreaksSorted);
		List<WorkSchedule> listToPrint = getSchedulesOrdered(byBreaksSorted);
		if (listToPrint.size() > 2) {
			result.append(getRuDay(listToPrint.get(0).getDay()))
					.append("-")
					.append(getRuDay(listToPrint.get(listToPrint.size() - 1).getDay()));

		} else {
			listToPrint.forEach(workSchedule -> result.append(getRuDay(workSchedule.getDay())).append(","));
			result.deleteCharAt(result.length() - 1);
		}
		rest.removeAll(listToPrint);

		if (!rest.isEmpty()) {
			result.append(",");
			result.append(buildDaysString(rest));
		}
		return result.toString();
	}

	private List<WorkSchedule> getSchedulesOrdered(List<WorkSchedule> byBreaksSorted) {
		if (byBreaksSorted.size() < 3)
			return byBreaksSorted;
		int order = byBreaksSorted.get(0).getDay().ordinal();
		List<WorkSchedule> result = new ArrayList<>();
		result.add(byBreaksSorted.get(0));
		for (int i = 1; i < byBreaksSorted.size(); i++) {
			if (byBreaksSorted.get(i).getDay().ordinal() == order + 1) {
				result.add(byBreaksSorted.get(i));
				order++;
			} else return result;
		}
		return result;
	}

	private String getRuDay(DayOfWeek day) {
		Locale locale = new Locale("ru", "RU");
		return day.getDisplayName(TextStyle.SHORT, locale);
	}

	private String getWorkingHours(WorkSchedule ws) {
		return ws.getWorkingHours().getStart() + "-" + ws.getWorkingHours().getEnd();
	}

	private String getIntervals(WorkSchedule ws) {
		return ws.getBreaks().isEmpty() ? "" : ws.getBreaks()
				.stream()
				.map(interval -> interval.getStart() + "-" + interval.getEnd())
				.collect(Collectors.joining(", ", ", перерыв: ", ""));
	}

	private String buildWeekendString(List<WorkSchedule> list) {
		List<DayOfWeek> weekend = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
		List<DayOfWeek> toRemove = list.stream()
				.map(WorkSchedule::getDay)
				.collect(Collectors.toList());

		if (toRemove.size() < 7) {
			weekend.removeAll(toRemove);

			StringBuilder result = new StringBuilder();
			weekend.forEach(dayOfWeek -> result.append(getRuDay(dayOfWeek))
					.append(","));
			return result.deleteCharAt(result.length() - 1)
					.append(" - выходной").toString();

		} else return "";
	}
}