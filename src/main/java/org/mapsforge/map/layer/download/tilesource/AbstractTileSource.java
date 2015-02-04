/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download.tilesource;

public abstract class AbstractTileSource implements TileSource {
	private static final int TWO_POW_16 = 65536; // 1 << 16
	private static final int PRIME = 31;
	private final String hostName;
	private final int port;

	protected AbstractTileSource(final String hostName, final int port) {
		if (hostName == null || hostName.isEmpty()) {
			throw new IllegalArgumentException("no host name specified");
		} else if (port < 0 || port >= TWO_POW_16) {
			throw new IllegalArgumentException("invalid port number: " + port);
		}

		this.hostName = hostName;
		this.port = port;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof AbstractTileSource)) {
			return false;
		}
		final AbstractTileSource other = (AbstractTileSource) obj;
		if (!this.hostName.equals(other.hostName)) {
			return false;
		} else if (this.port != other.port) {
			return false;
		}
		return true;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = PRIME * result + this.hostName.hashCode();
		result = PRIME * result + this.port;
		return result;
	}
}
