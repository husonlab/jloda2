/*
 * SequenceUtils.java Copyright (C) 2023 Daniel H. Huson
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

package jloda.seq;

import java.io.StringWriter;

/**
 * some utilities for DNA and amino acid sequences
 * Daniel Huson, 9.2011
 */
public class SequenceUtils {
    private final static byte[][][] codon2aminoAcid = new byte[127][127][127];

    static {
        // initialize the codon2aminoAcid table
        String nucleotides = "actgACGTuUN-";
        for (int i = 0; i < nucleotides.length(); i++) {
            char a = nucleotides.charAt(i);
            for (int j = 0; j < nucleotides.length(); j++) {
                char b = nucleotides.charAt(j);
                for (int k = 0; k < nucleotides.length(); k++) {
                    char c = nucleotides.charAt(k);
                    codon2aminoAcid[a][b][c] = getAminoAcidInit(a, b, c);
                }
            }
        }
    }

    /**
     * translate DNA into amino acids
     *
     * @return amino acid
     */
    static private byte getAminoAcidInit(int c1, int c2, int c3) {
        c1 = Character.toUpperCase(c1);
        if (c1 == 'T')
            c1 = 'U';
        c2 = Character.toUpperCase(c2);
        if (c2 == 'T')
            c2 = 'U';
        c3 = Character.toUpperCase(c3);
        if (c3 == 'T')
            c3 = 'U';

        if (c1 == '-' || c2 == '-' || c3 == '-')
            return '-';

		switch (c1) {
			case 'U' -> {
				switch (c2) {
					case 'U' -> {
						return switch (c3) {
							case 'U' -> (byte) 'F';
							case 'C' -> (byte) 'F';
							case 'A' -> (byte) 'L';
							case 'G' -> (byte) 'L';
							default -> (byte) 'X';
						};
					}
					case 'C' -> {
						return switch (c3) {
							case 'U' -> (byte) 'S';
							case 'C' -> (byte) 'S';
							case 'A' -> (byte) 'S';
							case 'G' -> (byte) 'S';
							default -> (byte) 'S';
						};
					}
					case 'A' -> {
						return switch (c3) {
							case 'U' -> (byte) 'Y';
							case 'C' -> (byte) 'Y';
							case 'A' -> (byte) '*';
							case 'G' -> (byte) '*';
							default -> (byte) 'X';
						};
					}
					case 'G' -> {
						return switch (c3) {
							case 'U' -> (byte) 'C';
							case 'C' -> (byte) 'C';
							case 'A' -> (byte) '*';
							case 'G' -> (byte) 'W';
							default -> (byte) 'X';
						};
					}
					default -> {
						return (byte) 'X';
					}
				}
			}
			case 'C' -> {
				switch (c2) {
					case 'U' -> {
						return switch (c3) {
							case 'U' -> (byte) 'L';
							case 'C' -> (byte) 'L';
							case 'A' -> (byte) 'L';
							case 'G' -> (byte) 'L';
							default -> (byte) 'L';
						};
					}
					case 'C' -> {
						return switch (c3) {
							case 'U' -> (byte) 'P';
							case 'C' -> (byte) 'P';
							case 'A' -> (byte) 'P';
							case 'G' -> (byte) 'P';
							default -> (byte) 'P';
						};
					}
					case 'A' -> {
						return switch (c3) {
							case 'U' -> (byte) 'H';
							case 'C' -> (byte) 'H';
							case 'A' -> (byte) 'Q';
							case 'G' -> (byte) 'Q';
							default -> (byte) 'X';
						};
					}
					case 'G' -> {
						return switch (c3) {
							case 'U' -> (byte) 'R';
							case 'C' -> (byte) 'R';
							case 'A' -> (byte) 'R';
							case 'G' -> (byte) 'R';
							default -> (byte) 'R';
						};
					}
					default -> {
						return (byte) 'X';
					}
				}
			}
			case 'A' -> {
				switch (c2) {
					case 'U' -> {
						return switch (c3) {
							case 'U' -> (byte) 'I';
							case 'C' -> (byte) 'I';
							case 'A' -> (byte) 'I';
							case 'G' -> (byte) 'M';
							default -> (byte) 'X';
						};
					}
					case 'C' -> {
						return switch (c3) {
							case 'U' -> (byte) 'T';
							case 'C' -> (byte) 'T';
							case 'A' -> (byte) 'T';
							case 'G' -> (byte) 'T';
							default -> (byte) 'T';
						};
					}
					case 'A' -> {
						return switch (c3) {
							case 'U' -> (byte) 'N';
							case 'C' -> (byte) 'N';
							case 'A' -> (byte) 'K';
							case 'G' -> (byte) 'K';
							default -> (byte) 'X';
						};
					}
					case 'G' -> {
						return switch (c3) {
							case 'U' -> (byte) 'S';
							case 'C' -> (byte) 'S';
							case 'A' -> (byte) 'R';
							case 'G' -> (byte) 'R';
							default -> (byte) 'X';
						};
					}
					default -> {
						return (byte) 'X';
					}
				}
			}
			case 'G' -> {
				switch (c2) {
					case 'U' -> {
						return switch (c3) {
							case 'U' -> (byte) 'V';
							case 'C' -> (byte) 'V';
							case 'A' -> (byte) 'V';
							case 'G' -> (byte) 'V';
							default -> (byte) 'V';
						};
					}
					case 'C' -> {
						return switch (c3) {
							case 'U' -> (byte) 'A';
							case 'C' -> (byte) 'A';
							case 'A' -> (byte) 'A';
							case 'G' -> (byte) 'A';
							default -> (byte) 'A';
						};
					}
					case 'A' -> {
						return switch (c3) {
							case 'U' -> (byte) 'D';
							case 'C' -> (byte) 'D';
							case 'A' -> (byte) 'E';
							case 'G' -> (byte) 'E';
							default -> (byte) 'X';
						};
					}
					case 'G' -> {
						return switch (c3) {
							case 'U' -> (byte) 'G';
							case 'C' -> (byte) 'G';
							case 'A' -> (byte) 'G';
							case 'G' -> (byte) 'G';
							default -> (byte) 'G';
						};
					}
					default -> {
						return (byte) 'X';
					}
				}
			}
			default -> {
				return 'X';
			}
		}
    }

