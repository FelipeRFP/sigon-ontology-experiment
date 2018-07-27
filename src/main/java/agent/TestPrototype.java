/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import java.util.Scanner;

public class TestPrototype {

	public static void main(String args[]) throws Exception {

		Scanner scanner = new Scanner(System.in);
		Manager proto = new Manager();

		try { proto.loadOntology("baseOntology.owl"); } 
		catch (Exception e) {
			System.out.println(e);
			try { proto.createOntology();}
			catch (Exception e1) { e1.printStackTrace();
			}
		}

		boolean end = false;
		
		while (!end) {
			System.out.println("Assert:");
			String word = scanner.nextLine();

			proto.reason();

			System.out.println(word);

			if (!word.equalsIgnoreCase("exit") && !word.equals("")) {
				proto.ontologyAssert(word);
				continue;
			}

			try {
				proto.saveOntology("baseOntology.owl");
				end = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		end = false;

		while (!end) {
			System.out.println("Query:");
			String word = scanner.nextLine();
			System.out.println(word);

			if (!word.equalsIgnoreCase("exit") && !word.equals(""))
				System.out.println(proto.ontologyQuery(word.trim()));
			else
				end = true;

			scanner.close();

		}

	}

}
