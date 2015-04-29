import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
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
				// Q2
				long unmarriedMen = readLong(record, 4423);
				long marriedMen = readLongSum(record, 4432, 3);
				long unmarriedWomen = readLong(record, 4468);
				long marriedWomen = readLongSum(record, 4477, 3);
				LongArrayWritable q2m = new LongArrayWritable(2);
				q2m.data[0] = unmarriedMen;
				q2m.data[1] = marriedMen;
				context.write(new Text(state +".Men.PercentUnmarried"), q2m);
				LongArrayWritable q2w = new LongArrayWritable(2);
				q2w.data[0] = unmarriedWomen;
				q2w.data[1] = marriedWomen;
				context.write(new Text(state +".Women.PercentUnmarried"), q2w);

				// Q3
				long age1Men = readLongSum(record, 3865, 13);
				long age2Men = readLongSum(record, 3982, 5);
				long age3Men = readLongSum(record, 4027, 2);
				long age1Women = readLongSum(record, 4144, 13);
				long age2Women = readLongSum(record, 4261, 5);
				long age3Women = readLongSum(record, 4306, 2);
				long men = readLong(record, 364);
				long women = readLong(record, 373);
				LongArrayWritable q3m = new LongArrayWritable(4);
				q3m.data[0] = age1Men;
				q3m.data[1] = age2Men;
				q3m.data[2] = age3Men;
				q3m.data[3] = men;
				context.write(new Text(state +".Men.Ages"), q3m);
				LongArrayWritable q3w = new LongArrayWritable(4);
				q3w.data[0] = age1Women;
				q3w.data[1] = age2Women;
				q3w.data[2] = age3Women;
				q3w.data[3] = women;
				context.write(new Text(state +".Women.Ages"), q3w);

				// Q8
				long elderly = readLong(record, 1066);
				long population = readLong(record, 301);
				LongArrayWritable q8 = new LongArrayWritable(2);
				q8.data[0] = elderly;
				q8.data[1] = population;
				q8.state = state;
				context.write(new Text("HighestPercentElderly"), q8);
			}

			// processing for segment 2 records
			if (recordPartIndex == 2) {
				// Q1
				long owned = readLong(record, 1804);
				long rented = readLong(record, 1813);
				LongArrayWritable q1 = new LongArrayWritable(2);
				q1.data[0] = rented;
				q1.data[1] = owned;
				context.write(new Text(state +".Residences.PercentRented"), q1);

				// Q4
				long nonRural = readLongSum(record, 1858, 2) +readLong(record, 1885);
				long rural = readLong(record, 1876);
				LongArrayWritable q4 = new LongArrayWritable(2);
				q4.data[0] = rural;
				q4.data[1] = nonRural;
				context.write(new Text(state +".Residences.PercentRural"), q4);

				// Q5
				long[] houseValues = readLongArray(record, 2929, 20);
				LongArrayWritable q5 = new LongArrayWritable();
				q5.data = houseValues;
				context.write(new Text(state +".Residences.MedianValue"), q5);

				// Q6
				long[] rentsPaid = readLongArray(record, 3451, 16);
				LongArrayWritable q6 = new LongArrayWritable();
				q6.data = rentsPaid;
				context.write(new Text(state +".MedianRent"), q6);

				// Q7
				long[] houseRooms = readLongArray(record, 2389, 9);
				LongArrayWritable q7 = new LongArrayWritable();
				q7.data = houseRooms;
				context.write(new Text("95thPercentile"), q7);
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

	public static class AnalysisReducer extends Reducer<Text, LongArrayWritable, Text, Text> {

		public static final String[] VALUES = {"Less Than $15000",
			"$15000-$19999", "$20000-$24999", "$25000-$29999", "$30000-$34999", "$35000-$39999",
			"$40000-$44999", "$45000-$49999", "$50000-$59999", "$60000-$74999", "$75000-$99999",
			"$100000-$124999", "$125000-$149999", "$150000-$174999", "$175000-$199999", "$200000-$249999",
			"$250000-$299999", "$300000-$399999", "$400000-$499999", "More Than $500000"};
		public static final String[] RENTS = {"Less Than $100",
			"$100-$149", "$150-$199", "$200-$249", "$250-$299", "$300-$349",
			"$350-$399", "$400-$449", "$450-$499", "$500-$549", "$550-$599",
			"$600-$649", "$650-$699", "$700-$749", "$750-$999", "More Than $1000", "No Cash Rent"};

		public void reduce(Text originalKey, Iterable<LongArrayWritable> values, Context context) throws IOException, InterruptedException {

			String fullKey = originalKey.toString();
			if (fullKey.contains(".")) {
				String key = fullKey.replaceFirst(".*\\.", "");
				switch(key) {
					case "PercentRented": {
						long rented = 0, owned = 0;
						for (LongArrayWritable set : values) {
							rented += set.data[0];
							owned += set.data[1];
						}
						context.write(originalKey, new Text(String.valueOf(100.0 * rented / ((double) rented + owned)) + "%"));
						break;
					}
					case "PercentUnmarried": {
						long unmarried = 0, married = 0;
						for (LongArrayWritable set : values) {
							unmarried += set.data[0];
							married += set.data[1];
						}
						context.write(originalKey, new Text(String.valueOf(100.0 * unmarried / ((double) unmarried + married)) + "%"));
						break;
					}
					case "Ages": {
						long below18 = 0, middle = 0, older = 0, total = 0;
						for (LongArrayWritable set : values) {
							below18 += set.data[0];
							middle += set.data[1];
							older += set.data[2];
							total += set.data[3];
						}
						context.write(new Text(fullKey + ".PercentUnder18"), new Text(String.valueOf(100.0 * below18 / ((double) total)) + "%"));
						context.write(new Text(fullKey + ".Percent19-29"), new Text(String.valueOf(100.0 * middle / ((double) total)) + "%"));
						context.write(new Text(fullKey + ".Percent30-39"), new Text(String.valueOf(100.0 * older / ((double) total)) + "%"));
						break;
					}
					case "PercentRural": {
						long rural = 0, nonRural = 0;
						for (LongArrayWritable set : values) {
							rural += set.data[0];
							nonRural += set.data[1];
						}
						context.write(originalKey, new Text(String.valueOf(100.0 * rural / ((double) rural +nonRural)) + "%"));
						break;
					}
					case "MedianValue": {
						long[] counts = new long[20];
						for (int i = 0; i < counts.length; ++i)
							counts[i] = 0;
						long total = 0;
						for (LongArrayWritable set : values) {
							for (int i = 0; i < counts.length; ++i) {
								counts[i] += set.data[i];
								total += set.data[i];
							}
						}
						int median = 0;
						long remaining = total/2;
						for (; median < counts.length; ++median)
							remaining -= counts[median];
							if (remaining < 0)
								break;
						context.write(originalKey, new Text(VALUES[median]));
						break;
					}
					case "MedianRent": {
						long[] counts = new long[16];
						for (int i = 0; i < counts.length; ++i)
							counts[i] = 0;
						long total = 0;
						for (LongArrayWritable set : values) {
							for (int i = 0; i < counts.length; ++i) {
								counts[i] += set.data[i];
								total += set.data[i];
							}
						}
						int median = 0;
						long remaining = total/2;
						for (; median < counts.length; ++median)
							remaining -= counts[median];
						if (remaining < 0)
							break;
						context.write(originalKey, new Text(RENTS[median]));
						break;
					}
				}
			} else {
				if (fullKey.equalsIgnoreCase("95thPercentile")) {

				} else if (fullKey.equalsIgnoreCase("HighestPercentElderly")) {
					HashMap<String, long[]> states = new HashMap<String, long[]>();
					for (LongArrayWritable set : values) {
						if (!states.containsKey(set.state)) {
							states.put(set.state, set.data);
						} else {
							long[] state = states.get(set.state);
							state[0] += set.data[0];
							state[1] += set.data[1];
						}
					}
					String highestState = null;
					double highest = -1;
					for(String state: states.keySet()) {
						long[] stateInfo = states.get(state);
						if (highest < ((double)stateInfo[0]) /stateInfo[1]) {
							highestState = state;
							highest = ((double)stateInfo[0]) /stateInfo[1];
						}
					}
					context.write(originalKey, new Text(highestState +" " +highest +"%"));
				}
			}
		}
	}

	public static class LongArrayWritable implements Writable {
		public long[] data;
		public String state;

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

			int stateLength = (state == null)? 0: state.length();
			out.writeInt(stateLength);
			for(int i=0; i<stateLength; ++i)
				out.writeChar(state.charAt(i));
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			int length = in.readInt();
			data = new long[length];
			for(int i=0; i<length; ++i)
				data[i] = in.readLong();

			int stateLength = in.readInt();
			char[] chars = new char[stateLength];
			for(int i=0; i<stateLength; ++i)
				chars[i] = in.readChar();
			state = new String(chars);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "AnalysisJob");
		job.setJarByClass(AnalysisJob.class);
		job.setMapperClass(AnalysisMapper.class);
		job.setReducerClass(AnalysisReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongArrayWritable.class);
//		job.setNumReduceTasks(32);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
