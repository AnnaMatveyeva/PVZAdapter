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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {


		WorkSchedule ws = new WorkSchedule();
		ws.day = DayOfWeek.WEDNESDAY;
		ws.workingHours = new Interval();
		ws.workingHours.start = LocalTime.of(9, 0);
		ws.workingHours.end = LocalTime.of(18, 0);
		Interval intervalSu = new Interval();
		intervalSu.start = LocalTime.of(13, 0);
		intervalSu.end = LocalTime.of(14, 0);
		ws.breaks = Arrays.asList(intervalSu);


		WorkSchedule wsT = new WorkSchedule();
		wsT.day = DayOfWeek.THURSDAY;
		wsT.workingHours = new Interval();
		wsT.workingHours.start = LocalTime.of(9, 0);
		wsT.workingHours.end = LocalTime.of(18, 0);
		Interval intervalT = new Interval();
		intervalT.start = LocalTime.of(13, 0);
		intervalT.end = LocalTime.of(14, 0);
		wsT.breaks = Arrays.asList(intervalT);


		WorkSchedule ws2 = new WorkSchedule();
		ws2.day = DayOfWeek.TUESDAY;
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
		Interval second = new Interval();
		second.start = LocalTime.of(13, 30);
		second.end = LocalTime.of(14, 0);

		ws1.breaks = Arrays.asList(interval,second);


		List<WorkSchedule> list = new ArrayList<>();

		list.add(ws);
		list.add(ws1);
		list.add(ws2);
		list.add(wsT);
		WorkScheduleAdapterImpll adapter = new WorkScheduleAdapterImpll();
		System.out.println(adapter.adapter(list));

	}
}

class WorkScheduleAdapterImpll {

	public String adapter(List<WorkSchedule> list) {
		StringBuilder result = new StringBuilder();
		List<WorkSchedule> sorted = list.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
		List<Interval> intervals = new LinkedList<>();
		sorted.stream().filter(workSchedule -> !intervals.contains(workSchedule.getWorkingHours())).forEach(workSchedule -> intervals.add(workSchedule.getWorkingHours()));

		Map<Interval, List<WorkSchedule>> groupByWorkingHours = new LinkedHashMap<>();
		intervals.stream().forEach(interval -> groupByWorkingHours.put(interval, new LinkedList<>()));

		sorted.stream().forEach(workSchedule -> groupByWorkingHours.get(workSchedule.getWorkingHours()).add(workSchedule));

		for (List<WorkSchedule> listSchedule : groupByWorkingHours.values()) {
			List<WorkSchedule> sortedSchedule = listSchedule.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
			Map<List<Interval>, Set<WorkSchedule>> byIntervals = new LinkedHashMap<>();

			for (int i = 0; i < sortedSchedule.size(); i++) {
				for (int j = 0; j < sortedSchedule.size(); j++) {

					if (sortedSchedule.get(i).getBreaks().containsAll(sortedSchedule.get(j).getBreaks()) && sortedSchedule.get(j).getBreaks().containsAll(sortedSchedule.get(i).getBreaks())) {
						if (byIntervals.containsKey(sortedSchedule.get(i).getBreaks())) {
							byIntervals.get(sortedSchedule.get(j).getBreaks()).add(sortedSchedule.get(j));
						} else {
							Set<WorkSchedule> listToMap = new LinkedHashSet<>();
							listToMap.add(sortedSchedule.get(j));
							byIntervals.put(sortedSchedule.get(j).getBreaks(), listToMap);
						}
					} else {
						Set<WorkSchedule> listToMap = new LinkedHashSet<>();
						listToMap.add(sortedSchedule.get(j));
						if (byIntervals.containsKey(sortedSchedule.get(j).getBreaks()) && !byIntervals.get(sortedSchedule.get(j).getBreaks()).contains(sortedSchedule.get(j)))
							byIntervals.put(sortedSchedule.get(j).getBreaks(), listToMap);
					}
				}
			}

			byIntervals.values().stream().forEach(workSchedules -> {
				List<WorkSchedule> byBreaksSorted = workSchedules.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
				if (isInOrder(byBreaksSorted)) {
					result.append(getWorkingDay(byBreaksSorted.get(0))).append("-").append(getWorkingDay(byBreaksSorted.get(byBreaksSorted.size() - 1)));
				} else {
					byBreaksSorted.stream().forEach(workSchedule -> result.append(getWorkingDay(workSchedule))
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

	public String getWorkingDay(WorkSchedule ws) {
		StringBuilder result = new StringBuilder();
		result.append(ws.day.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
		return result.toString();
	}

	public String getWorkingHours(WorkSchedule ws) {
		StringBuilder result = new StringBuilder();
		result.append(ws.workingHours.start).append("-").append(ws.workingHours.end).append(",");

		return result.toString();
	}

	public String getIntervals(WorkSchedule ws) {
		String result = "перерыв: " + ws.breaks.stream().map(interval -> interval.getStart()+ "-" + interval.getEnd()).collect(Collectors.joining(","));
		return result + "<br/>";
	}

	public String findWeekend(List<WorkSchedule> list) {
		List<DayOfWeek> weekend = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
		List<DayOfWeek> toRemove = list.stream().map(workSchedule -> workSchedule.day).collect(Collectors.toList());
		weekend.removeAll(toRemove);
		StringBuilder result = new StringBuilder();
		weekend.stream().forEach(dayOfWeek -> result.append(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())).append(","));
		return result.deleteCharAt(result.length() - 1).append(" - выходной").toString();
	}

}

class WorkSchedule  {

	DayOfWeek day;
	Interval workingHours;
	List<Interval> breaks = new ArrayList<>();

	public DayOfWeek getDay() {
		return day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public Interval getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(Interval workingHours) {
		this.workingHours = workingHours;
	}

	public List<Interval> getBreaks() {
		return breaks;
	}

	public void setBreaks(List<Interval> breaks) {
		this.breaks = breaks;
	}


//	@Override
//	public boolean equals(Object obj) {
//		if (obj == this) {
//			return true;
//		}
//		if (obj == null || obj.getClass() != this.getClass()) {
//			return false;
//		}
//		WorkSchedule ws = (WorkSchedule) obj;
//
//		return this.workingHours.equals(ws.workingHours) && this.breaks.equals(ws.breaks);
//	}

}

class Interval {

	LocalTime start;

	LocalTime end;

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public void setEnd(LocalTime end) {
		this.end = end;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Interval)) return false;
		Interval interval = (Interval) o;
		return this.start.compareTo(interval.getStart()) == 0 &&
				this.end.compareTo(interval.getEnd()) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStart(), getEnd());
	}
}
