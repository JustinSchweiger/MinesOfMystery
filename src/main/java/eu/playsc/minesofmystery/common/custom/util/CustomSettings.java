package eu.playsc.minesofmystery.common.custom.util;

import lombok.Getter;

@Getter
public class CustomSettings {
	private final boolean isSolid;
	private final boolean isGlowing;
	private final boolean isBreakable;

	public CustomSettings(final boolean isSolid, final boolean isGlowing, final boolean isBreakable) {
		this.isSolid = isSolid;
		this.isGlowing = isGlowing;
		this.isBreakable = isBreakable;
	}
}
