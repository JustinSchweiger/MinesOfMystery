package eu.playsc.minesofmystery.custom.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

@Getter
public class CustomBlockSettings {
	private final boolean isSolid;
	private final boolean isGlowing;
	private final boolean isBreakable;
	private final BlockData breakParticle;

	public CustomBlockSettings(final boolean isSolid, final boolean isGlowing, final boolean isBreakable) {
		this(isSolid, isGlowing, isBreakable, Material.STONE.createBlockData());
	}

	public CustomBlockSettings(final boolean isSolid, final boolean isGlowing, final boolean isBreakable, final BlockData breakParticle) {
		this.isSolid = isSolid;
		this.isGlowing = isGlowing;
		this.isBreakable = isBreakable;
		this.breakParticle = breakParticle;
	}
}
