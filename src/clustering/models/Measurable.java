package clustering.models;

import java.util.Map;

/**
 * Created by Liam on 29-Apr-16.
 */
public interface Measurable {

    Map<String, Double> getVector();

    double getVectorSum();
}
