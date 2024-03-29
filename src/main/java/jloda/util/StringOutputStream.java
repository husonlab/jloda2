/*
 * StringOutputStream.java Copyright (C) 2023 Daniel H. Huson
 *
 * (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jloda.util;

import java.io.OutputStream;

/**
 * a string output stream
 * Daniel Huson, 1.2022
 */
public class StringOutputStream extends OutputStream {
	private final StringBuilder string = new StringBuilder();

	@Override
	public void write(int b) {
		this.string.append((char) b);
	}

	@Override
	public String toString() {
		return this.string.toString();
	}
}
