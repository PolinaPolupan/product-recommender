package ru.mirea.hadoop.recommender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Recommender {

    private static final String STRIPES_OUTPUT =
            "/user/polina/recommender/generated-stripes/part-r-00000";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: Recommender <product>");
            System.exit(2);
        }

        String requested_product = args[0];

        Configuration configuration = new Configuration();

        Path output_path = new Path(STRIPES_OUTPUT);

        try (FileSystem file_system =
                     output_path.getFileSystem(configuration);
             BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     file_system.open(output_path),
                                     StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+", 2);

                if (parts.length != 2) {
                    continue;
                }

                String product = parts[0];

                if (!product.equals(requested_product)) {
                    continue;
                }

                Map<String, Integer> recommendations =
                        parseStripe(parts[1]);

                printRecommendations(recommendations);
                return;
            }
        }

        System.out.println(
                "Product not found: " + requested_product
        );
    }

    private static Map<String, Integer> parseStripe(String stripe) {
        Map<String, Integer> result = new HashMap<>();

        stripe = stripe.trim();

        if (stripe.startsWith("{")) {
            stripe = stripe.substring(1);
        }

        if (stripe.endsWith("}")) {
            stripe = stripe.substring(0, stripe.length() - 1);
        }

        if (stripe.isBlank()) {
            return result;
        }

        String[] entries = stripe.split(",");

        for (String entry : entries) {
            String[] parts = entry.trim().split("=");

            if (parts.length != 2) {
                continue;
            }

            String product = parts[0].trim();
            int count = Integer.parseInt(parts[1].trim());

            result.put(product, count);
        }

        return result;
    }

    private static void printRecommendations(
            Map<String, Integer> recommendations) {

        List<Map.Entry<String, Integer>> sorted =
                new ArrayList<>(recommendations.entrySet());

        sorted.sort(
                Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey())
        );

        System.out.println("Recommendations:");
	
	int limit = Math.min(10, sorted.size());
	
	for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);

            System.out.printf(
                    "%s\t%d%n",
                    entry.getKey(),
                    entry.getValue()
            );
	}
    }
}
