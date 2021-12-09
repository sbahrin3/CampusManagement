package sbahrin;

import java.util.concurrent.ForkJoinPool;

import org.perf4j.StopWatch;

import lebah.db.Database;

public class TestParallelProcess {

	public static void main(String[] args) throws Exception {

		Database db = null;
		try {
			db = new Database("com.mysql.jdbc.Driver",
					"jdbc:mysql://localhost:3307/erican_data?zeroDateTimeBehavior=convertToNull", "root", "");

			System.out.println();
			System.out.println("Process: with java parallel API.");
			StopWatch stopWatch = new StopWatch();
			int processors = Runtime.getRuntime().availableProcessors();
			ForkJoinPool pool = new ForkJoinPool(processors);
			DataCounterTask task = new DataCounterTask(db);
			Integer result = pool.invoke(task);
			System.out.println("Result = " + result);
			stopWatch.stop();
			System.out.println("Elapsed time parallel process: " + stopWatch.getElapsedTime());

		} catch (Exception e) {
			throw e;
		}

	}

}
