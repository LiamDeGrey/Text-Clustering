package results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String PATH = "Data/RESULTS.txt";
    private static final String STORAGE_FORMAT = "Used phrases : %b; Number of clusters : %d; Number of articles : %d; Accuracy : %.2f%%;";
    private static final String MATCH_FORMAT = "Used phrases : %b; Number of clusters : %d; Number of articles : %d;";
    private static final String ACCURACY_FORMAT = " %.2f%%;";

    private static boolean addedResult;

    public static void storeResult(final boolean usedPhrases, final int clusters, final int articles, final double accuracy) {
        addedResult = false;
        final List<String> lines = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(PATH))) {
                Files.lines(Paths.get(PATH)).forEach(s -> {
                    if (s.contains(String.format(MATCH_FORMAT, usedPhrases, clusters, articles))) {
                        s = s.concat(String.format(ACCURACY_FORMAT, accuracy));
                        addedResult = true;
                    }

                    lines.add(s);
                });
            }

            if (!addedResult) {
                lines.add(String.format(STORAGE_FORMAT, usedPhrases, clusters, articles, accuracy));
            }

            Files.write(Paths.get(PATH), lines, StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
