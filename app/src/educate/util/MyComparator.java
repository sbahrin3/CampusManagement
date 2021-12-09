package educate.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public abstract class MyComparator implements java.util.Comparator {

	public Comparator reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparing(Comparator other) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparing(Function keyExtractor,
			Comparator keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparing(Function keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparingInt(ToIntFunction keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparingLong(ToLongFunction keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> nullsLast(Comparator<? super T> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T, U> Comparator<T> comparing(
			Function<? super T, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
			Function<? super T, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingInt(
			ToIntFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingLong(
			ToLongFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingDouble(
			ToDoubleFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
