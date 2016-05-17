package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.models.Article;


/**
 * A helper class to create the vectors for each document.
 * Each document has an array of unique documentWord's, each
 * containing it's specific TFIDF weighting
 * Created by Liam on 24-Apr-16.
 */
public class DocumentVectorCreator {
    private static final String PHRASE_FORMAT = "%s,%s";
    private static final boolean FLAG_PHRASES = false;

    /**
     * @param articles
     * @return a set of all unique words in all of the documents
     */
    public static void setArticleVectors(final List<Article> articles) {
        System.out.println("STARTING: word weighting");
        final Map<String, Integer> wordCountTable = new HashMap<>();

        findTermFrequencies(articles, wordCountTable);
        System.out.println("COMPLETED: Term frequency pass");

        findInverseDocumentFrequencies(articles, wordCountTable);
        System.out.println("COMPLETED: Inverse document frequency pass");
    }

    private static List<String> cleanTerms(final String[] words) {

        final List<String> cleanWords = new ArrayList<>();
        String tempWord;
        for (final String word : words) {
            tempWord = word.replaceAll("[^a-z]", "");

            if (!tempWord.equals("")) {
                cleanWords.add(tempWord);
            }
        }

        return cleanWords;
    }

    private static void findTermFrequencies(final List<Article> articles, final Map<String, Integer> wordCountTable) {
        final List<String> removalWords = new ArrayList<>();
        final int commonThreshold = articles.size() / 2;
        List<String> words;

        Integer universalPhraseCount;


        //Remove common words
        for (final Article article : articles) {
            words = cleanTerms(article.getBody().toLowerCase().split("\\s"));

            for (final String word : words) {
                wordCountTable.put(word, (universalPhraseCount = wordCountTable.get(word)) != null ? universalPhraseCount + 1 : 1);
            }
        }

        wordCountTable.entrySet().stream().filter(wordCount -> wordCount.getValue() > commonThreshold).forEach(wordCount -> removalWords.add(wordCount.getKey()));

        wordCountTable.clear();


        Map<String, Double> documentPhrases;
        Double documentPhraseCount;
        String word1, word2;
        String phrase;
        for (final Article article : articles) {
            documentPhrases = new HashMap<>();
            words = cleanTerms(article.getBody().toLowerCase().split("\\s"));
            for (int i = 0; i < words.size(); i++) {
                if (!removalWords.contains(word1 = words.get(i))) {
                    if (FLAG_PHRASES) {
                        if (words.size() > i + 1 && !removalWords.contains(word2 = words.get(i + 1))) {
                            phrase = String.format(PHRASE_FORMAT, word1, word2);

                            wordCountTable.put(phrase, (universalPhraseCount = wordCountTable.get(phrase)) != null ? universalPhraseCount + 1 : 1);
                            documentPhrases.put(phrase, (documentPhraseCount = documentPhrases.get(phrase)) != null ? documentPhraseCount + 1 : 1);
                        }
                    }
                    else {
                        wordCountTable.put(word1, (universalPhraseCount = wordCountTable.get(word1)) != null ? universalPhraseCount + 1 : 1);
                        documentPhrases.put(word1, (documentPhraseCount = documentPhrases.get(word1)) != null ? documentPhraseCount + 1 : 1);
                    }
                }
            }

            article.setArticleVector(documentPhrases);
        }
    }

    private static void findInverseDocumentFrequencies(
            final List<Article> articles, final Map<String, Integer> wordCountTable) {
        final double totalDocuments = articles.size();

        for (final Article article : articles) {
            for (final Map.Entry<String, Double> documentWord : article.getVector().entrySet()) {
                documentWord.setValue(documentWord.getValue() * Math.log10((totalDocuments / (wordCountTable.get(documentWord.getKey()) * 1.0))));
            }
        }
    }
}
