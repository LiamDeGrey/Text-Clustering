package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.models.Measurable;


/**
 * a class to find the similarity between all of the
 * documents using cosine similarity
 * Created by Liam on 25-Apr-16.
 */
public class DocumentSimilarity {

    static double findDocumentSimilarities(final Measurable measurable1, final Measurable measurable2) {
        final Map<String, Double> weightSum = new HashMap<String, Double>() {
            private boolean firstRun = true;

            @Override
            public void putAll(final Map<? extends String, ? extends Double> m) {
                if (firstRun) {
                    super.putAll(m);
                    firstRun = false;
                }
                else {
                    if (entrySet().size() > 0 && m.entrySet().size() > 0) {
                        final List<String> requireRemoval = new ArrayList<>();
                        String originalWord;
                        Double newWeight;
                        for (final Map.Entry<String, Double> originalItem : entrySet()) {
                            if ((newWeight = m.get(originalWord = originalItem.getKey())) != null) {
                                put(originalWord, get(originalWord) * newWeight);
                            }
                            else {
                                requireRemoval.add(originalWord);
                            }
                        }

                        requireRemoval.forEach(this::remove);
                    }
                    else {
                        clear();
                    }
                }
            }
        };

        Double vectorSum;

        weightSum.putAll(measurable1.getVector());
        weightSum.putAll(measurable2.getVector());

        vectorSum = weightSum.values().stream().mapToDouble(Double::doubleValue).sum();

        final Double result = (vectorSum / (measurable1.getVectorSum() * measurable2.getVectorSum()));

        return result.isNaN() ? 0 : result;
    }

    public static double getVectorSum(final Map<String, Double> wordVector) {
        double articleSum = 0;

        for (final Double weight : wordVector.values()) {
            articleSum += Math.pow(weight, 2);
        }
        articleSum = Math.sqrt(articleSum);

        return articleSum;
    }
}
