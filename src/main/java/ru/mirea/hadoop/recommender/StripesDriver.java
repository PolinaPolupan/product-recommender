package ru.mirea.hadoop.recommender;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class StripesDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Usage: StripesDriver <input-path> <output-path>"
            );
            return 2;
        }

        Job job = Job.getInstance(
                getConf(),
                "Product Co-occurrence (Stripes)"
        );

        job.setJarByClass(StripesDriver.class);

        job.setMapperClass(StripesMapper.class);
        job.setReducerClass(StripesReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(MapWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(MapWritable.class);

        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exit_code = ToolRunner.run(
                new StripesDriver(),
                args
        );

        System.exit(exit_code);
    }
}
