/**
 * Created by WeiLi on 10/14/17.
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertedIndex {

    public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text>{
        private Text word = new Text();
        private final static Text docId = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
            //Reading input one line at a time and tokenizing
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            if(line.contains("\t")){
                tokenizer.nextToken();
                tokenizer.nextToken();
            }

            FileSplit filesplit = (FileSplit)context.getInputSplit();
            String filename = filesplit.getPath().getName();
            filename = filename.substring(0, filename.length()-4);
            docId.set(filename);
            while(tokenizer.hasMoreTokens()){
                word.set(tokenizer.nextToken());
                context.write(word, docId);
            }
        }
    }


    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            Map<String, Integer> map = new HashMap<>();
            for(Text value : values){
                String str = value.toString();
                if(map.containsKey(str)){
                    map.put(str, map.get(str) + 1);
                }else
                    map.put(str, 1);
            }
            StringBuilder sb = new StringBuilder();
            for(String doc : map.keySet()){
                sb.append(doc + ":" + map.get(doc) + "  ");
            }
            context.write(key, new Text(sb.toString()));
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
        if(args.length != 2){
            System.err.println("Usage: Inverted Index <input path> <output path>");
            System.exit(-1);
        }
        //Creating  a Hadoop jpb and assigning a job name for identification
        Job job = new Job();
        job.setJarByClass(InvertedIndex.class);
        job.setJobName("Inverted Index");
        //The HDFS input and output directories to be fetched from the Dataproc job submission console
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        //Providing the mapper and reducer class names.
        job.setMapperClass(InvertedIndexMapper.class);
        job.setReducerClass(InvertedIndexReducer.class);
        //Setting the job object with the data types of output key(Text) and value(Text)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
