package info.puzz.a10000sentences.language;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import info.puzz.a10000sentences.apimodels.LanguageVO;

/**
 * Created by puzz on 30/11/2016.
 */

public class Languages {

    private static final String LANGUAGES_STR = "Northwest Caucasian\tAbkhaz\tаҧсуа бызшәа, аҧсшәа\tab\tabk\tabk\tabk\tabks\t\n"+
            "Afro-Asiatic\tAfar\tAfaraf\taa\taar\taar\taar\taars\t\n"+
            "Indo-European\tAfrikaans\tAfrikaans\taf\tafr\tafr\tafr\tafrs\t\n"+
            "Niger–Congo\tAkan\tAkan\tak\taka\taka\taka + 2\t\tmacrolanguage, Twi is [tw/twi], Fanti is [fat]\n"+
            "Indo-European\tAlbanian\tShqip\tsq\tsqi\talb\tsqi + 4\t\tmacrolanguage, \"Albanian Phylozone\" in 639-6\n"+
            "Afro-Asiatic\tAmharic\tአማርኛ\tam\tamh\tamh\tamh\t\t\n"+
            "Afro-Asiatic\tArabic\tالعربية\tar\tara\tara\tara + 30\t\tmacrolanguage, Standard Arabic is [arb]\n"+
            "Indo-European\tAragonese\taragonés\tan\targ\targ\targ\t\t\n"+
            "Indo-European\tArmenian\tՀայերեն\thy\thye\tarm\thye\t\t\n"+
            "Indo-European\tAssamese\tঅসমীয়া\tas\tasm\tasm\tasm\t\t\n"+
            "Northeast Caucasian\tAvaric\tавар мацӀ, магӀарул мацӀ\tav\tava\tava\tava\t\t\n"+
            "Indo-European\tAvestan\tavesta\tae\tave\tave\tave\t\tancient\n"+
            "Aymaran\tAymara\taymar aru\tay\taym\taym\taym + 2\t\tmacrolanguage\n"+
            "Turkic\tAzerbaijani\tazərbaycan dili\taz\taze\taze\taze + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tBambara\tbamanankan\tbm\tbam\tbam\tbam\t\t\n"+
            "Turkic\tBashkir\tбашҡорт теле\tba\tbak\tbak\tbak\t\t\n"+
            "Language isolate\tBasque\teuskara, euskera\teu\teus\tbaq\teus\t\t\n"+
            "Indo-European\tBelarusian\tбеларуская мова\tbe\tbel\tbel\tbel\t\t\n"+
            "Indo-European\tBengali, Bangla\tবাংলা\tbn\tben\tben\tben\t\t\n"+
            "Indo-European\tBihari\tभोजपुरी\tbh\tbih\tbih\t\t\tCollective language code for Bhojpuri, Magahi, and Maithili\n"+
            "Creole\tBislama\tBislama\tbi\tbis\tbis\tbis\t\t\n"+
            "Indo-European\tBosnian\tbosanski jezik\tbs\tbos\tbos\tbos\tboss\t\n"+
            "Indo-European\tBreton\tbrezhoneg\tbr\tbre\tbre\tbre\t\t\n"+
            "Indo-European\tBulgarian\tбългарски език\tbg\tbul\tbul\tbul\tbuls\t\n"+
            "Sino-Tibetan\tBurmese\tဗမာစာ\tmy\tmya\tbur\tmya\t\t\n"+
            "Indo-European\tCatalan\tcatalà\tca\tcat\tcat\tcat\t\t\n"+
            "Austronesian\tChamorro\tChamoru\tch\tcha\tcha\tcha\t\t\n"+
            "Northeast Caucasian\tChechen\tнохчийн мотт\tce\tche\tche\tche\t\t\n"+
            "Niger–Congo\tChichewa, Chewa, Nyanja\tchiCheŵa, chinyanja\tny\tnya\tnya\tnya\t\t\n"+
            "Sino-Tibetan\tChinese\t中文 (Zhōngwén), 汉语, 漢語\tzh\tzho\tchi\tzho + 13\t\tmacrolanguage\n"+
            "Turkic\tChuvash\tчӑваш чӗлхи\tcv\tchv\tchv\tchv\t\t\n"+
            "Indo-European\tCornish\tKernewek\tkw\tcor\tcor\tcor\t\t\n"+
            "Indo-European\tCorsican\tcorsu, lingua corsa\tco\tcos\tcos\tcos\t\t\n"+
            "Algonquian\tCree\tᓀᐦᐃᔭᐍᐏᐣ\tcr\tcre\tcre\tcre + 6\t\tmacrolanguage\n"+
            "Indo-European\tCroatian\thrvatski jezik\thr\thrv\thrv\thrv\t\t\n"+
            "Indo-European\tCzech\tčeština, český jazyk\tcs\tces\tcze\tces\t\t\n"+
            "Indo-European\tDanish\tdansk\tda\tdan\tdan\tdan\t\t\n"+
            "Indo-European\tDivehi, Dhivehi, Maldivian\tދިވެހި\tdv\tdiv\tdiv\tdiv\t\t\n"+
            "Indo-European\tDutch\tNederlands, Vlaams\tnl\tnld\tdut\tnld\t\t\n"+
            "Sino-Tibetan\tDzongkha\tརྫོང་ཁ\tdz\tdzo\tdzo\tdzo\t\t\n"+
            "Indo-European\tEnglish\tEnglish\ten\teng\teng\teng\tengs\t\n"+
            "Constructed\tEsperanto\tEsperanto\teo\tepo\tepo\tepo\t\tconstructed, initiated from L.L. Zamenhof, 1887\n"+
            "Uralic\tEstonian\teesti, eesti keel\tet\test\test\test + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tEwe\tEʋegbe\tee\tewe\tewe\tewe\t\t\n"+
            "Indo-European\tFaroese\tføroyskt\tfo\tfao\tfao\tfao\t\t\n"+
            "Austronesian\tFijian\tvosa Vakaviti\tfj\tfij\tfij\tfij\t\t\n"+
            "Uralic\tFinnish\tsuomi, suomen kieli\tfi\tfin\tfin\tfin\t\t\n"+
            "Indo-European\tFrench\tfrançais, langue française\tfr\tfra\tfre\tfra\tfras\t\n"+
            "Niger–Congo\tFula, Fulah, Pulaar, Pular\tFulfulde, Pulaar, Pular\tff\tful\tful\tful + 9\t\tmacrolanguage\n"+
            "Indo-European\tGalician\tgalego\tgl\tglg\tglg\tglg\t\t\n"+
            "South Caucasian\tGeorgian\tქართული\tka\tkat\tgeo\tkat\t\t\n"+
            "Indo-European\tGerman\tDeutsch\tde\tdeu\tger\tdeu\tdeus\t\n"+
            "Indo-European\tGreek (modern)\tελληνικά\tel\tell\tgre\tell\tells\t\n"+
            "Tupian\tGuaraní\tAvañe'ẽ\tgn\tgrn\tgrn\tgrn + 5\t\tmacrolanguage\n"+
            "Indo-European\tGujarati\tગુજરાતી\tgu\tguj\tguj\tguj\t\t\n"+
            "Creole\tHaitian, Haitian Creole\tKreyòl ayisyen\tht\that\that\that\t\t\n"+
            "Afro-Asiatic\tHausa\t(Hausa) هَوُسَ\tha\thau\thau\thau\t\t\n"+
            "Afro-Asiatic\tHebrew (modern)\tעברית\the\theb\theb\theb\t\t\n"+
            "Niger–Congo\tHerero\tOtjiherero\thz\ther\ther\ther\t\t\n"+
            "Indo-European\tHindi\tहिन्दी, हिंदी\thi\thin\thin\thin\thins\t\n"+
            "Austronesian\tHiri Motu\tHiri Motu\tho\thmo\thmo\thmo\t\t\n"+
            "Uralic\tHungarian\tmagyar\thu\thun\thun\thun\t\t\n"+
            "Constructed\tInterlingua\tInterlingua\tia\tina\tina\tina\t\tconstructed by International Auxiliary Language Association\n"+
            "Austronesian\tIndonesian\tBahasa Indonesia\tid\tind\tind\tind\t\tCovered by macrolanguage [ms/msa]\n"+
            "Constructed\tInterlingue\tOriginally called Occidental; then Interlingue after WWII\tie\tile\tile\tile\t\tconstructed by Edgar de Wahl, first published in 1922\n"+
            "Indo-European\tIrish\tGaeilge\tga\tgle\tgle\tgle\t\t\n"+
            "Niger–Congo\tIgbo\tAsụsụ Igbo\tig\tibo\tibo\tibo\t\t\n"+
            "Eskimo–Aleut\tInupiaq\tIñupiaq, Iñupiatun\tik\tipk\tipk\tipk + 2\t\tmacrolanguage\n"+
            "Constructed\tIdo\tIdo\tio\tido\tido\tido\tidos\tconstructed by De Beaufront, 1907, as variation of Esperanto\n"+
            "Indo-European\tIcelandic\tÍslenska\tis\tisl\tice\tisl\t\t\n"+
            "Indo-European\tItalian\titaliano\tit\tita\tita\tita\titas\t\n"+
            "Eskimo–Aleut\tInuktitut\tᐃᓄᒃᑎᑐᑦ\tiu\tiku\tiku\tiku + 2\t\tmacrolanguage\n"+
            "Japonic\tJapanese\t日本語 (にほんご)\tja\tjpn\tjpn\tjpn\t\t\n"+
            "Austronesian\tJavanese\tbasa Jawa\tjv\tjav\tjav\tjav\t\t\n"+
            "Eskimo–Aleut\tKalaallisut, Greenlandic\tkalaallisut, kalaallit oqaasii\tkl\tkal\tkal\tkal\t\t\n"+
            "Dravidian\tKannada\tಕನ್ನಡ\tkn\tkan\tkan\tkan\t\t\n"+
            "Nilo-Saharan\tKanuri\tKanuri\tkr\tkau\tkau\tkau + 3\t\tmacrolanguage\n"+
            "Indo-European\tKashmiri\tकश्मीरी, كشميري\u200E\tks\tkas\tkas\tkas\t\t\n"+
            "Turkic\tKazakh\tқазақ тілі\tkk\tkaz\tkaz\tkaz\t\t\n"+
            "Austroasiatic\tKhmer\tខ្មែរ, ខេមរភាសា, ភាសាខ្មែរ\tkm\tkhm\tkhm\tkhm\t\ta.k.a. Cambodian\n"+
            "Niger–Congo\tKikuyu, Gikuyu\tGĩkũyũ\tki\tkik\tkik\tkik\t\t\n"+
            "Niger–Congo\tKinyarwanda\tIkinyarwanda\trw\tkin\tkin\tkin\t\t\n"+
            "Turkic\tKyrgyz\tКыргызча, Кыргыз тили\tky\tkir\tkir\tkir\t\t\n"+
            "Uralic\tKomi\tкоми кыв\tkv\tkom\tkom\tkom + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tKongo\tKikongo\tkg\tkon\tkon\tkon + 3\t\tmacrolanguage\n"+
            "Koreanic\tKorean\t한국어, 조선어\tko\tkor\tkor\tkor\t\t\n"+
            "Indo-European\tKurdish\tKurdî, كوردی\u200E\tku\tkur\tkur\tkur + 3\t\tmacrolanguage\n"+
            "Niger–Congo\tKwanyama, Kuanyama\tKuanyama\tkj\tkua\tkua\tkua\t\t\n"+
            "Indo-European\tLatin\tlatine, lingua latina\tla\tlat\tlat\tlat\tlats\tancient\n"+
            "Indo-European\tLuxembourgish, Letzeburgesch\tLëtzebuergesch\tlb\tltz\tltz\tltz\t\t\n"+
            "Niger–Congo\tGanda\tLuganda\tlg\tlug\tlug\tlug\t\t\n"+
            "Indo-European\tLimburgish, Limburgan, Limburger\tLimburgs\tli\tlim\tlim\tlim\t\t\n"+
            "Niger–Congo\tLingala\tLingála\tln\tlin\tlin\tlin\t\t\n"+
            "Tai–Kadai\tLao\tພາສາລາວ\tlo\tlao\tlao\tlao\t\t\n"+
            "Indo-European\tLithuanian\tlietuvių kalba\tlt\tlit\tlit\tlit\t\t\n"+
            "Niger–Congo\tLuba-Katanga\tTshiluba\tlu\tlub\tlub\tlub\t\t\n"+
            "Indo-European\tLatvian\tlatviešu valoda\tlv\tlav\tlav\tlav + 2\t\tmacrolanguage\n"+
            "Indo-European\tManx\tGaelg, Gailck\tgv\tglv\tglv\tglv\t\t\n"+
            "Indo-European\tMacedonian\tмакедонски јазик\tmk\tmkd\tmac\tmkd\t\t\n"+
            "Austronesian\tMalagasy\tfiteny malagasy\tmg\tmlg\tmlg\tmlg + 10\t\tmacrolanguage\n"+
            "Austronesian\tMalay\tbahasa Melayu, بهاس ملايو\u200E\tms\tmsa\tmay\tmsa + 13\t\tmacrolanguage, Standard Malay is [zsm], Indonesian is [id/ind]\n"+
            "Dravidian\tMalayalam\tമലയാളം\tml\tmal\tmal\tmal\t\t\n"+
            "Afro-Asiatic\tMaltese\tMalti\tmt\tmlt\tmlt\tmlt\t\t\n"+
            "Austronesian\tMāori\tte reo Māori\tmi\tmri\tmao\tmri\t\t\n"+
            "Indo-European\tMarathi (Marāṭhī)\tमराठी\tmr\tmar\tmar\tmar\t\t\n"+
            "Austronesian\tMarshallese\tKajin M̧ajeļ\tmh\tmah\tmah\tmah\t\t\n"+
            "Mongolic\tMongolian\tМонгол хэл\tmn\tmon\tmon\tmon + 2\t\tmacrolanguage\n"+
            "Austronesian\tNauruan\tDorerin Naoero\tna\tnau\tnau\tnau\t\t\n"+
            "Dené–Yeniseian\tNavajo, Navaho\tDiné bizaad\tnv\tnav\tnav\tnav\t\t\n"+
            "Niger–Congo\tNorthern Ndebele\tisiNdebele\tnd\tnde\tnde\tnde\t\t\n"+
            "Indo-European\tNepali\tनेपाली\tne\tnep\tnep\tnep\t\t\n"+
            "Niger–Congo\tNdonga\tOwambo\tng\tndo\tndo\tndo\t\t\n"+
            "Indo-European\tNorwegian Bokmål\tNorsk bokmål\tnb\tnob\tnob\tnob\t\tCovered by macrolanguage [no/nor]\n"+
            "Indo-European\tNorwegian Nynorsk\tNorsk nynorsk\tnn\tnno\tnno\tnno\t\tCovered by macrolanguage [no/nor]\n"+
            "Indo-European\tNorwegian\tNorsk\tno\tnor\tnor\tnor + 2\t\tmacrolanguage, Bokmål is [nb/nob], Nynorsk is [nn/nno]\n"+
            "Sino-Tibetan\tNuosu\tꆈꌠ꒿ Nuosuhxop\tii\tiii\tiii\tiii\t\tStandard form of Yi languages\n"+
            "Niger–Congo\tSouthern Ndebele\tisiNdebele\tnr\tnbl\tnbl\tnbl\t\t\n"+
            "Indo-European\tOccitan\toccitan, lenga d'òc\toc\toci\toci\toci\t\t\n"+
            "Algonquian\tOjibwe, Ojibwa\tᐊᓂᔑᓈᐯᒧᐎᓐ\toj\toji\toji\toji + 7\t\tmacrolanguage\n"+
            "Indo-European\tOld Church Slavonic, Church Slavonic, Old Bulgarian\tѩзыкъ словѣньскъ\tcu\tchu\tchu\tchu\t\tancient, in use by Orthodox Church\n"+
            "Afro-Asiatic\tOromo\tAfaan Oromoo\tom\torm\torm\torm + 4\t\tmacrolanguage\n"+
            "Indo-European\tOriya\tଓଡ଼ିଆ\tor\tori\tori\tori\t\t\n"+
            "Indo-European\tOssetian, Ossetic\tирон æвзаг\tos\toss\toss\toss\t\t\n"+
            "Indo-European\tPanjabi, Punjabi\tਪੰਜਾਬੀ, پنجابی\u200E\tpa\tpan\tpan\tpan\t\t\n"+
            "Indo-European\tPāli\tपाऴि\tpi\tpli\tpli\tpli\t\tancient\n"+
            "Indo-European\tPersian (Farsi)\tفارسی\tfa\tfas\tper\tfas + 2\t\tmacrolanguage\n"+
            "Indo-European\tPolish\tjęzyk polski, polszczyzna\tpl\tpol\tpol\tpol\tpols\t\n"+
            "Indo-European\tPashto, Pushto\tپښتو\tps\tpus\tpus\tpus + 3\t\tmacrolanguage\n"+
            "Indo-European\tPortuguese\tportuguês\tpt\tpor\tpor\tpor\t\t\n"+
            "Quechuan\tQuechua\tRuna Simi, Kichwa\tqu\tque\tque\tque + 44\t\tmacrolanguage\n"+
            "Indo-European\tRomansh\trumantsch grischun\trm\troh\troh\troh\t\t\n"+
            "Niger–Congo\tKirundi\tIkirundi\trn\trun\trun\trun\t\t\n"+
            "Indo-European\tRomanian\tlimba română\tro\tron\trum\tron\t\t[mo] for Moldavian has been withdrawn, recommending [ro] also for Moldavian\n"+
            "Indo-European\tRussian\tРусский\tru\trus\trus\trus\t\t\n"+
            "Indo-European\tSanskrit (Saṁskṛta)\tसंस्कृतम्\tsa\tsan\tsan\tsan\t\tancient, still spoken\n"+
            "Indo-European\tSardinian\tsardu\tsc\tsrd\tsrd\tsrd + 4\t\tmacrolanguage\n"+
            "Indo-European\tSindhi\tसिन्धी, سنڌي، سندھی\u200E\tsd\tsnd\tsnd\tsnd\t\t\n"+
            "Uralic\tNorthern Sami\tDavvisámegiella\tse\tsme\tsme\tsme\t\t\n"+
            "Austronesian\tSamoan\tgagana fa'a Samoa\tsm\tsmo\tsmo\tsmo\t\t\n"+
            "Creole\tSango\tyângâ tî sängö\tsg\tsag\tsag\tsag\t\t\n"+
            "Indo-European\tSerbian\tсрпски језик\tsr\tsrp\tsrp\tsrp\t\tThe ISO 639-2/T code srp deprecated the ISO 639-2/B code scc[1]\n"+
            "Indo-European\tScottish Gaelic, Gaelic\tGàidhlig\tgd\tgla\tgla\tgla\t\t\n"+
            "Niger–Congo\tShona\tchiShona\tsn\tsna\tsna\tsna\t\t\n"+
            "Indo-European\tSinhala, Sinhalese\tසිංහල\tsi\tsin\tsin\tsin\t\t\n"+
            "Indo-European\tSlovak\tslovenčina, slovenský jazyk\tsk\tslk\tslo\tslk\t\t\n"+
            "Indo-European\tSlovene\tslovenski jezik, slovenščina\tsl\tslv\tslv\tslv\t\t\n"+
            "Afro-Asiatic\tSomali\tSoomaaliga, af Soomaali\tso\tsom\tsom\tsom\t\t\n"+
            "Niger–Congo\tSouthern Sotho\tSesotho\tst\tsot\tsot\tsot\t\t\n"+
            "Indo-European\tSpanish\tespañol\tes\tspa\tspa\tspa\t\t\n"+
            "Austronesian\tSundanese\tBasa Sunda\tsu\tsun\tsun\tsun\t\t\n"+
            "Niger–Congo\tSwahili\tKiswahili\tsw\tswa\tswa\tswa + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tSwati\tSiSwati\tss\tssw\tssw\tssw\t\t\n"+
            "Indo-European\tSwedish\tsvenska\tsv\tswe\tswe\tswe\t\t\n"+
            "Dravidian\tTamil\tதமிழ்\tta\ttam\ttam\ttam\t\t\n"+
            "Dravidian\tTelugu\tతెలుగు\tte\ttel\ttel\ttel\t\t\n"+
            "Indo-European\tTajik\tтоҷикӣ, toçikī, تاجیکی\u200E\ttg\ttgk\ttgk\ttgk\t\t\n"+
            "Tai–Kadai\tThai\tไทย\tth\ttha\ttha\ttha\t\t\n"+
            "Afro-Asiatic\tTigrinya\tትግርኛ\tti\ttir\ttir\ttir\t\t\n"+
            "Sino-Tibetan\tTibetan Standard, Tibetan, Central\tབོད་ཡིག\tbo\tbod\ttib\tbod\t\t\n"+
            "Turkic\tTurkmen\tTürkmen, Түркмен\ttk\ttuk\ttuk\ttuk\t\t\n"+
            "Austronesian\tTagalog\tWikang Tagalog, ᜏᜒᜃᜅ᜔ ᜆᜄᜎᜓᜄ᜔\ttl\ttgl\ttgl\ttgl\t\tNote: Filipino (Pilipino) has the code [fil]\n"+
            "Niger–Congo\tTswana\tSetswana\ttn\ttsn\ttsn\ttsn\t\t\n"+
            "Austronesian\tTonga (Tonga Islands)\tfaka Tonga\tto\tton\tton\tton\t\t\n"+
            "Turkic\tTurkish\tTürkçe\ttr\ttur\ttur\ttur\t\t\n"+
            "Niger–Congo\tTsonga\tXitsonga\tts\ttso\ttso\ttso\t\t\n"+
            "Turkic\tTatar\tтатар теле, tatar tele\ttt\ttat\ttat\ttat\t\t\n"+
            "Niger–Congo\tTwi\tTwi\ttw\ttwi\ttwi\ttwi\t\tCovered by macrolanguage [ak/aka]\n"+
            "Austronesian\tTahitian\tReo Tahiti\tty\ttah\ttah\ttah\t\tOne of the Reo Mā` + \"`\" + `ohi (languages of French Polynesia)\n"+
            "Turkic\tUyghur\tئۇيغۇرچە\u200E, Uyghurche\tug\tuig\tuig\tuig\t\t\n"+
            "Indo-European\tUkrainian\tУкраїнська\tuk\tukr\tukr\tukr\t\t\n"+
            "Indo-European\tUrdu\tاردو\tur\turd\turd\turd\t\t\n"+
            "Turkic\tUzbek\tOʻzbek, Ўзбек, أۇزبېك\u200E\tuz\tuzb\tuzb\tuzb + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tVenda\tTshivenḓa\tve\tven\tven\tven\t\t\n"+
            "Austroasiatic\tVietnamese\tTiếng Việt\tvi\tvie\tvie\tvie\t\t\n"+
            "Constructed\tVolapük\tVolapük\tvo\tvol\tvol\tvol\t\tconstructed\n"+
            "Indo-European\tWalloon\twalon\twa\twln\twln\twln\t\t\n"+
            "Indo-European\tWelsh\tCymraeg\tcy\tcym\twel\tcym\t\t\n"+
            "Niger–Congo\tWolof\tWollof\two\twol\twol\twol\t\t\n"+
            "Indo-European\tWestern Frisian\tFrysk\tfy\tfry\tfry\tfry\t\t\n"+
            "Niger–Congo\tXhosa\tisiXhosa\txh\txho\txho\txho\t\t\n"+
            "Indo-European\tYiddish\tייִדיש\tyi\tyid\tyid\tyid + 2\t\tmacrolanguage\n"+
            "Niger–Congo\tYoruba\tYorùbá\tyo\tyor\tyor\tyor\t\t\n"+
            "Tai–Kadai\tZhuang, Chuang\tSaɯ cueŋƅ, Saw cuengh\tza\tzha\tzha\tzha + 16\t\tmacrolanguage\n"+
            "Niger–Congo\tZulu\tisiZulu\tzu\tzul\tzul\tzul\t\t";

