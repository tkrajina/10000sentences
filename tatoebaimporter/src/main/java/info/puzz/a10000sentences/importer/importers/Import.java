package info.puzz.a10000sentences.importer.importers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ThreadUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.language.Languages;

public class Import {

    public static final String OUTPUT_DIR = "../bucket_files";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        List<Importer> importers = new ArrayList<>();

        String[][] tatoebaLanguagePairs = new String[][]{
                new String[]{"pes", "eng"},
                new String[]{"nob", "eng"},
                new String[]{"ces", "eng"},
                new String[]{"mkd", "eng"},
                new String[]{"ces", "eng"},
                new String[]{"bul", "eng"},
                new String[]{"srp", "eng"},
                new String[]{"dan", "eng"},
                new String[]{"swe", "eng"},
                new String[]{"ukr", "eng"},
                new String[]{"nld", "eng"},
                new String[]{"fin", "eng"},
                new String[]{"mkd", "eng"},
                new String[]{"hun", "eng"},
                new String[]{"pol", "eng"},
                new String[]{"ita", "eng"},
                new String[]{"epo", "eng"},
                new String[]{"lat", "eng"},
                new String[]{"tur", "eng"},
                new String[]{"ell", "eng"},
                new String[]{"ron", "eng"},
                new String[]{"ara", "eng"},
                new String[]{"heb", "eng"},
                new String[]{"deu", "eng"},
                new String[]{"fra", "eng"},
                new String[]{"rus", "eng"},
                new String[]{"por", "eng"},
                new String[]{"spa", "eng"},
                new String[]{"lit", "eng"},

                // Nonenglish collections:
                new String[]{"spa", "fra"},
                new String[]{"deu", "ita"},
        };
        for (String[] tatoebaLanguagePair : tatoebaLanguagePairs) {
            importers.add(new TatoebaImporter(tatoebaLanguagePair[0], tatoebaLanguagePair[1], tatoebaLanguagePairs));
            importers.add(new TatoebaImporter(tatoebaLanguagePair[1], tatoebaLanguagePair[0], tatoebaLanguagePairs));
        }

        String[][] tmxImporters = new String[][]{
                {"slv", "eng", "en-sl.tmx"},
                {"est", "eng", "en-et.tmx"},
                {"lav", "eng", "en-lv.tmx"},
                {"slk", "eng", "en-sk.tmx"},
                {"hrv", "eng", "en-hr.tmx"},
                {"sqi", "eng", "en-sq.tmx"},
        };
        for (String[] eu : tmxImporters) {
            String lang1 = eu[0];
            String lang2 = eu[1];
            String baseFilename = eu[2];
            importers.add(new TMXImporter(lang1, lang2, baseFilename));
            importers.add(new TMXImporter(lang2, lang1, baseFilename));
        }

        // Slovene:

        // Estonian:


        InfoVO info = new InfoVO()
                .setLanguages(Languages.getLanguages());

        Set<String> languages = new HashSet<>();

        for (int i = 0; i < importers.size(); i++) {
            Importer importer = importers.get(i);
            String outFilename = String.format("%s-%s.csv", importer.knownLanguageAbbrev3, importer.targetLanguageAbbrev3);
            System.out.println(String.format("Language %d/%d: %s", i + 1, importers.size(), outFilename));

            languages.add(importer.targetLang.getName());
            languages.add(importer.knownLang.getName());

            SentenceWriter writer = new SentenceWriter(Paths.get(OUTPUT_DIR, outFilename).toString());
            importer.importCollection(writer);
            writer.close();

            SentenceCollectionVO collection = new SentenceCollectionVO()
                    .setKnownLanguage(importer.knownLang.getAbbrev())
                    .setTargetLanguage(importer.targetLang.getAbbrev())
                    .setCount(writer.counter)
                    .setType(importer.getType())
                    .setFilename(new File(writer.filename).getName());
            info.addSentencesCollection(collection);

            System.out.println("cooling cpu :)");
            Thread.sleep(10_000);
        }

        Object[] langArr = languages.toArray();
        Arrays.sort(langArr);
        System.out.printf("%d languages:\n", languages.size());
        for (Object lang : langArr) {
            System.out.printf("- %s\n", lang);
        }

        String infoFilename = Paths.get(OUTPUT_DIR, "info.json").toString();
        FileUtils.writeByteArrayToFile(new File(infoFilename), OBJECT_MAPPER.writeValueAsBytes(info));
    }
}
