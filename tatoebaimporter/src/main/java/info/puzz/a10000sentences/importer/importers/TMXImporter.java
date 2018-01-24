package info.puzz.a10000sentences.importer.importers;

import org.w3c.dom.Document;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import info.puzz.a10000sentences.apimodels.CollectionType;

public class TMXImporter extends Importer {
    private final String baseFilename;

    public TMXImporter(String knownLanguageAbbrev3, String targetLanguageAbbrev3, String baseFilename) {
        super(knownLanguageAbbrev3, targetLanguageAbbrev3);
        this.baseFilename = baseFilename;
    }

    @Override
    public CollectionType getType() {
        return CollectionType.OPUS_OPENSUBTITLES;
    }

    @Override
    public void importCollection(SentenceWriter writer) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new FileInputStream(String.format("%s/%s", RAW_FILES_PATH, baseFilename)));


/*        WordCounter counter = new WordCounter();

        List<SentenceVO> sentences = new ArrayList<>();
        Set<Integer> knownSenteceHashes = new HashSet<>();

        int ignoredSentences = 0;
        String targetLine, knownLine;
        while (true) {
            targetLine = targetFile.readLine();
            knownLine = knownFile.readLine();
            if (targetLine == null && knownLine == null) {
                break;
            }
            if (StringUtils.isNotEmpty(targetLine) && StringUtils.isNotEmpty(knownLine)) {
                if (numberPattern.matcher(targetLine).matches()) {
                    //
                } else if (Character.isUpperCase(targetLine.charAt(0)) && Character.isUpperCase(knownLine.charAt(0))) {
                    if (targetLine.indexOf(":") >= 0 || targetLine.indexOf("(") >= 0) {

                    } else {
                        for (SentenceVO s : importSentence(targetLine, knownLine)) {
                            Integer h = s.getKnownSentence().hashCode();
                            if (!knownSenteceHashes.contains(h) && sentenceOK(s)) {
                                sentences.add(s);
                                counter.countWordsInSentence(s, knownLang, targetLang);
                                knownSenteceHashes.add(h);
                            } else {
                                ignoredSentences += 1;
                            }
                        }
                    }
                }
            }
        }

        System.out.printf("%d sentences ignored\n", ignoredSentences);
        System.out.printf("%d sentence candidates\n", sentences.size());

        calculateComplexityAndReorder(counter, sentences);

        // Let's ignore the 20% most complex
        int max = (int) (sentences.size() * 0.70);
        float oneEvery = max / ((float)MAX_SENTENCES_NO);
        for (float i = 0; i < max; i += oneEvery) {
            writer.writeSentence(sentences.get((int)i));
        }*/
    }
}
