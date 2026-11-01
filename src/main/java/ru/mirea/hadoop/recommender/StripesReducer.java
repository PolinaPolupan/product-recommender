package ru.mirea.hadoop.recommender;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class StripesReducer
        extends Reducer<Text, MapWritable, Text, MapWritable> {

    @Override
    protected void reduce(
            Text key,
            Iterable<MapWritable> values,
            Context context
    ) throws IOException, InterruptedException {

        MapWritable result = new MapWritable();

        for (MapWritable stripe : values) {
            for (Map.Entry<org.apache.hadoop.io.Writable,
                    org.apache.hadoop.io.Writable> entry
                    : stripe.entrySet()) {

                Text neighbor = (Text) entry.getKey();
                IntWritable value = (IntWritable) entry.getValue();

                IntWritable current = (IntWritable) result.get(neighbor);

                if (current == null) {
                    result.put(
                            new Text(neighbor),
                            new IntWritable(value.get())
                    );
                } else {
                    current.set(current.get() + value.get());
                }
            }
        }

        context.write(key, result);
    }
}
