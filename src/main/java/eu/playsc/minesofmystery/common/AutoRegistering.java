package eu.playsc.minesofmystery.common;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.annotations.AutoRegister;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoRegistering {
	public static void findAndRegister() {
		Common.log("Registering listeners...");

		final List<Class<?>> classes = findClasses();

		final AtomicInteger registered = new AtomicInteger();

		classes.forEach(clazz -> {
			try {
				final Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
				MinesOfMystery.getInstance().getServer().getPluginManager().registerEvents(listener, MinesOfMystery.getInstance());
				registered.getAndIncrement();
			} catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				Common.log("Failed to register class " + clazz.getName() + "!", LogLevel.ERROR);
			}
		});

		Common.log("Registered " + registered.get() + " listeners!", LogLevel.SUCCESS);
	}

	private static List<Class<?>> findClasses() {
		final Reflections reflections = new Reflections("eu.playsc.minesofmystery");
		final List<Class<?>> classes = new ArrayList<>(reflections.getTypesAnnotatedWith(AutoRegister.class));

		classes.forEach(clazz -> {
			if (!Listener.class.isAssignableFrom(clazz)) {
				Common.log("Class " + clazz.getName() + " is annotated with @AutoRegister but does not implement Listener! It will not be registered!", LogLevel.ERROR);
				classes.remove(clazz);
			}
		});

		return classes;
	}
}
