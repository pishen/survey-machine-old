package pishen.core;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import pishen.db.DBHandler;
import pishen.db.Record;

import com.lexicalscope.jewel.cli.CliFactory;


public class Main {
	private static CLIOptions options;
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		//setting log4j
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n")));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(
					new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n"), "survey-machine.log", false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//setting options
		options = CliFactory.parseArguments(CLIOptions.class, args);
		
		//main process with RuntimeException catched
		try{
			mainWithCatch();
		}catch(RuntimeException e){
			log.error("RuntimeException", e);
		}
	}
	
	public static void mainWithCatch(){
		DBHandler dbHandler = new DBHandler("new-graph-db");
		
		int numOfTestCases = 0;
		double cocitationSumAP = 0.0;
		double katzSumAp = 0.0;
		
		for(Record sourceRecord: Record.getAllRecords(dbHandler)){
			log.info("Checking Record " + sourceRecord.getName());
			List<TestCase> testCases = TestCase.createTestCaseList(sourceRecord, options.getHideRatio(), options.getMinSrcRefSize());
			if(testCases == null){
				continue;
			}
			
			numOfTestCases += 1;
			int count = 0;
			for(TestCase testCase: testCases){
				log.info("testCase " + (++count) + " of source " + sourceRecord.getName());
				log.info("computing cocitation");
				testCase.computeRankForCocitation();
				log.info("computing Katz");
				testCase.computeRankForKatz(options.getKatzDepth(), options.getDecay());
			}
			
			log.info("computing SumAP for cocitation");
			cocitationSumAP += new MAPComputer(options.getTopK()).computeSumAPOn(testCases, RankingAlgo.Type.Cocitation);
			//log.info("MAP of Cocitation: " + map);
			log.info("computing SumAP for Katz");
			katzSumAp += new MAPComputer(options.getTopK()).computeSumAPOn(testCases, RankingAlgo.Type.Katz);
			//log.info("MAP of Katz: " + map);
		}
		
		log.info("Number of TestCases: " + numOfTestCases);
		log.info("MAP of Cocitation: " + (cocitationSumAP / (double)numOfTestCases));
		log.info("MAP of Katz: " + (katzSumAp / (double)numOfTestCases));
		
		//Record sourceRecord = Record.getOrCreateRecord(dbHandler, "journals-sigir-Aoe90a");
		
		
	}
}
