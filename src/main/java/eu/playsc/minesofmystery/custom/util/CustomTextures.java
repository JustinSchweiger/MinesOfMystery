package eu.playsc.minesofmystery.custom.util;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class CustomTextures {
	private final JSONObject textures;
	private boolean hasTextures = false;

	public CustomTextures(final JSONObject textures) {
		this.textures = textures;

		if (textures != null) {
			this.hasTextures = true;
		}
	}

	public CustomTextures() {
		this.textures = null;
		this.hasTextures = false;
	}
}
