package info.puzz.a10000sentences.importer.importers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import info.puzz.a10000sentences.apimodels.CollectionType;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;

public class TMXImporter extends Importer {

    private static final String TU = "tu";
    private static final String TUV = "tuv";

    private final String baseFilename;

    int ignoredSentences;

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
        SAXParserFactory factory = SAXParserFactory.newInstance();
        final WordCounter counter = new WordCounter();
        final List<SentenceVO> sentences = new ArrayList<>();
        final Set<Integer> knownSenteceHashes = new HashSet<>();
        final Set<Integer> targetSenteceHashes = new HashSet<>();


        try {

            SAXParser parser = factory.newSAXParser();
            File file = new File(String.format("%s/%s", RAW_FILES_PATH, baseFilename));

            parser.parse(file, new HandlerBase() {

                private boolean sentence;

                private Map<String, String> sentenceTranslations;

                String currentLang;

                public void notationDecl(String name, String publicId, String systemId) {
                    //System.out.println("notationDecl");
                }

                public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
                    //System.out.println("unparsedEntityDecl");
                }

                public void setDocumentLocator(Locator locator) {
                    //System.out.println("setDocumentLocator");
                }

                public void startDocument() throws SAXException {
                    //System.out.println("startDocument");
                }

                public void endDocument() throws SAXException {
                    //System.out.println("endDocument");
                }

                public void startElement(String name, AttributeList attributes) throws SAXException {
                    if (TU.equals(name)) {
                        sentence = true;
                    }
                    if (TUV.equals(name)) {
                        if (attributes != null && attributes.getLength() > 0) {
                            for (int i = 0; i < attributes.getLength(); i++) {
                                if ("xml:lang".equals(attributes.getName(i))) {
                                    currentLang = attributes.getValue(i);
                                }
                            }
                        }
                    }
                    //System.out.println("startElement " + name + " " + (attributes == null ? 0 : attributes.getLength()));
                }

                public void endElement(String name) throws SAXException {
                    if (TU.equals(name)) {
                        sentence = false;
                        //System.out.println(sentenceTranslations);

//                        System.out.println(sentenceTranslations);
//                        System.out.println(knownLang);
//                        System.out.println(targetLang);
                        if (sentences.size() < 60_000) {
                            String knownLine = sentenceTranslations.get(knownLang.getAbbrev());
                            String targetLine = sentenceTranslations.get(targetLang.getAbbrev());
                            if (StringUtils.isNotEmpty(targetLine) && StringUtils.isNotEmpty(knownLine)) {
                                if (Character.isUpperCase(targetLine.charAt(0)) && Character.isUpperCase(knownLine.charAt(0))) {
                                    String id = String.format("%s-%s-%d", knownLang.getAbbrev(), targetLang.getAbbrev(), targetLine.hashCode());
                                    SentenceVO s = new SentenceVO().setSentenceId(String.valueOf(id)).setKnownSentence(knownLine).setTargetSentence(targetLine);
                                    Integer knownHash = s.getKnownSentence().hashCode();
                                    Integer targetHash = s.getTargetSentence().hashCode();
                                    if (!targetSenteceHashes.contains(targetHash) && !knownSenteceHashes.contains(knownHash) && sentenceOK(s)) {
                                        sentences.add(s);
                                        counter.countWordsInSentence(s, knownLang, targetLang);
                                        knownSenteceHashes.add(knownHash);
                                        targetSenteceHashes.add(targetHash);
                                        if (sentences.size() % 1000 == 0) {
                                            System.out.println(String.format("%d sentences", sentences.size()));
                                        }
                                    } else {
                                        ignoredSentences += 1;
                                    }
                                }
                            }
                            //System.out.println("endElement " + name);
                        }

                        sentenceTranslations = null;
                        currentLang = null;
                    }
                }

                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (sentence && currentLang != null) {
                        if (sentenceTranslations == null) {
                            sentenceTranslations = new HashMap<>();
                        }
                        String s = StringUtils.trim(new String(Arrays.copyOfRange(ch, start, start + length)));
                        if (StringUtils.isNotEmpty(s)) {
                            sentenceTranslations.put(currentLang, s);
                        }
                    }
                }

                public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                    //System.out.println("ignorableWhitespace");
                }

                public void processingInstruction(String target, String data) throws SAXException {
                    //System.out.println("processingInstruction");
                }

                public void warning(SAXParseException e) throws SAXException {
                    //System.out.println("warning");
                }

                public void error(SAXParseException e) throws SAXException {
                    //System.out.println("error");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        int ignoredSentences = 0;
        //System.out.printf("%d sentences ignored\n", ignoredSentences);
        //System.out.printf("%d sentence candidates\n", sentences.size());

        calculateComplexityAndReorder(counter, sentences);

        // Let's ignore the 20% most complex
        int max = (int) (sentences.size() * 0.70);
        float oneEvery = max / ((float) MAX_SENTENCES_NO);
        for (float i = 0; i < max; i += oneEvery) {
            writer.writeSentence(sentences.get((int) i));
        }
    }

}

