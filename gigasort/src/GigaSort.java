import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GigaSort {

	public static class SortMapper extends Mapper<Object, Text, LongWritable, LongWritable>{

		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			LongWritable num = new LongWritable(Long.parseLong(value.toString()));
			context.write(num, num);
		}
	}

	public static class SortReducer extends Reducer<LongWritable, LongWritable, LongWritable, LongWritable> {

		private long counter = 0;

		public void reduce(LongWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			for (LongWritable val : values) {
				if (counter %1000 == 0)
					context.write(key, key);
				++counter;
			}
		}
	}

	public static class SortPartitioner extends Partitioner<LongWritable, LongWritable> {
		public int getPartition(LongWritable key, LongWritable value, int numReduceTasks) {
			long partitionSize = Long.MAX_VALUE /numReduceTasks;
			return (int) (key.get() /partitionSize);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "GigaSort");
		job.setJarByClass(GigaSort.class);
		job.setMapperClass(SortMapper.class);
		job.setReducerClass(SortReducer.class);
		job.setPartitionerClass(SortPartitioner.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(32);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
