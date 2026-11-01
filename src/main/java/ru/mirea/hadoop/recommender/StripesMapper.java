package ru.mirea.hadoop.recommender;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class StripesMapper
        extends Mapper<Object, Text, Text, MapWritable> {

    private final Text product_key = new Text();

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

        String[] fields = line.split("\\s+", 2);

        if (fields.length < 2) {
            return;
        }

        TreeSet<String> products = new TreeSet<>();

        Arrays.stream(fields[1].split(","))
                .map(String::trim)
                .filter(product -> !product.isEmpty())
                .forEach(products::add);

        String[] items = products.toArray(new String[0]);

        for (int i = 0; i < items.length; i++) {
            MapWritable stripe = new MapWritable();

            for (int j = 0; j < items.length; j++) {
                if (i == j) {
                    continue;
                }

                Text neighbor = new Text(items[j]);
                IntWritable count = new IntWritable(1);

                stripe.put(neighbor, count);
            }

            product_key.set(items[i]);
            context.write(product_key, stripe);
        }
    }
}
