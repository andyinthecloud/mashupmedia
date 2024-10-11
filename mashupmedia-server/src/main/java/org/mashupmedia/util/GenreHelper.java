package org.mashupmedia.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.music.Genre;

public class GenreHelper {
	// https://www.chosic.com/list-of-music-genres/#genre0

	public enum GenreType {
		WORLD_AND_TRADITIONAL_FOLK(new String[] {
				"world and traditional_folk",
				"folkmusik",
				"world",
				"afropop",
				"arab folk",
				"canzone genovese",
				"celtic",
				"corsican",
				"desert",
				"finnish",
				"greek",
				"griot",
				"irish",
				"kundiman",
				"malian",
				"mande",
				"min'yo",
				"native american",
				"neo kyma",
				"nordic",
				"norwegian",
				"rabindra sangeet",
				"russian",
				"scottish",
				"shamanic",
				"sufi chant",
				"swedish",
				"tatar",
				"yoik"
		}),
		LATIN(new String[] {
				"latin",
				"bachata",
				"bolero",
				"bossa nova",
				"chicha",
				"colombian",
				"cumbia",
				"dominican",
				"flamenco",
				"huayno",
				"latin",
				"mariachi",
				"merengue",
				"mexican",
				"neotango",
				"perreo",
				"reggaeton",
				"puerto rican",
				"ranchera",
				"espanol",
				"salsa",
				"spanish",
				"tango",
				"tejano",
				"timba",
				"latino",
				"tropical",
				"twoubadou",
				"zouk"
		}),
		HIP_HOP_AND_RAP(new String[] {
				"hip hop and rap",
				"hip hop",
				"rap",
				"bounce",
				"crunk",
				"g funk",
				"hyphy",
				"nerdcore",
				"trap"
		}),
		R_AND_B(new String[] {
				"r&b",
				"r and b",
				"disco",
				"funk",
				"motown",
				"neo",
				"quiet storm",
				"soul",
				"urban contemporary"
		}),
		METAL(new String[] {
				"metal",
				"hard rock",
				"nwothm",
				"death",
				"thrash"
		}),

		COUNTRY(new String[] {
				"country",
				"bakersfield sound",
				"bluegrass",
				"cajun",
				"cowboy western",
				"cowpunk",
				"dansband",
				"honky tonk",
				"kentucky",
				"nashville",
				"new americana",
				"red dirt",
				"sertanejo",
				"western swing",
				"wyoming roots"
		}),
		FOLK_AND_ACOUSTIC(new String[] {
				"folk",
				"acoustic",
				"anti-folk",
				"ectofolk",
				"lilith",
				"melancholia",
				"mellow gold",
				"new americana",
				"singer songwriter",
				"stomp and holler"
		}),
		CLASSICAL(new String[] {
				"classical",
				"avant-garde",
				"baroque",
				"chamber",
				"choral",
				"compositional ambient",
				"early music",
				"early romantic era",
				"historically informed performance",
				"impressionism",
				"italian baroque",
				"late romantic era",
				"medieval",
				"minimalism",
				"neoclassicism",
				"opera",
				"orchestra",
				"polyphony",
				"post-romantic era",
				"renaissance",
				"serialism",
				"string quartet",
				"violin"
		}),
		JAZZ(new String[] {
				"jazz",
				"bebop",
				"bossa nova",
				"contemporary post-bop",
				"dixieland",
				"free improvisation",
				"hard bop",
				"harlem renaissance",
				"ragtime",
				"stride"
		}),
		BLUES(new String[] {
				"blues"
		}),
		EASY_LISTENING(new String[] {
				"easy listening",
				"adult standards",
				"ballroom",
				"big band",
				"brill building pop",
				"soundtrack",
				"deep adult standards",
				"exotica",
				"hollywood",
				"light music",
				"lounge",
				"movie tunes",
				"romantico",
				"space age pop",
				"swing",
				"torch song"
		}),
		NEW_AGE(new String[] {
				"new age",
				"ambient",
				"background",
				"bow pop",
				"calming instrumental",
				"classify",
				"fourth world",
				"healing",
				"meditation",
				"neo-classical",
				"operatic pop",
				"relaxative",
				"sleep",
				"meditation"
		}),
		ROCK(new String[] {
				"rock",
				"british invasion",
				"britpop",
				"dance-punk",
				"grunge",
				"mellow gold",
				"new romantic",
				"new wave",
				"permanent wave",
				"post-grunge"
		}),
		POP(new String[] {
				"pop",
				"classify",
				"escape room",
				"levenslied",
				"metropopolis",
				"neo mellow",
				"new romantic",
				"soft rock",
				"talent show",
				"vapor soul" }),
		DANCE(new String[] {
				"edm",
				"bass house",
				"bass trap",
				"big room",
				"breakbeat",
				"breakcore",
				"brostep",
				"chillstep",
				"complextro",
				"dance",
				"deep",
				"disco",
				"dubstep",
				"electro",
				"electronic",
				"electropop",
				"filthstep",
				"future bass",
				"future",
				"garage",
				"gaming",
				"glitch hop",
				"house",
				"trance",
				"sky room",
				"tech"
		}),

		OTHER(new String[] {});

		GenreType(String[] genres) {
			this.genres = genres;
		}

		private String[] genres;

		boolean isGenre(String value) {

			if (genres == null || genres.length == 0) {
				return false;
			}

			String preparedValue = StringUtils.trimToEmpty(value).toLowerCase()
					.replaceAll("_", " ");
			if (StringUtils.isEmpty(preparedValue)) {
				return false;
			}

			return Arrays.stream(genres)
					.anyMatch(g -> preparedValue.contains(g));
		}

	}

	public static Genre getGenre(String genreValue) {

		GenreType genreType = Arrays.stream(GenreType.values())
				.filter(gt -> gt.isGenre(genreValue))
				.findAny()
				.orElse(GenreType.OTHER);

		return Genre.builder()
				.idName(genreType.name())
				.build();

	}
}
