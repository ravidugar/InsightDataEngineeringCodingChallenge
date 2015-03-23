/**
 * 
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * @author Ravi
 *
 */
public class Program {

	/**
	 * the method is used to initialize the queues to be used in the calculating the median
	 */
	public static void initialize() {
		lowerQueue = new PriorityQueue<Integer>(25, new Comparator<Integer>() {
			@Override
			public int compare(Integer num1, Integer num2) {
				return -num1.compareTo(num2);
			}
		});
		upperQueue = new PriorityQueue<Integer>();
		upperQueue.add(Integer.MAX_VALUE);
		lowerQueue.add(Integer.MIN_VALUE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length >= 2) {
			input = args[0];
			output = args[1];
		}
		
		List<File> files = getFileNames(input);
		countWords(files);
		writeCountWords();
		initialize();
		calculateMedian(files);
	}

	/**
	 * this method is used for getting the files present in the directory
	 * @param folderName
	 * @return
	 */
	private static List<File> getFileNames(String folderName) {
		File inputFolder = new File(folderName);
		System.out.println(inputFolder.getAbsolutePath());
		List<File> retVal = new ArrayList<File>();
		for (File file : inputFolder.listFiles()) {
			if (file.isDirectory()) {
				retVal.addAll(getFileNames(file.getPath()));
			} else {
				retVal.add(file);
			}
		}
		Collections.sort(retVal);
		return retVal;
	}

	/**
	 * this method is used to find the token present in the string
	 * @param line
	 */
	private static void tokennize(String line) {
		String[] words = split(line);
		for (String word : words) {
			if (word == null || word.equals(""))
				continue;
			String key = word.trim();
			if (!wordCountTreeMap.containsKey(key)) {
				wordCountTreeMap.put(key, 0L);
			}
			wordCountTreeMap.put(key, wordCountTreeMap.get(key) + 1);
		}
	}

	/**
	 * this method splits the line based on certain characters
	 * @param line
	 */
	private static String[] split(String line) {
		return line.toLowerCase().replace('\\', ' ')
				.split("[$@#%^&_~`+-=,'\"?!*/.;\\s{}()]");
	}

	/**
	 * this method is used to count the words in all the files
	 * @param files
	 */
	private static void countWords(List<File> files) {
		for (File file : files) {
			try (Reader reader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(reader)) {
				String bufferLine = "";
				while (true) {
					bufferLine = bufferedReader.readLine();
					if (bufferLine == null)
						break;
					tokennize(bufferLine);
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
	}

	/**
	 * this method is used to print the words and their count into a file 
	 */
	private static void writeCountWords() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output + "/wc_result.txt"), "utf-8"))) {
			for (String key : wordCountTreeMap.keySet()) {
				writer.write(key + "    " + wordCountTreeMap.get(key));
				writer.write("\n");
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * this method is used to print the median of the word counts per line
	 * @param files
	 */
	private static void calculateMedian(List<File> files) {
		for (File file : files) {
			try (Reader reader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(reader);
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(output + "/med_result.txt"), "utf-8"))) {
				String bufferLine = "";
				while (true) {
					bufferLine = bufferedReader.readLine();
					if (bufferLine == null)
						break;
					int wordCount = split(bufferLine).length;
					double median = getMedian(wordCount);
					writer.write(median + "\n");
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
	}

	/**
	 * this method is used to calculate the median.
	 * It uses two heaps which divides the entered number in two almost equal halves.
	 * Half of the numbers would be greater than the median and the rest would be lesser.
	 * The upper half will be maintained in a min heap and the lower half will be maintained in a max heap.
	 * With this it can be found out whether a new number would go to the upper half or lower half.
	 * All that needs to be done is to compare the new number with the head of two heaps.
	 * After deciding insert in a heap.
	 * After this insertion if the heaps are unbalanced, just move from one heap to another.
	 * And now find the median.
	 * If two heaps contain same number of elements then median is the average of the head of two heaps.
	 * If one is greater, then median is the head of the larger heap.
	 * @param num
	 * @return
	 */
	private static double getMedian(int num) {
		// adding the number to proper heap
		if (num >= upperQueue.peek()) {
			upperQueue.add(num);
		} else {
			lowerQueue.add(num);
		}
		
		// balancing the heaps
		if (upperQueue.size() - lowerQueue.size() == 2) {
			lowerQueue.add(upperQueue.poll());
		} else if (lowerQueue.size() - upperQueue.size() == 2) {
			upperQueue.add(lowerQueue.poll());
		}
		
		// returning the median
		if (upperQueue.size() == lowerQueue.size()) {
			return (upperQueue.peek() + lowerQueue.peek()) / 2.0;
		} else if (upperQueue.size() > lowerQueue.size()) {
			return upperQueue.peek();
		} else {
			return lowerQueue.peek();
		}
	}

	/**
	 * 
	 */
	private static TreeMap<String, Long> wordCountTreeMap = new TreeMap<String, Long>();
	private static PriorityQueue<Integer> upperQueue;
	private static PriorityQueue<Integer> lowerQueue;
	private static String input = "wc_input";
	private static String output = "wc_output";
}
