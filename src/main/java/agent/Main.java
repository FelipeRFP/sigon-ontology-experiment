package agent;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import agent.AgentLexer;
import agent.AgentParser;
import br.ufsc.ine.agent.Agent;
import br.ufsc.ine.parser.AgentWalker;
import br.ufsc.ine.parser.VerboseListener;

public class Main {	
	public static void main(String[] args) {
		startAgent("app.on");
		
		while (true) {
			@SuppressWarnings("resource")
			Scanner scanIn = new Scanner(System.in);
			String inputString = scanIn.nextLine();
			if (!inputString.isEmpty());
				Hear.envObservable.onNext(inputString);
		}
	}
	
	public static void startAgent(String filePath) {
		try {
			File agentFile = new File(filePath);
			CharStream stream = CharStreams.fromFileName(agentFile.getAbsolutePath());
			AgentLexer lexer = new AgentLexer(stream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			AgentParser parser = new AgentParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new VerboseListener());

			ParseTree tree = parser.agent();
			ParseTreeWalker walker = new ParseTreeWalker();

			AgentWalker agentWalker = new AgentWalker();
			walker.walk(agentWalker, tree);

			Agent agent = new Agent();
			agent.run(agentWalker);


		} catch (IOException e) {
			System.out.println("I/O exception.");
		}
	}
	
	


}
