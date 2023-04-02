/*
 * HeatSpectrum.java Copyright (C) 2023 Daniel H. Huson
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

package jloda.swing.util;

import java.awt.*;

/**
 * provides a heat spectrum from 0=cold to 500=hot
 * Daniel Huson, 12.2008
 */
public class HeatSpectrum {
    final static private Color[] spectrum =
            new Color[]
                    {new Color(-13290435),
                            new Color(-13290435),
                            new Color(-13356227),
                            new Color(-13487555),
                            new Color(-13487554),
                            new Color(-13553345),
                            new Color(-13619137),
                            new Color(-13684929),
                            new Color(-13750720),
                            new Color(-13816512),
                            new Color(-13882303),
                            new Color(-13948095),
                            new Color(-14013886),
                            new Color(-14079678),
                            new Color(-14145470),
                            new Color(-14211260),
                            new Color(-14342846),
                            new Color(-14408636),
                            new Color(-14474427),
                            new Color(-14606012),
                            new Color(-14671804),
                            new Color(-14737594),
                            new Color(-14869180),
                            new Color(-14934970),
                            new Color(-15066555),
                            new Color(-15132346),
                            new Color(-15198137),
                            new Color(-15329721),
                            new Color(-15395512),
                            new Color(-15461303),
                            new Color(-15592887),
                            new Color(-15658679),
                            new Color(-15724470),
                            new Color(-15790261),
                            new Color(-15856052),
                            new Color(-15921844),
                            new Color(-15987635),
                            new Color(-16053427),
                            new Color(-16119219),
                            new Color(-16185011),
                            new Color(-16250802),
                            new Color(-16316594),
                            new Color(-16316592),
                            new Color(-16382383),
                            new Color(-16448173),
                            new Color(-16448170),
                            new Color(-16513705),
                            new Color(-16513705),
                            new Color(-16579494),
                            new Color(-16579235),
                            new Color(-16645025),
                            new Color(-16644768),
                            new Color(-16644764),
                            new Color(-16710300),
                            new Color(-16710295),
                            new Color(-16710040),
                            new Color(-16709780),
                            new Color(-16709523),
                            new Color(-16709261),
                            new Color(-16774796),
                            new Color(-16774537),
                            new Color(-16774023),
                            new Color(-16773765),
                            new Color(-16773506),
                            new Color(-16772992),
                            new Color(-16772478),
                            new Color(-16771961),
                            new Color(-16771448),
                            new Color(-16770932),
                            new Color(-16770417),
                            new Color(-16769903),
                            new Color(-16769133),
                            new Color(-16768618),
                            new Color(-16767847),
                            new Color(-16767333),
                            new Color(-16766562),
                            new Color(-16765792),
                            new Color(-16765021),
                            new Color(-16764251),
                            new Color(-16763480),
                            new Color(-16762710),
                            new Color(-16761940),
                            new Color(-16760914),
                            new Color(-16760143),
                            new Color(-16759117),
                            new Color(-16758090),
                            new Color(-16757319),
                            new Color(-16756295),
                            new Color(-16755524),
                            new Color(-16754499),
                            new Color(-16753729),
                            new Color(-16752702),
                            new Color(-16751676),
                            new Color(-16750907),
                            new Color(-16749881),
                            new Color(-16749111),
                            new Color(-16748598),
                            new Color(-16747828),
                            new Color(-16747315),
                            new Color(-16746545),
                            new Color(-16746032),
                            new Color(-16745006),
                            new Color(-16743723),
                            new Color(-16742441),
                            new Color(-16741414),
                            new Color(-16740132),
                            new Color(-16739361),
                            new Color(-16738335),
                            new Color(-16736796),
                            new Color(-16736026),
                            new Color(-16734744),
                            new Color(-16733717),
                            new Color(-16732691),
                            new Color(-16731409),
                            new Color(-16730639),
                            new Color(-16729614),
                            new Color(-16728588),
                            new Color(-16727562),
                            new Color(-16726793),
                            new Color(-16725767),
                            new Color(-16724998),
                            new Color(-16724229),
                            new Color(-16723204),
                            new Color(-16722691),
                            new Color(-16721921),
                            new Color(-16720897),
                            new Color(-16720641),
                            new Color(-16719617),
                            new Color(-16719105),
                            new Color(-16718849),
                            new Color(-16718337),
                            new Color(-16717825),
                            new Color(-16717313),
                            new Color(-16717058),
                            new Color(-16717058),
                            new Color(-16717058),
                            new Color(-16717060),
                            new Color(-16717061),
                            new Color(-16717062),
                            new Color(-16717063),
                            new Color(-16717065),
                            new Color(-16717067),
                            new Color(-16717068),
                            new Color(-16717069),
                            new Color(-16717071),
                            new Color(-16717074),
                            new Color(-16717076),
                            new Color(-16717078),
                            new Color(-16717080),
                            new Color(-16717082),
                            new Color(-16717085),
                            new Color(-16717087),
                            new Color(-16717090),
                            new Color(-16717092),
                            new Color(-16717095),
                            new Color(-16717098),
                            new Color(-16717100),
                            new Color(-16717360),
                            new Color(-16717619),
                            new Color(-16717878),
                            new Color(-16718393),
                            new Color(-16718653),
                            new Color(-16718911),
                            new Color(-16719170),
                            new Color(-16719686),
                            new Color(-16719945),
                            new Color(-16720205),
                            new Color(-16720464),
                            new Color(-16720979),
                            new Color(-16721239),
                            new Color(-16721498),
                            new Color(-16721756),
                            new Color(-16722014),
                            new Color(-16722529),
                            new Color(-16722531),
                            new Color(-16722790),
                            new Color(-16723048),
                            new Color(-16723306),
                            new Color(-16723564),
                            new Color(-16723823),
                            new Color(-16724081),
                            new Color(-16724339),
                            new Color(-16724598),
                            new Color(-16724856),
                            new Color(-16725115),
                            new Color(-16725116),
                            new Color(-16725631),
                            new Color(-16725633),
                            new Color(-16726147),
                            new Color(-16726150),
                            new Color(-16726407),
                            new Color(-16726922),
                            new Color(-16726924),
                            new Color(-16727182),
                            new Color(-16727185),
                            new Color(-16727699),
                            new Color(-16727957),
                            new Color(-16727959),
                            new Color(-16728217),
                            new Color(-16728475),
                            new Color(-16728734),
                            new Color(-16728735),
                            new Color(-16728993),
                            new Color(-16728995),
                            new Color(-16729509),
                            new Color(-16729511),
                            new Color(-16729769),
                            new Color(-16729770),
                            new Color(-16729773),
                            new Color(-16730031),
                            new Color(-16730032),
                            new Color(-16730290),
                            new Color(-16730548),
                            new Color(-16730550),
                            new Color(-16730551),
                            new Color(-16730553),
                            new Color(-16730810),
                            new Color(-16730812),
                            new Color(-16731070),
                            new Color(-16731071),
                            new Color(-16731072),
                            new Color(-16731074),
                            new Color(-16731075),
                            new Color(-16731076),
                            new Color(-16731077),
                            new Color(-16731079),
                            new Color(-16600007),
                            new Color(-16468681),
                            new Color(-16468682),
                            new Color(-16337612),
                            new Color(-16272077),
                            new Color(-16140749),
                            new Color(-16009679),
                            new Color(-15944144),
                            new Color(-15812817),
                            new Color(-15681746),
                            new Color(-15615955),
                            new Color(-15484884),
                            new Color(-15353557),
                            new Color(-15222230),
                            new Color(-15091159),
                            new Color(-14894296),
                            new Color(-14828761),
                            new Color(-14631641),
                            new Color(-14500570),
                            new Color(-14368988),
                            new Color(-14172380),
                            new Color(-14041053),
                            new Color(-13909725),
                            new Color(-13712606),
                            new Color(-13515999),
                            new Color(-13384672),
                            new Color(-13187809),
                            new Color(-13056482),
                            new Color(-12859618),
                            new Color(-12662499),
                            new Color(-12531172),
                            new Color(-12334309),
                            new Color(-12137445),
                            new Color(-12006117),
                            new Color(-11743719),
                            new Color(-11612391),
                            new Color(-11415271),
                            new Color(-11218409),
                            new Color(-11087081),
                            new Color(-10824682),
                            new Color(-10627562),
                            new Color(-10430698),
                            new Color(-10233835),
                            new Color(-10036716),
                            new Color(-9905388),
                            new Color(-9642989),
                            new Color(-9511661),
                            new Color(-9249262),
                            new Color(-9052142),
                            new Color(-8855278),
                            new Color(-8658415),
                            new Color(-8461296),
                            new Color(-8329968),
                            new Color(-8133104),
                            new Color(-7870704),
                            new Color(-7739377),
                            new Color(-7476978),
                            new Color(-7280114),
                            new Color(-7148531),
                            new Color(-6886131),
                            new Color(-6689267),
                            new Color(-6557940),
                            new Color(-6295284),
                            new Color(-6098676),
                            new Color(-5901813),
                            new Color(-5770485),
                            new Color(-5508085),
                            new Color(-5376758),
                            new Color(-5114358),
                            new Color(-4983030),
                            new Color(-4786166),
                            new Color(-4589559),
                            new Color(-4392439),
                            new Color(-4261368),
                            new Color(-4130040),
                            new Color(-3867896),
                            new Color(-3736569),
                            new Color(-3539960),
                            new Color(-3343353),
                            new Color(-3212281),
                            new Color(-3081210),
                            new Color(-2884602),
                            new Color(-2753530),
                            new Color(-2556922),
                            new Color(-2360315),
                            new Color(-2294779),
                            new Color(-2098171),
                            new Color(-1967099),
                            new Color(-1836027),
                            new Color(-1639420),
                            new Color(-1508348),
                            new Color(-1377276),
                            new Color(-1246205),
                            new Color(-1180669),
                            new Color(-1049597),
                            new Color(-852990),
                            new Color(-852990),
                            new Color(-721918),
                            new Color(-590846),
                            new Color(-459775),
                            new Color(-328703),
                            new Color(-263167),
                            new Color(-132095),
                            new Color(-132096),
                            new Color(-66560),
                            new Color(-1280),
                            new Color(-1536),
                            new Color(-1792),
                            new Color(-2048),
                            new Color(-2304),
                            new Color(-2816),
                            new Color(-3072),
                            new Color(-3584),
                            new Color(-3840),
                            new Color(-4352),
                            new Color(-4864),
                            new Color(-5120),
                            new Color(-5632),
                            new Color(-6400),
                            new Color(-6912),
                            new Color(-7424),
                            new Color(-7936),
                            new Color(-8704),
                            new Color(-9216),
                            new Color(-9728),
                            new Color(-10240),
                            new Color(-11008),
                            new Color(-11520),
                            new Color(-12288),
                            new Color(-12800),
                            new Color(-13568),
                            new Color(-14336),
                            new Color(-14848),
                            new Color(-15872),
                            new Color(-16384),
                            new Color(-17152),
                            new Color(-17920),
                            new Color(-18944),
                            new Color(-19456),
                            new Color(-20480),
                            new Color(-20992),
                            new Color(-21760),
                            new Color(-22784),
                            new Color(-23552),
                            new Color(-24064),
                            new Color(-25088),
                            new Color(-25856),
                            new Color(-26880),
                            new Color(-27648),
                            new Color(-28416),
                            new Color(-29184),
                            new Color(-30208),
                            new Color(-30976),
                            new Color(-31744),
                            new Color(-32512),
                            new Color(-33536),
                            new Color(-34304),
                            new Color(-34816),
                            new Color(-35840),
                            new Color(-36608),
                            new Color(-37632),
                            new Color(-38400),
                            new Color(-39168),
                            new Color(-39936),
                            new Color(-40960),
                            new Color(-41728),
                            new Color(-42496),
                            new Color(-43520),
                            new Color(-44032),
                            new Color(-45056),
                            new Color(-45824),
                            new Color(-46592),
                            new Color(-47360),
                            new Color(-48128),
                            new Color(-48896),
                            new Color(-49664),
                            new Color(-50432),
                            new Color(-51200),
                            new Color(-51968),
                            new Color(-52736),
                            new Color(-53248),
                            new Color(-54016),
                            new Color(-54784),
                            new Color(-55296),
                            new Color(-56064),
                            new Color(-56832),
                            new Color(-57344),
                            new Color(-58112),
                            new Color(-58368),
                            new Color(-59136),
                            new Color(-59648),
                            new Color(-60160),
                            new Color(-60928),
                            new Color(-61440),
                            new Color(-61952),
                            new Color(-62208),
                            new Color(-62720),
                            new Color(-63488),
                            new Color(-63744),
                            new Color(-64000),
                            new Color(-64512),
                            new Color(-64768),
                            new Color(-65280),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65536),
                            new Color(-65279),
                            new Color(-65279),
                            new Color(-65279),
                            new Color(-65279),
                            new Color(-65279),
                            new Color(-65022),
                            new Color(-65022),
                            new Color(-65022),
                            new Color(-64765),
                            new Color(-64765),
                            new Color(-64508),
                            new Color(-64251),
                            new Color(-63994),
                            new Color(-63994),
                            new Color(-63480),
                            new Color(-63223),
                            new Color(-62966),
                            new Color(-62452),
                            new Color(-62195),
                            new Color(-61681),
                            new Color(-61167),
                            new Color(-60396),
                            new Color(-59882),
                            new Color(-59111),
                            new Color(-58340),
                            new Color(-57312),
                            new Color(-56284),
                            new Color(-54999),
                            new Color(-53971),
                            new Color(-52686),
                            new Color(-51401),
                            new Color(-49859),
                            new Color(-48317),
                            new Color(-46518),
                            new Color(-44719),
                            new Color(-43177),
                            new Color(-41121),
                            new Color(-39322),
                            new Color(-37009),
                            new Color(-34953),
                            new Color(-32640),
                            new Color(-30841),
                            new Color(-28528),
                            new Color(-26729),
                            new Color(-23902),
                            new Color(-21846),
                            new Color(-19533),
                            new Color(-17477),
                            new Color(-15164),
                            new Color(-13108),
                            new Color(-11052),
                            new Color(-8996),
                            new Color(-7197),
                            new Color(-5398),
                            new Color(-3856)};

    /**
     * get the color for a specific value
     *
     * @return color
     */
    public static Color getColor(int value) {
        return spectrum[value];
    }

    /**
     * change the color associated with the given value
     *
	 */
    public static void setColor(int value, Color color) {
        spectrum[value] = color;
    }

    /**
     * get number of colors. size()-1 is max value
     *
     * @return size
     */
    public static int size() {
        return spectrum.length;
    }
}
