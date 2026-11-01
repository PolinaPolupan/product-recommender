package ru.mirea.hadoop.recommender;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PairsMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private final Text pair_key = new Text();

    @Override
    protected void map(
            Object key,
            Text value,
            Context context
    ) throws IOException, InterruptedException {

        String line = value.toString().trim();

        if (line.isEmpty()) {
            return;
        }

        // Split the order ID from the product list.
        String[] fields = line.split("\\s+", 2);

        if (fields.length < 2) {
            return;
        }

        // Deduplicate and sort products to generate each unordered pair once.
        TreeSet<String> products = new TreeSet<>();

        Arrays.stream(fields[1].split(","))
                .map(String::trim)
                .filter(product -> !product.isEmpty())
                .forEach(products::add);

        String[] items = products.toArray(new String[0]);

        for (int i = 0; i < items.length; i++) {
            for (int j = i + 1; j < items.length; j++) {
                pair_key.set(items[i] + "," + items[j]);
                context.write(pair_key, ONE);
            }
        }
    }
}
