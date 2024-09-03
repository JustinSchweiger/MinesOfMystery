package eu.playsc.minesofmystery.huds;

import eu.playsc.minesofmystery.common.Formatter;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.fonts.Icons;
import eu.playsc.minesofmystery.custom.fonts.Spaces;
import eu.playsc.minesofmystery.custom.hud.ActionBarHud;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import eu.playsc.minesofmystery.model.PlayerData;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(force = true)
public class ManaHud extends ActionBarHud {
	public ManaHud(final Player player) {
		super(player);
	}

	@Override
	public String getKey() {
		return "mana";
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public Map<CustomFont, CustomFont.Overwrite> getOverwrites() {
		final long Y_OFFSET_BAR = -47;
		final long Y_OFFSET_TEXT = -52;

		return new HashMap<>() {{
			this.putAll(Caps.getOverwrite(Caps.Type.DIGIT, Y_OFFSET_TEXT));
			this.putAll(Caps.getOverwrite(Caps.Type.SYMBOL, Y_OFFSET_TEXT));
			this.put(Icons.MANABAR_BORDER_LEFT, new CustomFont.Overwrite(Y_OFFSET_BAR));
			this.put(Icons.MANABAR_BORDER_RIGHT, new CustomFont.Overwrite(Y_OFFSET_BAR));
			this.put(Icons.MANABAR_FULL, new CustomFont.Overwrite(Y_OFFSET_BAR));
			this.put(Icons.MANABAR_EMPTY, new CustomFont.Overwrite(Y_OFFSET_BAR));
		}};
	}

	@Override
	public void render() {
		final PlayerData playerData = PlayerData.from(this.getPlayer());
		if (playerData == null || !playerData.isEnderDragonDefeated()) {
			return;
		}

		Component manaBar = new Formatter.Manabar(playerData.getMaxMana(), playerData.getCurrentMana())
				.withSize(100)
				.withFont(this.getFont())
				.build();

		manaBar = manaBar
				.append(Spaces.negative(100))
				.append(Caps.get(playerData.getCurrentMana() + "/" + playerData.getMaxMana()).font(this.getFont()));

		final CustomFont.CountResult countResult = CustomFont.countCharacters(manaBar);
		final int offset = countResult.calculateWidth(this.getOverwrites());

		this.update(manaBar, offset);
	}
}
