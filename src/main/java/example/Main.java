package example;

import lombok.Data;
import org.w3c.dom.ls.LSOutput;
import javax.sound.midi.Soundbank;
import java.awt.image.AreaAveragingScaleFilter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
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
		wsT.workingHours.start = LocalTime.of(8, 0);
		wsT.workingHours.end = LocalTime.of(17, 0);
		Interval intervalT = new Interval();
		intervalT.start = LocalTime.of(13, 0);
		intervalT.end = LocalTime.of(14, 0);
		wsT.breaks = Arrays.asList(intervalT);


		WorkSchedule ws2 = new WorkSchedule();
		ws2.day = DayOfWeek.THURSDAY;
		Interval wH = new Interval();
		wH.start = LocalTime.of(8, 0);
		wH.end = LocalTime.of(17, 0);
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
		WorkScheduleAdapterImpll adapter = new WorkScheduleAdapterImpll();
		System.out.println(adapter.adapter(list));

	}
}

interface WorkScheduleAdapterr {
	String prepare(List<WorkSchedule> list);
}

class WorkScheduleAdapterImpll implements WorkScheduleAdapterr {

	private boolean isComparedIntervals = false;

	@Override
	public String prepare(List<WorkSchedule> list) {
		StringBuilder result = new StringBuilder();

//		for (WorkSchedule ws : list){
//			result.append(ws.day).append(":")
//			.append(ws.workingHours.start)
//			.append("-")
//			.append(ws.workingHours.end)
//			.append(",")
//			.append(" перерыв");
//			for(Interval time : ws.breaks){
//				result.append(time.start).append("-").append(time.end);
//			}
//		}

		return null;
	}


	public String adapter(List<WorkSchedule> list) {
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
						if(byIntervals.containsKey(sortedSchedule.get(j).getBreaks()) && !byIntervals.get(sortedSchedule.get(j).getBreaks()).contains(sortedSchedule.get(j)))
							byIntervals.put(sortedSchedule.get(j).getBreaks(), listToMap);
					}
				}
			}
			
			for (Set<WorkSchedule> workSchedules : byIntervals.values()){

				List<WorkSchedule> byBreaksSorted = workSchedules.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
				for (WorkSchedule workSchedule : byBreaksSorted) {
					result.append(getWorkingDay(workSchedule))
							.append(",");
				}
				result.deleteCharAt(result.length() - 1)
						.append(": ")
						.append(byBreaksSorted.get(0).workingHours.start).append("-").append(byBreaksSorted.get(0).workingHours.end).append(",");
				result.append(getIntervals(byBreaksSorted.get(0)));
			}



//			Map<List<Interval>, List<WorkSchedule>> groupByIntervals = listSchedule.stream().sorted(Comparator.comparing(WorkSchedule::getDay).reversed()).collect(Collectors.groupingBy(WorkSchedule::getBreaks));
//			groupByIntervals.values().stream().forEach(workSchedules -> workSchedules.stream().forEach(workSchedule -> System.out.println(workSchedule.day)));
//
//			for (List<WorkSchedule> listByIntervals : groupByIntervals.values()) {
//				System.out.println(listByIntervals.size());
//				List<WorkSchedule> listByIntervalsSorted = listByIntervals.stream().sorted(Comparator.comparing(WorkSchedule::getDay)).collect(Collectors.toList());
//				System.out.println(listByIntervalsSorted.size());
//
//				for (WorkSchedule workSchedule : listByIntervalsSorted) {
//					result.append(getWorkingDay(workSchedule))
//							.append(",");
//				}
//				result.deleteCharAt(result.length() - 1)
//						.append(": ")
//						.append(listByIntervals.get(0).workingHours.start).append("-").append(listByIntervals.get(0).workingHours.end).append(",");
//				result.append(getIntervals(listByIntervalsSorted.get(0)));
//			}
		}
		result.append(findWeekend(list));

		return result.toString();
	}

	public boolean isIntervalsSimilar(List<Interval> first, List<Interval> second) {
		if (first.size() != second.size()) {
			return false;
		}
		List<Interval> cp = new ArrayList<>(first);
		for (Interval o : second) {
			if (!cp.remove(o)) {
				return false;
			}
		}


		return cp.isEmpty();
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
		StringBuilder result = new StringBuilder();
		result.append("перерыв: ");
		ws.breaks.stream().forEach(interval -> result.append(interval.start).append("-").append(interval.end).append(","));
		return result.deleteCharAt(result.length() - 1).append("<br/>").toString();
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

class WorkSchedule {

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

	public boolean isEqualsTime(WorkSchedule ws) {
		return this.workingHours.equals(ws.workingHours) && this.breaks.equals(ws.breaks);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		WorkSchedule ws = (WorkSchedule) obj;

		return this.workingHours.equals(ws.workingHours) && this.breaks.equals(ws.breaks);
	}

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