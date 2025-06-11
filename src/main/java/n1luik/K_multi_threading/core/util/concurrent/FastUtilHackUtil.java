package n1luik.K_multi_threading.core.util.concurrent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FastUtilHackUtil {

	public static class WrappingObjectSortedSet<T> implements ObjectSortedSet<T> {

		Set<T> backing;
		public WrappingObjectSortedSet(Set<T> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(o);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(T e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(o);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c);
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			return backing.addAll(c.stream().collect(Collectors.toSet()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectBidirectionalIterator<T> iterator(T fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectBidirectionalIterator<T> iterator() {
			final Iterator<T> backg = backing.iterator();
			return new ObjectBidirectionalIterator<T>() {

				@Override
				public T previous() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasPrevious() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public T next() {
					return backg.next();
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Nullable
		@Override
		public Comparator<? super T> comparator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> subSet(T fromElement, T toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> headSet(T toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> tailSet(T fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T first() {
			throw new UnsupportedOperationException();
		}

		@Override
		public T last() {
			throw new UnsupportedOperationException();
		}


	}

	public static class ConvertingObjectSortedSet<E, T> implements ObjectSortedSet<T> {

		Set<E> backing;
		Function<E, T> forward;
		Function<T, E> back;

		public ConvertingObjectSortedSet(Set<E> backing, Function<E, T> forward, Function<T, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((T) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@Override
		public boolean add(T e) {
			return backing.add(back.apply(e));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((T) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			return backing.addAll(c.stream().map(i -> back.apply(i)).collect(Collectors.toSet()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectBidirectionalIterator<T> iterator(T fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectBidirectionalIterator<T> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectBidirectionalIterator<T>() {

				@Override
				public T previous() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasPrevious() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public T next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Nullable
		@Override
		public Comparator<? super T> comparator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> subSet(T fromElement, T toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> headSet(T toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<T> tailSet(T fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T first() {
			throw new UnsupportedOperationException();
		}

		@Override
		public T last() {
			throw new UnsupportedOperationException();
		}


	}

	public static class ConvertingObjectSet<E, T> implements ObjectSet<T> {

		Set<E> backing;
		Function<E, T> forward;
		Function<T, E> back;

		public ConvertingObjectSet(Set<E> backing, Function<E, T> forward, Function<T, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((T) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@Override
		public boolean add(T e) {
			return backing.add(back.apply(e));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((T) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			return backing.addAll(c.stream().map(i -> back.apply(i)).collect(Collectors.toSet()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream().map(i -> back.apply((T) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectIterator<T> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectIterator<T>() {

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public T next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}


	}

	public static class ToObjectSet<T> implements ObjectSet<T> {

		Set<T> backing;

		public ToObjectSet(Set<T> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(o);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(T e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(o);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c);
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c);
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectIterator<T> iterator() {
			final Iterator<T> backg = backing.iterator();
			return new ObjectIterator<T>() {

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public T next() {
					return backg.next();
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}


	}

	public static class Int2ObjectConvertingFastSortedObjectSet<E, T> implements Int2ObjectSortedMap.FastSortedEntrySet<T> {

		Set<E> backing;
		Function<E, Int2ObjectMap.Entry<T>> forward;
		Function<Int2ObjectMap.Entry<T>, E> back;

		public Int2ObjectConvertingFastSortedObjectSet(Set<E> backing, Function<E, Int2ObjectMap.Entry<T>> forward, Function<Int2ObjectMap.Entry<T>, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((Int2ObjectMap.Entry<T>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@Override
		public boolean add(Int2ObjectMap.Entry<T> e) {
			return backing.add(back.apply(e));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((Int2ObjectMap.Entry<T>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream().map(i -> back.apply((Int2ObjectMap.Entry<T>) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@Override
		public boolean addAll(Collection<? extends Int2ObjectMap.Entry<T>> c) {
			return backing.addAll(c.stream().map(i -> back.apply(i)).collect(Collectors.toSet()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back.apply((Int2ObjectMap.Entry<T>) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream().map(i -> back.apply((Int2ObjectMap.Entry<T>) i)).collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectBidirectionalIterator<Entry<T>> iterator(Entry<T> fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectBidirectionalIterator<Entry<T>> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectBidirectionalIterator<>() {

				@Override
				public Entry<T> previous() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasPrevious() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public Entry<T> next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Nullable
		@Override
		public Comparator<? super Entry<T>> comparator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<Entry<T>> subSet(Entry<T> fromElement, Entry<T> toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<Entry<T>> headSet(Entry<T> toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSortedSet<Entry<T>> tailSet(Entry<T> fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Entry<T> first() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Entry<T> last() {
			throw new UnsupportedOperationException();
		}


		@Override
		public ObjectBidirectionalIterator<Entry<T>> fastIterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectBidirectionalIterator<Entry<T>> fastIterator(Entry<T> from) {
			throw new UnsupportedOperationException();
		}
	}

	public static class ConvertingObjectSetFast_Long<E, T> implements it.unimi.dsi.fastutil.longs.Long2ObjectMap.FastEntrySet<T> {

		Set<E> backing;
		Function<E, it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>> forward;
		Function<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>, E> back;

		public ConvertingObjectSetFast_Long(Set<E> backing,
											Function<E, it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>> forward,
											Function<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream()
						.map(i -> back.apply((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back
								.apply((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream()
						.map(i -> back.apply((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectIterator<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectIterator<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>>() {

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T> next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Override
		public boolean add(it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T> e) {
			return backing.add(back.apply(e));
		}

		@Override
		public boolean addAll(Collection<? extends it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>> c) {
			return backing.addAll(c.stream().map(back).collect(Collectors.toList()));
		}

		@Override
		public ObjectIterator<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<T>> fastIterator() {
			return iterator();
		}


	}

	public static class ConvertingObjectSetFast_Reference2Reference<E, K, V> implements Reference2ReferenceMap.FastEntrySet<K, V> {

		Set<E> backing;
		Function<E, Reference2ReferenceMap.Entry<K, V>> forward;
		Function<Reference2ReferenceMap.Entry<K, V>, E> back;

		public ConvertingObjectSetFast_Reference2Reference(Set<E> backing,
											Function<E, Reference2ReferenceMap.Entry<K, V>> forward,
											Function<Reference2ReferenceMap.Entry<K, V>, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((Reference2ReferenceMap.Entry<K, V>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((Reference2ReferenceMap.Entry<K, V>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream()
						.map(i -> back.apply((Reference2ReferenceMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back
								.apply((Reference2ReferenceMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream()
						.map(i -> back.apply((Reference2ReferenceMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectIterator<Reference2ReferenceMap.Entry<K, V>>() {

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public Reference2ReferenceMap.Entry<K, V> next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Override
		public boolean add(Reference2ReferenceMap.Entry<K, V> e) {
			return backing.add(back.apply(e));
		}

		@Override
		public boolean addAll(Collection<? extends Reference2ReferenceMap.Entry<K, V>> c) {
			return backing.addAll(c.stream().map(back).collect(Collectors.toList()));
		}


		@Override
		public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator() {
			return iterator();
		}
	}

	public static class ConvertingObjectSetFast_Reference2ObjectMap<E, K, V> implements Reference2ObjectMap.FastEntrySet<K, V> {

		Set<E> backing;
		Function<E, Reference2ObjectMap.Entry<K, V>> forward;
		Function<Reference2ObjectMap.Entry<K, V>, E> back;

		public ConvertingObjectSetFast_Reference2ObjectMap(Set<E> backing,
											Function<E, Reference2ObjectMap.Entry<K, V>> forward,
											Function<Reference2ObjectMap.Entry<K, V>, E> back) {
			this.backing = backing;
			this.forward = forward;
			this.back = back;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			try {
				return backing.contains(back.apply((Reference2ObjectMap.Entry<K, V>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public Object[] toArray() {
			return backing.stream().map(forward).toArray();
		}

		@Override
		public <R> R[] toArray(R[] a) {
			return backing.stream().map(forward).collect(Collectors.toSet()).toArray(a);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			try {
				return backing.remove(back.apply((Reference2ObjectMap.Entry<K, V>) o));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean containsAll(Collection<?> c) {
			try {
				return backing.containsAll(c.stream()
						.map(i -> back.apply((Reference2ObjectMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}

		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean removeAll(Collection<?> c) {
			try {
				return backing.removeAll(c.stream().map(i -> back
								.apply((Reference2ObjectMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean retainAll(Collection<?> c) {
			try {
				return backing.retainAll(c.stream()
						.map(i -> back.apply((Reference2ObjectMap.Entry<K, V>) i))
						.collect(Collectors.toSet()));
			} catch (ClassCastException cce) {
				return false;
			}
		}

		@Override
		public void clear() {
			backing.clear();

		}

		@Override
		public ObjectIterator<Reference2ObjectMap.Entry<K, V>> iterator() {
			final Iterator<E> backg = backing.iterator();
			return new ObjectIterator<Reference2ObjectMap.Entry<K, V>>() {

				@Override
				public boolean hasNext() {
					return backg.hasNext();
				}

				@Override
				public Reference2ObjectMap.Entry<K, V> next() {
					return forward.apply(backg.next());
				}

				@Override
				public void remove() {
					backg.remove();
				}
			};
		}

		@Override
		public boolean add(Reference2ObjectMap.Entry<K, V> e) {
			return backing.add(back.apply(e));
		}

		@Override
		public boolean addAll(Collection<? extends Reference2ObjectMap.Entry<K, V>> c) {
			return backing.addAll(c.stream().map(back).collect(Collectors.toList()));
		}


		@Override
		public ObjectIterator<Reference2ObjectMap.Entry<K, V>> fastIterator() {
			return iterator();
		}
	}

	private static <T> Int2ObjectMap.Entry<T> intEntryForwards(Map.Entry<Integer, T> entry) {
		return new Int2ObjectMap.Entry<T>() {

			@Override
			public T getValue() {
				return entry.getValue();
			}

			@Override
			public T setValue(T value) {
				return entry.setValue(value);
			}

			@Override
			public int getIntKey() {
				return entry.getKey();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == entry) {
					return true;
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return entry.hashCode();
			}
		};
	}
	private static <K> Object2BooleanMap.Entry<K> boolEntryForwards(Map.Entry<K, Boolean> entry) {
		return new Object2BooleanMap.Entry<K>() {

			@Override
			public boolean getBooleanValue() {
				return entry.getValue();
			}

			@Override
			public Boolean getValue() {
				return entry.getValue();
			}

			@Override
			public Boolean setValue(Boolean value) {
				return entry.setValue(value);
			}

			@Override
			public boolean setValue(boolean value) {
				return entry.setValue(value);
			}

			@Override
			public K getKey() {
				return entry.getKey();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == entry) {
					return true;
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return entry.hashCode();
			}
		};
	}

	private static <T> Map.Entry<Integer, T> intEntryBackwards(Int2ObjectMap.Entry<T> entry) {
		return entry;
	}
	private static <K> Map.Entry<K, Boolean> boolEntryBackwards(Object2BooleanMap.Entry<K> entry) {
		return entry;
	}

	private static <T> Long2ObjectMap.Entry<T> longEntryForwards(Map.Entry<Long, T> entry) {
		return new Long2ObjectMap.Entry<T>() {

			@Override
			public T getValue() {
				return entry.getValue();
			}

			@Override
			public T setValue(T value) {
				return entry.setValue(value);
			}

			@Override
			public long getLongKey() {
				return entry.getKey();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == entry) {
					return true;
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return entry.hashCode();
			}
		};
	}

	private static <T> Map.Entry<Long, T> longEntryBackwards(Long2ObjectMap.Entry<T> entry) {
		return entry;
	}

	private static Long2ByteMap.Entry longByteEntryForwards(Map.Entry<Long, Byte> entry) {
		return new Long2ByteMap.Entry() {

			@Override
			public Byte getValue() {
				return entry.getValue();
			}

			@Override
			public byte setValue(byte value) {
				return entry.setValue(value);
			}

			@Override
			public byte getByteValue() {
				return entry.getValue();
			}

			@Override
			public long getLongKey() {
				return entry.getKey();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == entry) {
					return true;
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return entry.hashCode();
			}

		};
	}

	private static <T> Map.Entry<Long, Byte> longByteEntryBackwards(Long2ByteMap.Entry entry) {
		return entry;
	}

	public static <T> Int2ObjectSortedMap.FastSortedEntrySet<T> entrySetFastSortedIntWrap(Map<Integer, T> map) {
		return new Int2ObjectConvertingFastSortedObjectSet<Map.Entry<Integer, T>, T>(map.entrySet(), FastUtilHackUtil::intEntryForwards, FastUtilHackUtil::intEntryBackwards);
	}

	public static <T> ObjectSet<Int2ObjectMap.Entry<T>> entrySetIntWrap(Map<Integer, T> map) {
		return new ConvertingObjectSet<Map.Entry<Integer, T>, Int2ObjectMap.Entry<T>>(map.entrySet(), FastUtilHackUtil::intEntryForwards, FastUtilHackUtil::intEntryBackwards);
	}
	public static <K> ObjectSet<Object2BooleanMap.Entry<K>> entrySetBooleanWrap(Map<K, Boolean> map) {
		return new ConvertingObjectSet<Map.Entry<K, Boolean>, Object2BooleanMap.Entry<K>>(map.entrySet(), FastUtilHackUtil::boolEntryForwards, FastUtilHackUtil::boolEntryBackwards);
	}

	public static <T> ObjectSet<Long2ObjectMap.Entry<T>> entrySetLongWrap(Map<Long, T> map) {
		return new ConvertingObjectSet<Map.Entry<Long, T>, Long2ObjectMap.Entry<T>>(map.entrySet(), FastUtilHackUtil::longEntryForwards, FastUtilHackUtil::longEntryBackwards);
	}

	public static <T> it.unimi.dsi.fastutil.longs.Long2ObjectMap.FastEntrySet<T> entrySetLongWrapFast(Map<Long, T> map) {
		return new ConvertingObjectSetFast_Long<Map.Entry<Long, T>, T>(map.entrySet(), FastUtilHackUtil::longEntryForwards, FastUtilHackUtil::longEntryBackwards);
	}

	public static ObjectSet<Long2ByteMap.Entry> entrySetLongByteWrap(Map<Long, Byte> map) {
		return new ConvertingObjectSet<Map.Entry<Long, Byte>, Long2ByteMap.Entry>(map.entrySet(), FastUtilHackUtil::longByteEntryForwards, FastUtilHackUtil::longByteEntryBackwards);
	}


	static class WrappingIntIterator implements IntBidirectionalIterator {

		Iterator<Integer> backing;

		public WrappingIntIterator(Iterator<Integer> backing) {
			this.backing = backing;
		}

		@Override
		public boolean hasNext() {
			return backing.hasNext();
		}

		@Override
		public int nextInt() {
			return backing.next();
		}

		@Override
		public Integer next() {
			return backing.next();
		}

		@Override
		public void remove() {
			backing.remove();
		}

		@Override
		public int previousInt() {
			return 0;
		}

		@Override
		public boolean hasPrevious() {
			return false;
		}
	}

	static class WrappingLongIterator implements LongIterator {

		Iterator<Long> backing;

		public WrappingLongIterator(Iterator<Long> backing) {
			this.backing = backing;
		}

		@Override
		public boolean hasNext() {
			return backing.hasNext();
		}

		@Override
		public long nextLong() {
			return backing.next();
		}

		@Override
		public Long next() {
			return backing.next();
		}

		@Override
		public void remove() {
			backing.remove();
		}

	}

	public static class WrappingIntSet implements IntSortedSet {

		Set<Integer> backing;

		public WrappingIntSet(Set<Integer> backing) {
			this.backing = backing;
		}

		@Override
		public boolean add(int key) {
			return backing.add(key);
		}

		@Override
		public boolean contains(int key) {
			return backing.contains(key);
		}

		@Override
		public int[] toIntArray() {
			return backing.stream().mapToInt(i -> i).toArray();
		}

		@Override
		public int[] toIntArray(int[] a) {
			if (a.length >= size()) {
				throw new UnsupportedOperationException();
			} else {
				return toIntArray();
			}
		}

		@Override
		public int[] toArray(int[] a) {
			return toIntArray(a);
		}

		@Override
		public boolean addAll(IntCollection c) {
			return backing.addAll(c);
		}

		@Override
		public boolean containsAll(IntCollection c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean removeAll(IntCollection c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(IntCollection c) {
			return backing.retainAll(c);
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Integer> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public IntBidirectionalIterator iterator(int fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntBidirectionalIterator iterator() {
			return new WrappingIntIterator(backing.iterator());
		}

		@Override
		public IntSortedSet subSet(int fromElement, int toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntSortedSet headSet(int toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntSortedSet tailSet(int fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntComparator comparator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int firstInt() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int lastInt() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(int k) {
			return backing.remove(k);
		}

	}

	public static LongSet wrapLongSet(Set<Long> longset) {
		return new WrappingLongSet(longset);
	}

	public static class WrappingLongSet implements LongSet {

		Set<Long> backing;

		public WrappingLongSet(Set<Long> backing) {
			this.backing = backing;
		}

		@Override
		public boolean add(long key) {
			return backing.add(key);
		}

		@Override
		public boolean contains(long key) {
			return backing.contains(key);
		}

		@Override
		public long[] toLongArray() {
			return backing.stream().mapToLong(i -> i).toArray();
		}

		@Override
		public long[] toLongArray(long[] a) {
			if (a.length >= size()) {
				throw new UnsupportedOperationException();
			} else {
				return toLongArray();
			}
		}

		@Override
		public long[] toArray(long[] a) {
			return toLongArray(a);
		}

		@Override
		public boolean addAll(LongCollection c) {
			return backing.addAll(c);
		}

		@Override
		public boolean containsAll(LongCollection c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean removeAll(LongCollection c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(LongCollection c) {
			return backing.retainAll(c);
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Long> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public LongIterator iterator() {
			return new WrappingLongIterator(backing.iterator());
		}

		@Override
		public boolean remove(long k) {
			return backing.remove(k);
		}

	}

	public static LongSortedSet wrapLongSortedSet(Set<Long> longset) {
		return new WrappingLongSortedSet(longset);
	}

	public static class WrappingLongSortedSet implements LongSortedSet {

		Set<Long> backing;

		public WrappingLongSortedSet(Set<Long> backing) {
			this.backing = backing;
		}

		@Override
		public boolean add(long key) {
			return backing.add(key);
		}

		@Override
		public boolean contains(long key) {
			return backing.contains(key);
		}

		@Override
		public long[] toLongArray() {
			return backing.stream().mapToLong(i -> i).toArray();
		}

		@Override
		public long[] toLongArray(long[] a) {
			if (a.length >= size()) {
				throw new UnsupportedOperationException();
			} else {
				return toLongArray();
			}
		}

		@Override
		public long[] toArray(long[] a) {
			return toLongArray(a);
		}

		@Override
		public boolean addAll(LongCollection c) {
			return backing.addAll(c);
		}

		@Override
		public boolean containsAll(LongCollection c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean removeAll(LongCollection c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(LongCollection c) {
			return backing.retainAll(c);
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Long> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public boolean remove(long k) {
			return backing.remove(k);
		}

		@Override
		public LongBidirectionalIterator iterator(long fromElement) {
			throw new UnsupportedOperationException();
			//return FastUtilHackUtil.wrap(new LinkedList<Long>(backing).iterator());
		}

		@Override
		public LongBidirectionalIterator iterator() {
			return FastUtilHackUtil.wrap(new LinkedList<Long>(backing).iterator());
		}

		@Override
		public LongSortedSet subSet(long fromElement, long toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LongSortedSet headSet(long toElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LongSortedSet tailSet(long fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LongComparator comparator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long firstLong() {
			return backing.stream().findAny().get();
		}

		@Override
		public long lastLong() {
			return backing.stream().findAny().get();
		}

	}

	public static IntSortedSet wrapIntSet(Set<Integer> intset) {
		return new WrappingIntSet(intset);
	}

	public static class WrappingObjectCollection<V> implements ObjectCollection<V> {

		Collection<V> backing;

		public WrappingObjectCollection(Collection<V> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return backing.contains(o);
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(V e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return backing.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public ObjectIterator<V> iterator() {
			return FastUtilHackUtil.itrWrap(backing);
		}

	}

	public static class WrappingBooleanCollection implements BooleanCollection {

		Collection<Boolean> backing;

		public WrappingBooleanCollection(Collection<Boolean> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return backing.contains(o);
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(boolean e) {
			return backing.add(e);
		}

		@Override
		public boolean contains(boolean key) {
			return backing.contains(key);
		}

		@Override
		public boolean rem(boolean key) {
			return backing.remove(key);
		}

		@Override
		public boolean remove(Object o) {
			return backing.remove(o);
		}

		@Override
		public boolean[] toBooleanArray() {
			Boolean[] array = backing.toArray(Boolean[]::new);
			boolean[] ra = new  boolean[backing.size()];
			for (int i = ra.length - 1; i >= 0; i--) {
				ra[i] = array[i];
			}
			return ra;
		}

		@Override
		public boolean[] toArray(boolean[] a) {
			Boolean[] array = backing.toArray(Boolean[]::new);
			if (a.length < array.length) {
				boolean[] ra = new  boolean[backing.size()];
				for (int i = ra.length - 1; i >= 0; i--) {
					ra[i] = array[i];
				}
				return ra;
			} else if (a.length == array.length) {
				for (int i = a.length - 1; i >= 0; i--) {
					a[i] = array[i];
				}
				return a;
			}else {
				for (int i = array.length - 1; i >= 0; i--) {
					a[i] = array[i];
				}
				for (int i = array.length; i < a.length; i++) {
					a[i] = false;
				}
				return a;

			}
		}

		@Override
		public boolean addAll(BooleanCollection c) {
			return backing.addAll(c);
		}

		@Override
		public boolean containsAll(BooleanCollection c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Boolean> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(BooleanCollection c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(BooleanCollection c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public @NotNull BooleanIterator iterator() {
			Iterator<Boolean> iterator = backing.iterator();
			return new BooleanIterator(){

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public void remove() {
					iterator.remove();
				}

				@Override
				public boolean nextBoolean() {
					return iterator.hasNext();
				}
			};
		}

	}

	public static class WrappingReferenceCollection<V> implements ReferenceCollection<V> {

		Collection<V> backing;

		public WrappingReferenceCollection(Collection<V> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return backing.contains(o);
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(V e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return backing.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public ObjectIterator<V> iterator() {
			return FastUtilHackUtil.itrWrap(backing);
		}

	}

	public static <K> ObjectCollection<K> wrap(Collection<K> c) {
		return new WrappingObjectCollection<K>(c);
	}
	public static BooleanCollection wrapBoolean(Collection<Boolean> c) {
		return new WrappingBooleanCollection(c);
	}

	public static <K> ReferenceCollection<K> wrapReference(Collection<K> c) {
		return new WrappingReferenceCollection<K>(c);
	}

	public static class WrappingByteCollection implements ByteCollection {

		Collection<Byte> backing;

		public WrappingByteCollection(Collection<Byte> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(byte o) {
			return backing.contains(o);
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(byte e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return backing.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Byte> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public ByteIterator iterator() {
			return FastUtilHackUtil.itrByteWrap(backing);
		}

		@Override
		public boolean rem(byte key) {
			return this.remove(key);
		}

		@Override
		public byte[] toByteArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public byte[] toByteArray(byte[] a) {
			return toArray(a);
		}

		@Override
		public byte[] toArray(byte[] a) {
			return ArrayUtils.toPrimitive(backing.toArray(new Byte[0]));
		}

		@Override
		public boolean addAll(ByteCollection c) {
			return addAll((Collection<Byte>) c);
		}

		@Override
		public boolean containsAll(ByteCollection c) {
			return containsAll((Collection<Byte>) c);
		}

		@Override
		public boolean removeAll(ByteCollection c) {
			return removeAll((Collection<Byte>) c);
		}

		@Override
		public boolean retainAll(ByteCollection c) {
			return retainAll((Collection<Byte>) c);
		}

	}

	public static ByteCollection wrapBytes(Collection<Byte> c) {
		return new WrappingByteCollection(c);
	}

	public static class WrappingIntCollection implements IntCollection {

		Collection<Integer> backing;

		public WrappingIntCollection(Collection<Integer> backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(int o) {
			return backing.contains(o);
		}

		@Override
		public Object[] toArray() {
			return backing.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(int e) {
			return backing.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return backing.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Integer> c) {
			return backing.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return backing.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return backing.retainAll(c);
		}

		@Override
		public void clear() {
			backing.clear();
		}

		@Override
		public IntIterator iterator() {
			return FastUtilHackUtil.itrIntWrap(backing);
		}

		@Override
		public boolean rem(int key) {
			return this.remove(key);
		}

		@Override
		public int[] toIntArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int[] toIntArray(int[] a) {
			return toArray(a);
		}

		@Override
		public int[] toArray(int[] a) {
			return ArrayUtils.toPrimitive(backing.toArray(new Integer[0]));
		}

		@Override
		public boolean addAll(IntCollection c) {
			return addAll((Collection<Integer>) c);
		}

		@Override
		public boolean containsAll(IntCollection c) {
			return containsAll((Collection<Integer>) c);
		}

		@Override
		public boolean removeAll(IntCollection c) {
			return removeAll((Collection<Integer>) c);
		}

		@Override
		public boolean retainAll(IntCollection c) {
			return retainAll((Collection<Integer>) c);
		}

	}

	public static IntCollection wrapInts(Collection<Integer> c) {
		return new WrappingIntCollection(c);
	}


	public static class WrappingLongListIterator implements LongListIterator {

		ListIterator<Long> backing;

		public WrappingLongListIterator(ListIterator<Long> backing) {
			this.backing = backing;
		}

		@Override
		public long previousLong() {
			return backing.previous();
		}

		@Override
		public long nextLong() {
			return backing.next();
		}

		@Override
		public boolean hasNext() {
			return backing.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return backing.hasPrevious();
		}

		@Override
		public int nextIndex() {
			return backing.nextIndex();
		}

		@Override
		public int previousIndex() {
			return backing.previousIndex();
		}

		@Override
		public void add(long k) {
			backing.add(k);
		}

		@Override
		public void remove() {
			backing.remove();
		}

		@Override
		public void set(long k) {
			backing.set(k);
		}
	}

	public static class SlimWrappingLongListIterator implements LongListIterator {

		Iterator<Long> backing;

		public SlimWrappingLongListIterator(Iterator<Long> backing) {
			this.backing = backing;
		}

		@Override
		public long previousLong() {
			throw new IllegalStateException();
		}

		@Override
		public long nextLong() {
			return backing.next();
		}

		@Override
		public boolean hasNext() {
			return backing.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			throw new IllegalStateException();
		}

		@Override
		public int nextIndex() {
			throw new IllegalStateException();
		}

		@Override
		public int previousIndex() {
			throw new IllegalStateException();
		}

		@Override
		public void add(long k) {
			throw new IllegalStateException();
		}

		@Override
		public void remove() {
			backing.remove();
		}

		@Override
		public void set(long k) {
			throw new IllegalStateException();
		}
	}

	public static LongListIterator wrap(ListIterator<Long> c) {
		return new WrappingLongListIterator(c);
	}

	public static LongListIterator wrap(Iterator<Long> c) {
		return new SlimWrappingLongListIterator(c);
	}

	public static class WrappingByteIterator implements ByteIterator {

		Iterator<Byte> parent;

		public WrappingByteIterator(Iterator<Byte> parent) {
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			return parent.hasNext();
		}

		@Override
		public Byte next() {
			return parent.next();
		}

		@Override
		public void remove() {
			parent.remove();
		}

		@Override
		public byte nextByte() {
			return next();
		}

	}

	public static ByteIterator itrByteWrap(Iterator<Byte> backing) {
		return new WrappingByteIterator(backing);
	}

	public static ByteIterator itrByteWrap(Iterable<Byte> backing) {
		return new WrappingByteIterator(backing.iterator());
	}

	public static IntIterator itrIntWrap(Iterator<Integer> backing) {
		return new WrappingIntIterator(backing);
	}

	public static IntIterator itrIntWrap(Iterable<Integer> backing) {
		return new WrappingIntIterator(backing.iterator());
	}

	public static class WrapperObjectIterator<T> implements ObjectIterator<T> {

		Iterator<T> parent;

		public WrapperObjectIterator(Iterator<T> parent) {
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			return parent.hasNext();
		}

		@Override
		public T next() {
			return parent.next();
		}

		@Override
		public void remove() {
			parent.remove();
		}

	}

	public static class IntWrapperEntry<T> implements Entry<T> {

		java.util.Map.Entry<Integer, T> parent;

		public IntWrapperEntry(java.util.Map.Entry<Integer, T> parent) {
			this.parent = parent;
		}

		@Override
		public T getValue() {
			return parent.getValue();
		}

		@Override
		public T setValue(T value) {
			return parent.setValue(value);
		}

		@Override
		public int getIntKey() {
			return parent.getKey();
		}

		@Override
		public Integer getKey() {
			return parent.getKey();
		}

	}

	public static class Long2IntWrapperEntry implements Long2IntMap.Entry {

		java.util.Map.Entry<Long, Integer> parent;

		public Long2IntWrapperEntry(java.util.Map.Entry<Long, Integer> parent) {
			this.parent = parent;
		}

		@Override
		public long getLongKey() {
			return parent.getKey();
		}

		@Override
		public int getIntValue() {
			return parent.getValue();
		}

		@Override
		public int setValue(int value) {
			return parent.setValue(value);
		}


	}

	public static class WrapperIntEntryObjectIterator<T> implements ObjectIterator<Entry<T>> {

		Iterator<Map.Entry<Integer, T>> parent;

		public WrapperIntEntryObjectIterator(Iterator<Map.Entry<Integer, T>> parent) {
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			return parent.hasNext();
		}

		@Override
		public Entry<T> next() {
			Map.Entry<Integer, T> val = parent.next();
			if (val == null) throw new UnsupportedOperationException();
			return new IntWrapperEntry<T>(val);
		}

		@Override
		public void remove() {
			parent.remove();
		}

	}

	public static <T> ObjectIterator<Entry<T>> intMapItrFake(Map<Integer, T> in) {
		return new WrapperIntEntryObjectIterator<T>(in.entrySet().iterator());
	}

	public static <T> ObjectIterator<T> itrWrap(Iterator<T> in) {
		return new WrapperObjectIterator<T>(in);
	}

	public static <T> ObjectIterator<T> itrWrap(Iterable<T> in) {
		return new WrapperObjectIterator<T>(in.iterator());
	}

	public static LongSortedSet concurrentLongSet() {
		return FastUtilHackUtil.wrapLongSortedSet(new CopyOnWriteArraySet<>());
	}

	public static  ObjectSortedSet concurrentObjectSet() {
		return new WrappingObjectSortedSet(new CopyOnWriteArraySet());
	}

	public static <E> CopyOnWriteArrayList<E> concurrentList(Collection<? extends E> v) {
		return new CopyOnWriteArrayList(v);
	}

	public static <K, V> ConcurrentHashMap<K, V> concurrentMap(Map<? extends K, ? extends V> v) {
		return new ConcurrentHashMap(v);
	}

	public static <K> ConcurrentHashMap.KeySetView<K,Boolean> concurrentSet(Set<? extends K> v) {
		ConcurrentHashMap.KeySetView<K, Boolean> objects = ConcurrentHashMap.newKeySet();
		objects.addAll(v);
		return objects;
	}

	public static <V> ConcurrentInt2ObjectOpenHashMap<V> concurrentInt2ObjectMap(Int2ObjectMap<? extends V> v) {
		return new ConcurrentInt2ObjectOpenHashMap<>(v);
	}

	public static <V> ConcurrentLinkedQueue<V> concurrentQueue(Collection<? extends V> v) {
		return new ConcurrentLinkedQueue<>(v);
	}


}