    /**
     * gets the amino acid for the codon starting the given position
     *
     * @return amino acid
     */
    static public byte getAminoAcid(byte[] sequence, int pos) {
        /*
        byte result=getAminoAcid(sequence[pos], sequence[pos + 1], sequence[pos + 2]);
        System.err.println(String.format("%c%c%c -> %c",sequence[pos],sequence[pos+1],sequence[pos+2],result));
        return result;
        */
        return getAminoAcid(sequence[pos], sequence[pos + 1], sequence[pos + 2]);
    }

    /**
     * gets the amino acid for the codon starting at the given position in the reverse strand.
     * To get the amino acid sequence of the reverse strand of DNA using this method,
     * start at beginning of leading strand, calling this method repeatedly, building the protein sequence from the end to the beginning
     *
     * @return amino acid
     */
    static public byte getAminoAcidReverse(byte[] sequence, int pos) {
        return getAminoAcid(getComplement(sequence[pos + 2]), getComplement(sequence[pos + 1]), getComplement(sequence[pos]));
    }

    /**
     * gets the amino acid for the codon a,b,cin the reverse strand.
     * To get the amino acid sequence of the reverse strand of DNA using this method,
     * start at the end of the leading strand and repeatedly call this method with letters at positions pos, pos-1, pos-2
     *
     * @param b param c
     * @return amino acid
     */
    static public byte getAminoAcidReverse(byte a, byte b, byte c) {
        return getAminoAcid(getComplement(a), getComplement(b), getComplement(c));
    }

    /**
     * gets the amino acid for the codon starting the given position
     *
     * @return amino acid
     */
    static public byte getAminoAcid(String sequence, int pos) {
        return getAminoAcid(sequence.charAt(pos), sequence.charAt(++pos), sequence.charAt(++pos));
    }

    /**
     * gets the amino acid for the codon starting at the given position in the reverse strand.
     * To get the amino acid sequence of the reverse strand of DNA using this method,
     * start at beginning of leading strand, calling this method repeatedly, building the protein sequence from the end to the beginning
     *
     * @return amino acid
     */
    static public byte getAminoAcidReverse(String sequence, int pos) {
        return getAminoAcid(getComplement((byte) sequence.charAt(pos + 2)), getComplement((byte) sequence.charAt(pos + 1)), getComplement((byte) sequence.charAt(pos)));
    }

    /**
     * translate DNA into amino acids
     *
     * @return amino acid
     */
    static public byte getAminoAcid(int c1, int c2, int c3) {
        try {
			byte aa = codon2aminoAcid[c1][c2][c3];
			if (aa != 0) {
				return aa;
			}
		} catch (Exception ignored) {
		}
        return 'X';
    }

    /**
     * is this a valid nucleotide (with ambiguity codes)
     *
     * @return true, if nucleotide
     */
    public static boolean isNucleotide(int ch) {
        return "atugckmryswbvhdxn".indexOf(Character.toLowerCase(ch)) != -1;
    }

    /**
     * reverse complement of string
     *
     * @return reverse complement
     */
    public static String getReverseComplement(String readSequence) {
        StringBuilder buf = new StringBuilder();
        for (int i = readSequence.length() - 1; i >= 0; i--) {
            buf.append((char) getComplement((byte) readSequence.charAt(i)));
        }
        return buf.toString();
    }

