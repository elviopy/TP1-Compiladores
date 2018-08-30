package Principal;

import java.util.Scanner;

import Principal.AnalizadorLexico;

public class Main {
	
    public static void main(String args[]) throws Exception{
    	
    	String er = null;
    	String alf = null;
    	String lexema = null;
    	
    	System.out.println("Ingrese su expresión regular seguido del  alfabeto (ab* ,abbbb):");
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Ingrese su Lexema: ");
    	sc.hasNextLine();
    	
    	//AB+ ABA+ AB+A+
    	//AB
    	//ABBB ABAAA ABBAA
    	
    	AnalizadorLexico al= new AnalizadorLexico(er, alf, lexema);
    	
    	
        /*
         *  CONVERSION REGEX -> AFN
         *  ALGORITMO DE THOMPSON
         */ 

   }
}


