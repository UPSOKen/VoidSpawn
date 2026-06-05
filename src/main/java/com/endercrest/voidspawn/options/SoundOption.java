package com.endercrest.voidspawn.options;

import com.endercrest.voidspawn.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SoundOption extends BaseOption<Sound> {
    private static final Method SOUND_NAME_METHOD = getSoundMethod("name");
    private static final Method SOUND_VALUE_OF_METHOD = getSoundMethod("valueOf", String.class);
    private static final List<String> sounds = loadSounds();

    private static List<String> loadSounds() {
        List<String> loadedSounds = new ArrayList<>();
        for (Sound sound : Registry.SOUNDS) {
            loadedSounds.add(getSoundName(sound));
        }
        return Collections.unmodifiableList(loadedSounds);
    }

    private static Method getSoundMethod(String methodName, Class<?>... parameterTypes) {
        try {
            return Sound.class.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String getSoundName(Sound sound) {
        try {
            return (String) SOUND_NAME_METHOD.invoke(sound);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to read sound name.", e);
        }
    }

    private static Optional<Sound> getSound(String value) {
        if (value == null) return Optional.empty();

        String normalizedValue = value.toLowerCase(Locale.ROOT);
        NamespacedKey key = normalizedValue.contains(":")
                ? NamespacedKey.fromString(normalizedValue)
                : NamespacedKey.minecraft(normalizedValue.replace('_', '.'));

        if (key != null) {
            Sound sound = Registry.SOUNDS.get(key);
            if (sound != null) return Optional.of(sound);
        }

        try {
            return Optional.of((Sound) SOUND_VALUE_OF_METHOD.invoke(null, value.toUpperCase(Locale.ROOT)));
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }

    public SoundOption(OptionIdentifier<Sound> identifier) {
        super(identifier);
    }

    @Override
    public Optional<Sound> getLoadedValue(@NotNull World world) {
        String value = ConfigManager.getInstance().getOption(world.getName(), getIdentifier());
        if (value == null) return Optional.empty();

        return getSound(value);
    }

    @Override
    public void setValue(@NotNull World world, String value) {
        if (value != null && getSound(value).isEmpty()) {
            throw new IllegalArgumentException(value + " is not a valid sound!");
        }
        super.setValue(world, value);
    }

    @Override
    public void setValue(@NotNull World world, String[] args) throws IllegalArgumentException {
        setValue(world, String.join(" ", args));
    }

    @Override
    public List<String> getOptions() {
        return sounds;
    }
}
