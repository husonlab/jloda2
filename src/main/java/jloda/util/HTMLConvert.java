/*
 *  HTMLConvert.java Copyright (C) 2022 Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jloda.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * convert HTML characters entities into characters and vice versa
 * Daniel Huson, 3.2022
 */
public class HTMLConvert {
	private final static String nameHtmlUnicode = "lceil	8968\n" +
												  "rceil	8969\n" +
												  "lfloor	8970\n" +
												  "rfloor	8971\n" +
												  "lang	9001\n" +
												  "rang	9002\n" +
												  "spades	9824\n" +
												  "clubs	9827\n" +
												  "hearts	9829\n" +
												  "diams	9830\n" +
												  "ndash	8211\n" +
												  "mdash	8212\n" +
												  "dagger	8224\n" +
												  "ddagger	8225\n" +
												  "permil	8240\n" +
												  "lsaquo	8249\n" +
												  "rsaquo	8250\n" +
												  "BlackSquare	9632\n" +
												  "WhiteSquare	9633\n" +
												  "WhiteSquareWithRoundedCorners	9634\n" +
												  "WhiteSquareContainingBlackSmallSquare	9635\n" +
												  "SquareWithHorizontalFill	9636\n" +
												  "SquareWithVerticalFill	9637\n" +
												  "SquareWithOrthogonalCrosshatch	9638\n" +
												  "SquareWithUpperLeftToLowerRightFill	9639\n" +
												  "SquareWithUpperRightToLowerLeftFill	9640\n" +
												  "SquareWithDiagonalCrosshatchFill	9641\n" +
												  "BlackSmallSquare	9642\n" +
												  "WhiteSmallSquare	9643\n" +
												  "BlackRectangle	9644\n" +
												  "WhiteRectangle	9645\n" +
												  "BlackVerticalRectangle	9646\n" +
												  "WhiteVerticalRectangle	9647\n" +
												  "BlackParallelogram	9648\n" +
												  "WhiteParallelogram	9649\n" +
												  "BlackUpPointingTriangle	9650\n" +
												  "WhiteUpPointingTriangle	9651\n" +
												  "BlackUpPointingSmallTriangle	9652\n" +
												  "WhiteUpPointingSmallTriangle	9653\n" +
												  "BlackRightPointingTriangle	9654\n" +
												  "WhiteRightPointingTriangle	9655\n" +
												  "BlackRightPointingSmallTriangle	9656\n" +
												  "WhiteRightPointingSmallTriangle	9657\n" +
												  "BlackRightPointingPointer	9658\n" +
												  "WhiteRightPointingPointer	9659\n" +
												  "BlackDownPointingTriangle	9660\n" +
												  "WhiteDownPointingTriangle	9661\n" +
												  "BlackDownPointingSmallTriangle	9662\n" +
												  "WhiteDownPointingSmallTriangle	9663\n" +
												  "BlackLeftPointingTriangle	9664\n" +
												  "WhiteLeftPointingTriangle	9665\n" +
												  "BlackLeftPointingSmallTriangle	9666\n" +
												  "WhiteLeftPointingSmallTriangle	9667\n" +
												  "BlackLeftPointingPointer	9668\n" +
												  "WhiteLeftPointingPointer	9669\n" +
												  "BlackDiamond	9670\n" +
												  "WhiteDiamond	9671\n" +
												  "WhiteDiamondContainingBlackSmallDiamond	9672\n" +
												  "Fisheye	9673\n" +
												  "Lozenge	9674\n" +
												  "WhiteCircle	9675\n" +
												  "DottedCircle	9676\n" +
												  "CircleWithVerticalFill	9677\n" +
												  "Bullseye	9678\n" +
												  "BlackCircle	9679\n" +
												  "CircleWithLeftHalfBlack	9680\n" +
												  "CircleWithRightHalfBlack	9681\n" +
												  "CircleWithLowerHalfBlack	9682\n" +
												  "CircleWithUpperHalfBlack	9683\n" +
												  "CircleWithUpperRightQuadrantBlack	9684\n" +
												  "CircleWithAllButUpperLeftQuadrantBlack	9685\n" +
												  "LeftHalfBlackCircle	9686\n" +
												  "RightHalfBlackCircle	9687\n" +
												  "InverseBullet	9688\n" +
												  "InverseWhiteCircle	9689\n" +
												  "BlackLowerRightTriangle	9698\n" +
												  "BlackLowerLeftTriangle	9699\n" +
												  "BlackUpperLeftTriangle	9700\n" +
												  "BlackUpperRightTriangle	9701\n" +
												  "WhiteBullet	9702\n" +
												  "SquareWithLeftHalfBlack	9703\n" +
												  "SquareWithRightHalfBlack	9704\n" +
												  "SquareWithUpperLeftDiagonalHalfBlack	9705\n" +
												  "SquareWithLowerRightDiagonalHalfBlack	9706\n" +
												  "WhiteUpPointingTriangleWithDot	9708\n" +
												  "UpPointingTriangleWithLeftHalfBlack	9709\n" +
												  "UpPointingTriangleWithRightHalfBlack	9710\n" +
												  "LargeCircle	9711\n" +
												  "UpperLeftTriangle	9720\n" +
												  "UpperRightTriangle	9721\n" +
												  "LowerLeftTriangle	9722\n" +
												  "WhiteMediumSquare	9723\n" +
												  "BlackMediumSquare	9724\n" +
												  "WhiteMediumSmallSquare	9725\n" +
												  "BlackMediumSmallSquare	9726\n" +
												  "LowerRightTriangle	9727\n" +
												  "CIRCLE_DONE	10112\n" +
												  "CIRCLED_TWO	10113\n" +
												  "CIRCLED_THREE	10114\n" +
												  "CIRCLED_FOUR	10115\n" +
												  "CIRCLED_FIVE	10116\n" +
												  "CIRCLED_SIX	10117\n" +
												  "CIRCLED_SEVEN	10118\n" +
												  "CIRCLED_EIGHT	10119\n" +
												  "CIRCLED_NINE	10120\n" +
												  "CIRCLED_TEN	10121\n" +
												  "BLACK_STAR	9733\n" +
												  "WHITE_STAR	9734\n" +
												  "CHECK_BOX	9745\n" +
												  "CHECKED_BOX	9746\n" +
												  "PEACE_SIGN	9774\n" +
												  "YIN_YANG	9775\n" +
												  "FROWNING_FACE	9785\n" +
												  "SMILING_FACE	9786\n" +
												  "WARNING_SIGN	9888\n" +
												  "HIGH_VOLTAGE	9889\n" +
												  "CHECK_MARK	10003\n" +
												  "HEAVY_CHECKMARK	10004\n" +
												  "MULTIPLICATION_X	10005\n" +
												  "HEAVY_MULTIPLICATION_X	10006\n" +
												  "BALLOT_	10007\n" +
												  "HEAVY_BALLOT	10008\n";

