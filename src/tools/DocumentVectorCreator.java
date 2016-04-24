package tools;

import java.util.ArrayList;
import java.util.List;

import models.Article;
import models.DocumentWord;

/**
 * A helper class to create the vectors for each document.
 * Each document has an array of unique documentWord's, each
 * containing it's specific TFIDF weighting
 * Created by Liam on 24-Apr-16.
 */
public class DocumentVectorCreator {

    public static void setArticleVectors(final List<Article> articles) {
        System.out.println("STARTING: word weighting");

        double index = 0;
        List<DocumentWord> documentWords;
        for (final Article article : articles) {
            documentWords = findTermFrequencies(article.getBody());
            System.out.println(String.format("COMPLETED: %.8f%%", (index += 0.5) / articles.size()));
            findInverseDocumentFrequencies(articles, documentWords);
            System.out.println(String.format("COMPLETED: %.8f%%", (index += 0.5) / articles.size()));
        }
    }

    private static List<DocumentWord> findTermFrequencies(final String body) {
        final List<DocumentWord> documentWords = new ArrayList<>();

        final String[] words = body.split("\\s");
        final int documentLength = words.length;

        DocumentWord documentWord;
        String word;
        int wordInstances;
        for (int i = 0; i < words.length; i++) {
            if ((word = words[i]) != null) {
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

    private static List<DocumentWord> findInverseDocumentFrequencies(
            final List<Article> articles, final List<DocumentWord> documentWords) {
        final int totalDocuments = articles.size();

        int documentCount;
        String currentWord;
        for (final DocumentWord documentWord : documentWords) {
            documentCount = 0;
            currentWord = documentWord.getWord().toLowerCase();

            for (final Article article : articles) {
                if (article.getBody().toLowerCase().contains(currentWord)) {
                    documentCount++;
                }
            }

            documentWord.setInverseDocumentFrequency(Math.log((totalDocuments / documentCount)));
        }

        return documentWords;
    }
}
