/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyManager;

import java.util.Scanner;
        
public class TestPrototype {
    
    public static void main(String args[]) throws Exception{
        
        Scanner scanner = new Scanner(System.in);
        Prototype proto = new Prototype();
        
        try{
            proto.loadOntology("/home/felipe/Desktop/ExampleOntology.txt"); } 
        catch(Exception e){
            try { proto.createOntology();} 
            catch(Exception e1) { e1.printStackTrace(); }
        }
        
        boolean end = false;
        while(!end){
            System.out.println("Assert:");
            String word = scanner.nextLine();
            
            proto.reason();
            
            System.out.println(word);
            
            if(!word.equalsIgnoreCase("exit") && !word.equals("")){
                proto.ontologyAssert(word);
                continue;
            }
            
            
            
            try{
                proto.saveOntology("/home/felipe/Desktop/ExampleOntology.txt");
                end = true;

            }catch(Exception e){
                    e.printStackTrace();
            }
        }  
        
        end = false;
        
        while(!end){
            System.out.println("Query:");
            String word = scanner.nextLine();
            System.out.println(word);
            
            if(!word.equalsIgnoreCase("exit") && !word.equals(""))
                System.out.println(proto.ontologyQuery(word.trim()));
            else
                end = true;
            

         scanner.close();
            
            
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