    private static Map<String, LanguageVO> sentencesByAbbrevs;

    private Languages() throws Exception {
        throw new Exception();
    }

    public static List<LanguageVO> getLanguages() {
        if (sentencesByAbbrevs == null) {
            sentencesByAbbrevs = new ConcurrentHashMap<>();
        }
        List<LanguageVO> languages = new ArrayList<>();
        for (String line : LANGUAGES_STR.split("\n")) {
            String[] parts = line.split("\t");

            String abbrev = parts[3].trim();
            LanguageVO lang = new LanguageVO()
                    .setAbbrev(abbrev)
                    .setAbbrev3(parts[4].trim())
                    .setFamily(parts[0].trim())
                    .setName(parts[1].trim())
                    .setNativeName(parts[2].trim())
                    .setRightToLeft(StringUtils.equals(abbrev, "ar") || StringUtils.equals(abbrev, "he"));
            languages.add(lang);
            sentencesByAbbrevs.put(lang.getAbbrev(), lang);
            sentencesByAbbrevs.put(lang.getAbbrev3(), lang);
        }
        return languages;
    }

    public static LanguageVO getLanguageByAbbrev(String abbrev) {
        if (sentencesByAbbrevs == null) {
            getLanguages();
        }
        return sentencesByAbbrevs.get(abbrev);
    }

    public static void main(String[] args) {
        for (LanguageVO language : getLanguages()) {
            System.out.println(language);
        }
    }

}
