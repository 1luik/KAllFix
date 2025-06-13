package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FalseObjectLinkedOpenHashSet<T> extends ObjectLinkedOpenHashSet<T> {

		Set<T> backing;
		public FalseObjectLinkedOpenHashSet() {
			this.backing = ConcurrentHashMap.newKeySet();
		}
		public FalseObjectLinkedOpenHashSet(Set<T> backing) {
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
		public ObjectListIterator<T> iterator(T fromElement) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectListIterator<T> iterator() {
			final Iterator<T> backg = backing.iterator();
			return new ObjectListIterator<T>() {

				@Override
				public T previous() {
					throw new UnsupportedOperationException();
				}

				@Override
				public int nextIndex() {
					throw new UnsupportedOperationException();
				}

				@Override
				public int previousIndex() {
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