	public final static Map<String, Character> htmlCharacterMap = new HashMap<>();
	public final static Map<Character, String> characterHtmlMap = new HashMap<>();

	static {
		nameHtmlUnicode.lines().map(line -> line.split("\t")).filter(tokens -> tokens.length == 2)
				.forEach(tokens -> {
					var htmlName = String.format("&%s;", tokens[0].trim());
					var ch = (char) Integer.parseInt(tokens[1]);
					htmlCharacterMap.put(htmlName.toLowerCase(), ch);
					if (htmlName.contains("_")) // is not an offical HTML name
						characterHtmlMap.put(ch, String.format("&#%s;", tokens[1]));
					else
						characterHtmlMap.put(ch, htmlName.toLowerCase());
				});
	}

	/**
	 * converts HTML entities into characters
	 *
	 * @param text source text
	 * @return text in which HTML entities have been replaced by characters
	 */
	public static String convertHtmlToCharacters(String text) {
		var buf = new StringBuilder();
		for (var pos = 0; pos < text.length(); pos++) {
			var ch = text.charAt(pos);
			var replaced = false;
			if (ch == '\\' && pos + 5 < text.length() && text.charAt(pos + 1) == 'u') {
				var endPos = pos + 6;
				var word = text.substring(pos + 2, endPos);
				if (NumberUtils.isInteger(word, 16)) {
					var character = (char) NumberUtils.parseInt(word, 16);
					buf.append(character);
					pos = endPos - 1;
					replaced = true;
				}
			} else if (ch == '&') {
				var endPos = text.indexOf(";", pos + 2);
				if (endPos != -1) {
					var word = text.substring(pos, endPos + 1);
					var character = htmlCharacterMap.get(word.toLowerCase());
					if (character == null) {
						if (word.startsWith("&#x") && NumberUtils.isInteger(word.substring(3, word.length() - 1), 16)) {
							character = (char) NumberUtils.parseInt(word.substring(3, word.length() - 1), 16);
						} else if (word.startsWith("&#") && NumberUtils.isInteger(word.substring(2, word.length() - 1))) {
							character = (char) NumberUtils.parseInt(word.substring(2, word.length() - 1));
						}
					}
					if (character != null) {
						buf.append(character);
						pos = endPos;
						replaced = true;
					}
				}
			}
			if (!replaced) {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	public static void main(String[] args) {


		if (true) {
			for (var html : new TreeSet<>(htmlCharacterMap.keySet())) {
				System.err.println(html + " = " + htmlCharacterMap.get(html));
			}
		} else {
			var row = 0;
			var col = 0;

			for (var html : new TreeSet<>(htmlCharacterMap.keySet())) {
				var ch = htmlCharacterMap.get(html);
				System.err.printf("<Button alignment=\"CENTER\" layoutX=\"23.0\" layoutY=\"8.0\" mnemonicParsing=\"false\" prefHeight=\"16.0\" prefWidth=\"16.0\" style=\"-fx-background-color: transparent; -fx-border-color: transparent;\" text=\"%c\"  GridPane.rowIndex=\"%d\" GridPane.columnIndex=\"%d\" />%n",
						ch, row, col);
				if (++col == 12) {
					row++;
					col = 0;
				}
			}
		}

		System.err.println("Count: " + htmlCharacterMap.size());
	}
}