    /**
     * gets the  complement of a nucleotide. Returns ambiguity codes unaltered.
     *
     * @return reverse complement
     */
    public static byte getComplement(byte nucleotide) {
		return switch (nucleotide) {
			case 'a' -> (byte) 't';
			case 'A' -> (byte) 'T';
			case 'c' -> (byte) 'g';
			case 'C' -> (byte) 'G';
			case 'g' -> (byte) 'c';
			case 'G' -> (byte) 'C';
			case 't' -> (byte) 'a';
			case 'T' -> (byte) 'A';
			default -> nucleotide;
		};
	}

    /**
     * reverses (but does NOT complement) a sequence
     *
     * @return reverse string (but not complemented
     */
    public static String getReverse(String sequence) {
        StringWriter w = new StringWriter();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            w.write(sequence.charAt(i));
        }
        return w.toString();
    }

    /**
     * reverses (but does NOT complement) a sequence
     *
     * @return reverse string (but not complemented)
     */
    public static byte[] getReverse(byte[] sequence) {
        byte[] result = new byte[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            result[i] = sequence[sequence.length - 1 - i];
        }
        return result;
    }

    /**
     * gets the reverse complement
     *
     * @return reverse complement
     */
    public static byte[] getReverseComplement(byte[] sequence) {
        byte[] result = new byte[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            result[i] = getComplement(sequence[sequence.length - 1 - i]);
        }
        return result;
    }

	/**
	 * gets the reverse complement of a segment
	 *
	 * @param offset   start of segment
	 * @param length   length of segment
	 */
	public static byte[] getReverseComplement(byte[] sequence, int offset, int length, byte[] result) {
		if (result == null)
			result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = getComplement(sequence[length + offset - 1 - i]);
		}
		return result;
	}

    public static void getSegment(byte[] sequence, int offset, int length, byte[] result) {
        if (result == null)
            result = new byte[length];
        System.arraycopy(sequence, offset, result, 0, length);

    }

    /**
     * translate a DNA sequence into protein
     *
	 */
    public static byte[] translate(boolean reverse, int shift, String sequence) {
        byte[] result = new byte[(sequence.length() - shift) / 3];
        if (!reverse) {
            int pos = 0;
            for (int i = shift; i <= sequence.length() - 3; i += 3) {
                result[pos++] = getAminoAcid(sequence, i);
            }
        } else // reverse complement
        {
            int pos = 0;
            for (int i = sequence.length() - 1 - shift; i >= 2; i -= 3) {
                if (i + 2 < sequence.length())
                    result[pos++] = getAminoAcidReverse(sequence, i);
            }
        }
        return result;
    }

    /**
     * copies a string to a byte array, 0 terminated.
     * Note that the length of bytes is usually larger than the string length
     *
     * @return 0-terminated bytes
     */
    public static byte[] getBytes0Terminated(String string, byte[] bytes) {
        if (bytes.length < string.length() + 1)
            bytes = new byte[2 * string.length() + 1];
        for (int i = 0; i < string.length(); i++)
            bytes[i] = (byte) string.charAt(i);
        bytes[string.length()] = 0;
        return bytes;

    }

    /**
     * convert 0 terminated bytes to string
     *
     * @return string
     */
    public static String getStringFromBytes0Terminated(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (byte aByte : bytes) {
            if (aByte == 0)
                break;
            buf.append((char) aByte);
        }
        return buf.toString();
    }

    /**
     * counts how many times each of the given symbols have been used
     *
     * @return usage
     */
    public static int[] computeUsageCounts(byte[] sequence, byte[] symbols) {
        int[] counts = new int[symbols.length];

        for (byte b : sequence) {
            for (int j = 0; j < symbols.length; j++) {
                if (symbols[j] == b)
                    counts[j]++;
            }
        }
        return counts;
    }

    /**
     * count the number of gaps ('-') in a sequence
     *
     * @return number of gaps
     */
    public static int countGaps(String sequence) {
        int count = 0;
        for (int i = 0; i < sequence.length(); i++)
            if (sequence.charAt(i) == '-')
                count++;
        return count;
    }

    /**
     * count the number of gaps ('-') in a sequence
     *
     * @return number of gaps
     */
    public static int countGaps(byte[] sequence) {
        int count = 0;
        for (byte aSequence : sequence)
            if (aSequence == '-')
                count++;
        return count;
    }

    public static int compare(byte[] str1, int offset1, byte[] str2, int offset2, int length) {
        length = Math.min(str1.length, length);
        length = Math.min(str2.length, length);

        for (int i = 0; i < length; i++) {
            if (str1[offset1 + i] < str2[offset2 + i])
                return -1;
            else if (str1[offset1 + i] > str2[offset2 + i])
                return 1;
        }
        return -Integer.compare(str1.length, str2.length);
    }

    public static int compare(byte[] str1, byte[] str2) {
        final int top = Math.min(str1.length, str2.length);
        for (int i = 0; i < top; i++) {
            if (str1[i] < str2[i])
                return -1;
            else if (str1[i] > str2[i])
                return 1;
        }
        return -Integer.compare(str1.length, str2.length);
    }
}
