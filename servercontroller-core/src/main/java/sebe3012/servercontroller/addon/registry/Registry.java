package sebe3012.servercontroller.addon.registry;

import sebe3012.servercontroller.api.addon.Addon;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registry<T> {
	private Map<Addon, List<T>> entries = new HashMap<>();

	public void registerEntry(@NotNull Addon addon, @NotNull T entry) {
		entries.computeIfAbsent(addon, a -> new ArrayList<>());
		entries.get(addon).add(entry);
	}

	public void unregisterEntry(@NotNull Addon addon, @NotNull T entry) {
		entries.computeIfAbsent(addon, a -> new ArrayList<>());
		entries.get(addon).remove(entry);
	}

	@NotNull
	public List<T> getEntries(Addon addon) {
		entries.computeIfAbsent(addon, a -> new ArrayList<>());
		return Collections.unmodifiableList(entries.get(addon));
	}

	@NotNull
	public Collection<List<T>> getValues(){
		return Collections.unmodifiableCollection(entries.values());
	}

	@NotNull
	public Map<Addon, List<T>> getData(){
		return Collections.unmodifiableMap(entries);
	}

}
