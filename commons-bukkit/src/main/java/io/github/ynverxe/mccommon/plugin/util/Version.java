package io.github.ynverxe.mccommon.plugin.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(int major, int minor, int patch, @Nullable String optionalPart, boolean isPaper) implements Comparable<Version> {

  private static final Pattern PATTERN = Pattern.compile("^(?:(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-((?:pre|rc)\\d+))?|((\\d{2})w(\\d{2})([a-z])))$");

  public static @NotNull Version parse(@NotNull String s) {
    Matcher matcher = PATTERN.matcher(s);

    if (!matcher.find()) {
      throw new IllegalArgumentException("Invalid version string");
    }

    if (matcher.group(5) != null) {
      throw new IllegalArgumentException("Snapshot versions aren't supported");
    }

    int major = Integer.parseInt(matcher.group(1));
    int minor = Integer.parseInt(matcher.group(2));
    int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
    String optionalPart = matcher.group(4) != null ? matcher.group(4) : null;

    boolean isPaper = false;
    try {
      Class.forName("com.destroystokyo.paper.ParticleBuilder");
      isPaper = true;
    } catch (ClassNotFoundException ignored) {
    }

    return new Version(major, minor, patch, optionalPart, isPaper);
  }

  @Override
  public int compareTo(@NotNull Version other) {
    if (this.major != other.major) {
      return Integer.compare(this.major, other.major);
    }
    if (this.minor != other.minor) {
      return Integer.compare(this.minor, other.minor);
    }
    if (this.patch != other.patch) {
      return Integer.compare(this.patch, other.patch);
    }

    if (this.optionalPart == null && other.optionalPart == null) {
      return 0;
    }
    if (this.optionalPart == null) {
      return 1;
    }
    if (other.optionalPart == null) {
      return -1;
    }

    String thisType = this.optionalPart.substring(0, 3).toLowerCase();
    int thisNum = Integer.parseInt(this.optionalPart.substring(3));

    String otherType = other.optionalPart.substring(0, 3).toLowerCase();
    int otherNum = Integer.parseInt(other.optionalPart.substring(3));

    if (!thisType.equals(otherType)) {
      if (thisType.equals("pre") && otherType.equals("rc")) return -1;
      if (thisType.equals("rc") && otherType.equals("pre")) return 1;
    }

    return Integer.compare(thisNum, otherNum);
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch + (optionalPart == null ? "" : "-" + optionalPart);
  }

  public static @NotNull Version current() {
    return parse(Bukkit.getMinecraftVersion());
  }
}