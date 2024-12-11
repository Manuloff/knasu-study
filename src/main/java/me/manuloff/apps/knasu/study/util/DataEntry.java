package me.manuloff.apps.knasu.study.util;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Manuloff
 * @since 23:27 10.12.2024
 */
@EqualsAndHashCode
public class DataEntry implements Iterable<String> {

	private final List<String> data;

	private DataEntry(@NonNull List<String> data) {
		this.data = data;
	}

	@NonNull
	public static DataEntry fromString(@NonNull String data) {
		return of(data.split(","));
	}

	@NonNull
	public static DataEntry of(@NonNull String... values) {
		return new DataEntry(Arrays.stream(values).collect(Collectors.toCollection(LinkedList::new)));
	}

	@NonNull
	public static DataEntry of(@NonNull List<String> values) {
		return new DataEntry(new LinkedList<>(values));
	}

	@NonNull
	public static DataEntry of() {
		return new DataEntry(new LinkedList<>());
	}

	//

	@NonNull
	public List<String> getData() {
		return new LinkedList<>(data);
	}

	@NonNull
	public DataEntry add(@NonNull Object value) {
		this.data.add(value.toString());
		return this;
	}

	@NonNull
	public DataEntry set(int index, @NonNull Object value) {
		this.data.set(index, value.toString());
		return this;
	}

	@NonNull
	public DataEntry clear() {
		this.data.clear();
		return this;
	}

	public int size() {
		return this.data.size();
	}

	public boolean has(int index) {
		return index >= 0 && index < this.data.size();
	}

	@NonNull
	public String getString(int index) {
		this.checkIndex(index);
		return this.data.get(index);
	}

	public int getInt(int index) {
		this.checkIndex(index);
		return Integer.parseInt(this.data.get(index));
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= data.size()) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + data.size());
		}
	}

	@NonNull
	public String asString() {
		return String.join(",", this.data);
	}

	@Override
	public String toString() {
		return "DataEntry" + this.data;
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return this.data.iterator();
	}

	@Override
	public void forEach(Consumer<? super String> action) {
		this.data.forEach(action);
	}

	@Override
	public Spliterator<String> spliterator() {
		return this.data.spliterator();
	}

	@NonNull
	public DataEntry deepCopy() {
		return new DataEntry(new LinkedList<>(this.data));
	}
}
