package eu.playsc.minesofmystery.resourcepack;

import eu.playsc.minesofmystery.common.Common;
import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResourcepackServer extends NanoHTTPD {
	@Getter
	private static final int port = 6969;

	protected ResourcepackServer() {
		super(port);
		try {
			this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			Common.log("Resourcepack server started on port " + port);
		} catch (final IOException e) {
			Common.throwError("Failed to start resourcepack server on port " + port, e);
		}
	}

	@Override
	public Response serve(final IHTTPSession session) {
		if ("/resourcepack.zip".equals(session.getUri())) {
			try {
				final FileInputStream fis = new FileInputStream(ResourcepackManager.getRESOURCEPACK());
				return newFixedLengthResponse(Response.Status.OK, "application/zip", fis, ResourcepackManager.getRESOURCEPACK().length());
			} catch (final FileNotFoundException e) {
				return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found");
			}
		}

		return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
	}
}
