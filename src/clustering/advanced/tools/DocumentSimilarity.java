package clustering.advanced.tools;

import java.util.HashMap;
import java.util.Map;

import clustering.common.models.Measurable;

/**
 * a class to find the similarity between all of the
 * documents using Euclidean distance
 * Created by Liam on 25-Apr-16.
 */
public class DocumentSimilarity {

    public static double findDocumentSimilarities(final Measurable measurable1, final Measurable measurable2) {
        final Map<String, Double> weightSum = new HashMap<String, Double>() {
            @Override
            public void putAll(final Map<? extends String, ? extends Double> m) {
                if (isEmpty()) {
                    super.putAll(m);
                }
                else {
                    Double initialValue;
                    for (final Entry<? extends String, ? extends Double> newItem : m.entrySet()) {
                        put(newItem.getKey(), Math.pow(
                                ((initialValue = get(newItem.getKey())) != null ? initialValue : 0) - newItem.getValue(),
                                2));
                    }
                }
            }
        };

        Double vectorSum;

        weightSum.putAll(measurable1.getVector());
        weightSum.putAll(measurable2.getVector());

        vectorSum = weightSum.values().stream().mapToDouble(Double::doubleValue).sum();

        return Math.sqrt(vectorSum);
    }
}
