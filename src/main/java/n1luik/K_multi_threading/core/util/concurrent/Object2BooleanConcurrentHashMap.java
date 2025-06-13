package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Object2BooleanConcurrentHashMap<K> implements Object2BooleanMap<K> {

	Map<K, Boolean> backing;

	public Object2BooleanConcurrentHashMap() {
		backing = new ConcurrentHashMap<K, Boolean>();
	}

	@Override
	public boolean getBoolean(Object key) {
		return backing.getOrDefault(key, false);
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return backing.containsValue(value);
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends Boolean> m) {
		backing.putAll(m);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public void defaultReturnValue(boolean rv) {
		throw new NotImplementedException("MCMT - Not implemented");
	}

	@Override
	public boolean defaultReturnValue() {
		return false;
	}

	@Override
	public ObjectSet<Entry<K>> object2BooleanEntrySet() {
		return FastUtilHackUtil.entrySetBooleanWrap(backing);
	}

	@Override
	public ObjectSet<K> keySet() {
		return new FastUtilHackUtil.ToObjectSet<>(backing.keySet());
	}

	@Override
	public BooleanCollection values() {
		return FastUtilHackUtil.wrapBoolean(backing.values());
	}

	@Override
	public boolean containsKey(Object key) {
		return backing.containsKey(key);
	}

	@Override
	public boolean containsValue(boolean value) {
		return backing.containsValue(value);
	}

	@Override
	public boolean put(K key, boolean value) {
		Boolean put = backing.put(key, value);
		return put != null && put;
	}

	@Override
	public Boolean put(K key, Boolean value) {
		return backing.put(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backing.remove(key, value);
	}

	@Override
	public boolean removeBoolean(Object key) {
		Boolean remove = backing.remove(key);
		return remove != null && remove;
	}

	@Override
	public boolean getOrDefault(Object key, boolean defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public @Nullable Boolean replace(K key, Boolean value) {
		return backing.replace(key, value);
	}

	@Override
	public Boolean getOrDefault(Object key, Boolean defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean replace(K key, boolean value) {
		Boolean replace = backing.replace(key, value);
		return replace != null && replace;
	}
}
