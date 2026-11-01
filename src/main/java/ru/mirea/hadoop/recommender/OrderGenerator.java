package ru.mirea.hadoop.recommender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderGenerator {

    private static final int ORDER_COUNT = 10_000;
    private static final int PRODUCT_COUNT = 100;
    private static final int MIN_ITEMS = 2;
    private static final int MAX_ITEMS = 10;

    private static final Random RANDOM = new Random(42);

    public static void main(String[] args) throws IOException {

        Path output_path = Path.of("data/generated_orders.txt");

        Files.createDirectories(output_path.getParent());

        List<String> products = new ArrayList<>();

        for (int i = 1; i <= PRODUCT_COUNT; i++) {
            products.add("product_" + i);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(output_path)) {

            for (int order_id = 1; order_id <= ORDER_COUNT; order_id++) {

                List<String> shuffled_products =
                        new ArrayList<>(products);

                Collections.shuffle(shuffled_products, RANDOM);

                int item_count =
                        MIN_ITEMS
                        + RANDOM.nextInt(MAX_ITEMS - MIN_ITEMS + 1);

                List<String> order_products =
                        shuffled_products.subList(0, item_count);

                writer.write(
                        "order_" + order_id
                        + " "
                        + String.join(",", order_products)
                );

                writer.newLine();
            }
        }

        System.out.println(
                "Generated " + ORDER_COUNT
                + " orders with "
                + PRODUCT_COUNT
                + " products."
        );

        System.out.println("Output: " + output_path);
    }
}
