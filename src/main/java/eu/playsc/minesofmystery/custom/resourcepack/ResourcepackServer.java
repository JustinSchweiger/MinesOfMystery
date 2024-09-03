package eu.playsc.minesofmystery.custom.resourcepack;

import eu.playsc.minesofmystery.common.Common;
import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResourcepackServer extends NanoHTTPD {
	@Getter
	private static final int PORT = 6969;

	protected ResourcepackServer() {
		super(PORT);
		try {
			this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		} catch (final IOException e) {
			Common.error("Failed to start resourcepack server on port " + PORT, e);
		}
	}

	@Override
	public Response serve(final IHTTPSession session) {
		if ("/resourcepack.zip".equals(session.getUri())) {
			try {
				final FileInputStream inputStream = new FileInputStream(ResourcepackManager.getRESOURCEPACK());
				return newFixedLengthResponse(
						Response.Status.OK,
						"application/zip",
						inputStream,
						ResourcepackManager.getRESOURCEPACK().length()
				);
			} catch (final FileNotFoundException e) {
				return newFixedLengthResponse(
						Response.Status.NOT_FOUND,
						"text/plain",
						"File not found"
				);
			}
		}

		return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
	}
}
