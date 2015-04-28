import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CensusAnalysis {

	public static class CensusMapper extends Mapper<Object, Text, Text, LongWritable>{

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String record = value.toString();

			// metadata
			String state = record.substring(8, 10);
			int summaryLevel = Integer.parseInt(record.substring(10, 13));
			int recordPartIndex = Integer.parseInt(record.substring(24, 28));  //basically which segment, 1 or 2
			int recordPartCount = Integer.parseInt(record.substring(28, 32));  //basically how many segments (usually 2)

			if (recordPartIndex == 1) {
				// Q2 variables
				long unmarriedMen = readLong(record, 4423);
				long marriedMen = readLongSum(record, 4432, 3);
				long unmarriedWomen = readLong(record, 4468);
				long marriedWomen = readLongSum(record, 4477, 3);

				// Q3 variables
				long age1Men = readLongSum(record, 3865, 13);
				long age2Men = readLongSum(record, 3982, 5);
				long age3Men = readLongSum(record, 4027, 2);
				long age1Women = readLongSum(record, 4144, 13);
				long age2Women = readLongSum(record, 4261, 5);
				long age3Women = readLongSum(record, 4306, 2);
				long men = readLong(record, 364);
				long women = readLong(record, 373);

				// Q8 variables
				


			}
			if (recordPartIndex == 2) {
				// Q1 variables
				long owned = readLong(record, 1804);
				long rented = readLong(record, 1813);

				// Q4 variables
				long urban = readLongSum(record, 1858, 2);
				long rural = readLong(record, 1876);

				// Q5 variables
				long[] houseValues = readLongArray(record, 2929, 20);

				// Q6 variables
				long[] rentsPaid = readLongArray(record, 3451, 17);

				// Q7 variables



				context.write(new Text(state +".Owned"), new LongWritable(owned));
				context.write(new Text(state +".Rented"), new LongWritable(rented));
			}
		}

		private long readLong(String record, int offset) {
			return Long.parseLong(record.substring(offset -1, offset +8));
		}

		private long readLongSum(String record, int offset, int count) {
			long sum = 0;
			--offset;
			int end = offset +count *9;
			for(; offset<end; offset+=9)
				sum += Long.parseLong(record.substring(offset, offset +9));
			return sum;
		}

		private long[] readLongArray(String record, int offset, int count) {
			--offset;
			long[] longs = new long[count];
			for(int i=0; i<count; ++i) {
				longs[i] = Long.parseLong(record.substring(offset, offset + 9));
				offset += 9;
			}
			return longs;
		}
	}

	public static class CensusReducer extends Reducer<Text, LongWritable, Text, DoubleWritable> {

		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

			for (LongWritable val : values) {
				context.write(key, new DoubleWritable(val.get()));
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
		job.setJarByClass(CensusAnalysis.class);
		job.setMapperClass(CensusMapper.class);
		job.setReducerClass(CensusReducer.class);
		job.setPartitionerClass(SortPartitioner.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(32);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
