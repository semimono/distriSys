import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AnalysisJob {

	public static class AnalysisMapper extends Mapper<Object, Text, Text, LongArrayWritable>{

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String record = value.toString();

			// only processing records with summary level 100
			int summaryLevel = Integer.parseInt(record.substring(10, 13));
			if (summaryLevel != 100)
				return;

			// metadata
			String state = record.substring(8, 10);
			int recordPartIndex = Integer.parseInt(record.substring(24, 28));

			// processing for segment 1 records
			if (recordPartIndex == 1) {
				// Q2 variables
				long unmarriedMen = readLong(record, 4423);
				long marriedMen = readLongSum(record, 4432, 3);
				long unmarriedWomen = readLong(record, 4468);
				long marriedWomen = readLongSum(record, 4477, 3);
				LongArrayWritable q2m = new LongArrayWritable(2);
				q2m.data[0] = unmarriedMen;
				q2m.data[1] = marriedMen;
				context.write(new Text(state +".UnmarriedMen"), q2m);
				LongArrayWritable q2w = new LongArrayWritable(2);
				q2w.data[0] = unmarriedWomen;
				q2w.data[1] = marriedWomen;
				context.write(new Text(state +".UnmarriedWomen"), q2w);

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
				long population = readLong(record, 301);
				long elderly = readLong(record, 1066);
			}

			// processing for segment 2 records
			if (recordPartIndex == 2) {
				// Q1 variables
				long owned = readLong(record, 1804);
				long rented = readLong(record, 1813);
//				context.write(new Text(state +".Owned"), new LongWritable(owned));
//				context.write(new Text(state +".Rented"), new LongWritable(rented));

				// Q4 variables
				long urban = readLongSum(record, 1858, 2);
				long rural = readLong(record, 1876);

				// Q5 variables
				long[] houseValues = readLongArray(record, 2929, 20);

				// Q6 variables
				long[] rentsPaid = readLongArray(record, 3451, 17);

				// Q7 variables
				long[] houseRooms = readLongArray(record, 2389, 9);
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

	public static class AnalysisReducer extends Reducer<Text, LongArrayWritable, Text, DoubleWritable> {

		public void reduce(Text originalKey, Iterable<LongArrayWritable> values, Context context) throws IOException, InterruptedException {

			String fullKey = originalKey.toString();
			if (fullKey.contains(".")) {
				String key = fullKey.replaceFirst(".*?\\.", "");
				switch(key) {
					case "UnmarriedMen":
					case "UnmarriedWomen":
						long unmarried = 0, married = 0;
						for (LongArrayWritable set : values) {
							unmarried += set.data[0];
							married += set.data[1];
						}
						context.write(originalKey, new DoubleWritable(unmarried /((double)unmarried +married)));
						break;
				}
			} else {

			}
		}
	}

	public static class LongArrayWritable implements Writable {
		public long[] data;

		LongArrayWritable() {}
		LongArrayWritable(int size) {
			data = new long[size];
		}

		@Override
		public void write(DataOutput out) throws IOException {
			int length = (data == null)? 0: data.length;
			out.writeInt(length);
			for(long l: data)
				out.writeLong(l);
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			int length = in.readInt();
			data = new long[length];
			for(int i=0; i<length; ++i)
				data[i] = in.readLong();
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "AnalysisJob");
		job.setJarByClass(AnalysisJob.class);
		job.setMapperClass(AnalysisMapper.class);
		job.setReducerClass(AnalysisReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(32);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
