package clustering.baseline.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.common.models.Article;

/**
 * A helper class to create the vectors for each document.
 * Each document has an array of unique documentWord's, each
 * containing it's specific TFIDF weighting
 * Created by Liam on 24-Apr-16.
 */
public class DocumentVectorCreator {

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

    private static void findTermFrequencies(final List<Article> articles, final Map<String, Integer> wordCountTable) {
        Map<String, Double> documentWords;
        String[] words;
        double documentLength;

        Integer wordCount;
        String word;
        double wordInstances;
        for (final Article article : articles) {
            documentWords = new HashMap<>();
            words = article.getBody().toLowerCase().split("\\s");
            documentLength = words.length;

            for (int i = 0; i < words.length; i++) {
                if ((word = words[i]) != null) {
                    wordCountTable.put(word, (wordCount = wordCountTable.get(word)) != null ? wordCount + 1 : 1);

                    wordInstances = 1;
                    for (int j = i + 1; j < words.length; j++) {
                        if (word.equalsIgnoreCase(words[j])) {
                            wordInstances++;
                            words[j] = null;
                        }
                    }

                    documentWords.put(word, wordInstances / documentLength);
                }
            }

            article.setArticleVector(documentWords);
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
