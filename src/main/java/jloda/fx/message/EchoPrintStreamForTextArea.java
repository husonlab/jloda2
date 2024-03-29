/*
 * EchoPrintStreamForTextArea.java Copyright (C) 2023 Daniel H. Huson
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

package jloda.fx.message;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import jloda.util.StringUtils;

import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * print stream that sends text to text area
 * Daniel Huson, 11.2022
 */
public class EchoPrintStreamForTextArea extends PrintStream {
	private final LinkedBlockingQueue<String> lines = new LinkedBlockingQueue<>();

	/**
	 * constructor
	 *
	 * @param ps       stream to echo
	 * @param textArea text area to echo to
	 */
	public EchoPrintStreamForTextArea(PrintStream ps, TextArea textArea) {
		this(ps, s -> s, textArea);
	}

	/**
	 * constructor
	 *
	 * @param ps       stream to echo
	 * @param filter   filters output, only non-null results are shown in the text area
	 * @param textArea text area to echo to
	 */
	public EchoPrintStreamForTextArea(PrintStream ps, Function<String, String> filter, TextArea textArea) {
		super(ps);

		// will queue lines and print out sparingly
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			if (!lines.isEmpty()) {
				Platform.runLater(() -> {
					final long start = System.currentTimeMillis();
					while (!lines.isEmpty()) {
						final var line = lines.remove();
						ps.print(line);
						Platform.runLater(() -> {
							var filtered = filter.apply(line);
							if (filtered != null) {
								textArea.appendText(line);
								textArea.positionCaret(textArea.getText().length());
								textArea.setScrollTop(Double.MAX_VALUE);
							}
						});
						if (System.currentTimeMillis() - start > 100)
							break;
					}
				});
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
	}

	public void println(String x) {
		lines.add(x+"\n");
	}

	public void print(String x) {
		lines.add(x);
	}

	public void println(Object x) {
		lines.add(x + "\n");
	}

	public void print(Object x) {
		lines.add(x == null ? null : x.toString());
	}

	public void println(boolean x) {
		lines.add(x + "\n");
	}

	public void print(boolean x) {
		lines.add("" + x);
	}

	public void println(int x) {
		lines.add(x + "\n");
	}

	public void print(int x) {
		lines.add("" + x);
	}

	public void println(float x) {
		lines.add(x + "\n");
	}

	public void print(float x) {
		lines.add("" + x);
	}

	public void println(char x) {
		lines.add(x + "\n");
	}

	public void print(char x) {
		lines.add("" + x);
	}

	public void println(double x) {
		lines.add(x + "\n");
	}

	public void print(double x) {
		lines.add("" + x);
	}

	public void println(long x) {
		lines.add(x + "\n");
	}

	public void print(long x) {
		lines.add("" + x);
	}

	public void println(char[] x) {
		lines.add(StringUtils.toString(x) + "\n");
	}

	public void print(char[] x) {
		lines.add(StringUtils.toString(x));
	}

	public void write(byte[] buf, int off, int len) {
		lines.add(new String(buf, off, len));
	}

	public void setError() {
	}

	public boolean checkError() {
		return false;
	}

	public void flush() {
	}
}
