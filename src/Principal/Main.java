package Principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Principal.AnalizadorLexico;



public class Main {
	
	
    public static void main(String args[]) throws Exception{
    
    	AnalizadorLexico al =new AnalizadorLexico();
    	

    	
    	//Lectura de fichero    	

        String[] regex;
        String[] lexema;
        String archivo= "C:/WorkspaceCompiladores/TP1-Compiladores/src/resourses/archivo.txt";
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
     
        regex= b.readLine().split(" ");
        lexema= b.readLine().split(" ");
              
        b.close();
        
        //Analizamos si los lexemas pertenecen a las ER
        int flag= 0; 
        for (int i = 0; i < regex.length; i++) {
        	for (int j = 0; j < lexema.length; j++) {
        	  	//si un lexema pertenece a una regex true
        		if(lexema[i].matches(regex[i])){
        			System.out.println(lexema[i]+" es un lexema que pertenece a la clase "+regex[i]);
        			flag++;
        			break; 
        		}
			}
		}
        if(flag==0)
			System.out.println("el lexema no pertenece a ninguna clase");

   }
}


