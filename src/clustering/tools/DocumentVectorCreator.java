package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.models.Article;
import clustering.models.DocumentWord;

/**
 * A helper class to create the vectors for each document.
 * Each document has an array of unique documentWord's, each
 * containing it's specific TFIDF weighting
 * Created by Liam on 24-Apr-16.
 */
public class DocumentVectorCreator {

    public static void setArticleVectors(final List<Article> articles) {
        System.out.println("STARTING: word weighting");
        final Map<String, Integer> wordCountTable = new HashMap<>();

        List<DocumentWord> documentWords;
        for (final Article article : articles) {
            documentWords = findTermFrequencies(article.getBody(), wordCountTable);
            article.setDocumentWords(documentWords);
        }
        System.out.println("COMPLETED: Term frequency pass");

        findInverseDocumentFrequencies(articles, wordCountTable);
        System.out.println("COMPLETED: Inverse document frequency pass");
    }

    private static List<DocumentWord> findTermFrequencies(final String body, final Map<String, Integer> wordCountTable) {
        final List<DocumentWord> documentWords = new ArrayList<>();

        final String[] words = body.split("\\s");
        final double documentLength = words.length;

        DocumentWord documentWord;
        String word;
        double wordInstances;
        for (int i = 0; i < words.length; i++) {
            if ((word = words[i]) != null) {
                wordCountTable.put(word, wordCountTable.get(word) != null ? wordCountTable.get(word) + 1 : 1);

                wordInstances = 1;
                for (int j = i + 1; j < words.length; j++) {
                    if (word.equalsIgnoreCase(words[j])) {
                        wordInstances++;
                        words[j] = null;
                    }
                }

                documentWords.add(documentWord = new DocumentWord(word));
                documentWord.setTermFrequency(wordInstances / documentLength);
            }
        }

        return documentWords;
    }

    private static void findInverseDocumentFrequencies(
            final List<Article> articles, final Map<String, Integer> wordCountTable) {
        final double totalDocuments = articles.size();

        for (final Article article : articles) {
            for (final DocumentWord documentWord : article.getDocumentWords()) {
                documentWord.setInverseDocumentFrequency(Math.log10((totalDocuments / (wordCountTable.get(documentWord.getWord()) * 1.0))));
            }
        }
    }
}
