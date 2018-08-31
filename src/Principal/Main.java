package Principal;

/*
 @author Elvio Velazquez - Marcelo Vera
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Stack;

public class Main {
	/*
	 * El objeto se utiliza como una tupla de 3 elementos para 
	 * representar transiciones(estado inicio, simbolo
	 * de transicion, estado fin)
	 */
	public static class Trans {
		public int estado_ini, estado_fin;
		public char trans_symbol;

		public Trans(int v1, int v2, char sym) {
			this.estado_ini = v1;
			this.estado_fin = v2;
			this.trans_symbol = sym;
		}
	}

	/*
	 * sirve como el gráfico que representa los NFA Utilizará¡ esto para combinar mejor los
	 * estados.
	 */
	public static class NFA {
		public ArrayList<Integer> estados;
		public ArrayList<Trans> transicion;
		public int final_estado;

		public NFA() {
			this.estados = new ArrayList<Integer>();
			this.transicion = new ArrayList<Trans>();
			this.final_estado = 0;
		}

		public NFA(int size) {
			this.estados = new ArrayList<Integer>();
			this.transicion = new ArrayList<Trans>();
			this.final_estado = 0;
			this.setEstadoTam(size);
		}

		public NFA(char c) {
			this.estados = new ArrayList<Integer>();
			this.transicion = new ArrayList<Trans>();
			this.setEstadoTam(2);
			this.final_estado = 1;
			this.transicion.add(new Trans(0, 1, c));
		}

		public void setEstadoTam(int size) {
			for (int i = 0; i < size; i++)
				this.estados.add(i);
		}

		public void display() {
			for (Trans t : transicion) {
				System.out.println("(" + t.estado_ini + ", " + t.trans_symbol
						+ ", " + t.estado_fin + ")");
			}
		}
	}

	/*
	 Thompson algoritmo para kleene star
	 */
	public static NFA kleene(NFA n) {
		NFA resultado = new NFA(n.estados.size() + 2);
		resultado.transicion.add(new Trans(0, 1, 'E')); // new trans for q0

		// copia las transiciones existentes
		for (Trans t : n.transicion) {
			resultado.transicion.add(new Trans(t.estado_ini + 1,
					t.estado_fin + 1, t.trans_symbol));
		}

		// agrega una transicion vacio desde el estado final n al nuevo estado final
		resultado.transicion.add(new Trans(n.estados.size(),
				n.estados.size() + 1, 'E'));

		// Retrocede desde el último estado de n al estado inicial de n.
		resultado.transicion.add(new Trans(n.estados.size(), 1, 'E'));

		// agrega una transicion vacia desde el nuevo estado inicial al nuevo estado final
		resultado.transicion.add(new Trans(0, n.estados.size() + 1, 'E'));

		resultado.final_estado = n.estados.size() + 1;
		return resultado;
	}

	/*
	 * Algoritmo de THompson para concatenacion.
	 */
	public static NFA concat(NFA n, NFA m) {
		// /*
		m.estados.remove(0); // borra estado inicial
		// copy NFA m's transición to n, y conecta n & m
		//
		for (Trans t : m.transicion) {
			n.transicion.add(new Trans(t.estado_ini + n.estados.size() - 1,
					t.estado_fin + n.estados.size() - 1, t.trans_symbol));
		}

		// toma m y se combina con n después de borrar el estado m inicial
		for (Integer s : m.estados) {
			n.estados.add(s + n.estados.size() + 1);
		}

		n.final_estado = n.estados.size() + m.estados.size() - 2;
		return n;
	
	}

	/*
	 * union() - Lowest Precedence regular expression operator. Thompson
	 * algorithm for union (or).
	 */
	public static NFA union(NFA n, NFA m) {
		NFA resultado = new NFA(n.estados.size() + m.estados.size() + 2);

		// la ramificación de q0 al inicio de n
		resultado.transicion.add(new Trans(0, 1, 'E'));

		// copiar las transiciones existentes en n
		for (Trans t : n.transicion) {
			resultado.transicion.add(new Trans(t.estado_ini + 1,
					t.estado_fin + 1, t.trans_symbol));
		}

		// transicion desde el ultimo n hasta el estado final
		resultado.transicion.add(new Trans(n.estados.size(), n.estados.size()
				+ m.estados.size() + 1, 'E'));

		// la ramificación de q0 al inicio de m
		resultado.transicion.add(new Trans(0, n.estados.size() + 1, 'E'));

		// copia transiciones existentes de m
		for (Trans t : m.transicion) {
			resultado.transicion.add(new Trans(t.estado_ini + n.estados.size()
					+ 1, t.estado_fin + n.estados.size() + 1, t.trans_symbol));
		}

		// transición de la última m al estado final
		resultado.transicion.add(new Trans(m.estados.size() + n.estados.size(),
				n.estados.size() + m.estados.size() + 1, 'E'));

		// 2 nuevos estados y cambiado m para evitar la repetición de la
		// última n & 1 m
		resultado.final_estado = n.estados.size() + m.estados.size() + 1;
		return resultado;
	}



	// simplifica las comprobaciones de condición boolean repetidas
	public static boolean alfa(char c) {
		return c >= 'a' && c <= 'z';
	}

	public static boolean alfabeto(char c) {
		return alfa(c) || c == 'E';
	}

	public static boolean expregOperator(char c) {
		return c == '(' || c == ')' || c == '*' || c == '|';
	}

	public static boolean validarExpRegChar(char c) {
		return alfabeto(c) || expregOperator(c);
	}

	// comprueba si la cadena dada es una expresión regular válida
	public static boolean validarExpReg(String expreg) {
		if (expreg.isEmpty())
			return false;
		for (char c : expreg.toCharArray())
			if (!validarExpRegChar(c))
				return false;
		return true;
	}

	/*
		*compile() - compila una expresión regular dada en un NFA usando Thompson
		*Algoritmo de construcción. Implementará el típico modelo de pila de compilación para
		*simplificar el procesamiento de la cadena. Esto le da prioridad descendente a
		*personajes a la derecha.
	*/
	public static NFA compile(String expreg) {
		if (!validarExpReg(expreg)) {
			System.out.println("Expresión Regular Invalida.");
			return new NFA();
		}

		Stack<Character> operadores = new Stack<Character>();
		Stack<NFA> operandos = new Stack<NFA>();
		Stack<NFA> concat_stack = new Stack<NFA>();
		boolean ccflag = false; //bandera de concatenacion
		char op, c; //caracter actual de la cadena
		int para_count = 0;
		NFA nfa1, nfa2;

		for (int i = 0; i < expreg.length(); i++) {
			c = expreg.charAt(i);
			if (alfabeto(c)) {
				operandos.push(new NFA(c));
				if (ccflag) { // concatena
					operadores.push('.'); // '.' representa a la concatenación.
				} else
					ccflag = true;
			} else {
				if (c == ')') {
					ccflag = false;
					if (para_count == 0) {
						System.out.println("Error: Más de un parentesis "
								+ "borre un parentesis");
						System.exit(1);
					} else {
						para_count--;
					}
					// procesa la pila de operadores hasta '('
					while (!operadores.empty() && operadores.peek() != '(') {
						op = operadores.pop();
						if (op == '.') {
							nfa2 = operandos.pop();
							nfa1 = operandos.pop();
							operandos.push(concat(nfa1, nfa2));
						} else if (op == '|') {
							nfa2 = operandos.pop();

							if (!operadores.empty() && operadores.peek() == '.') {

								concat_stack.push(operandos.pop());
								while (!operadores.empty()
										&& operadores.peek() == '.') {

									concat_stack.push(operandos.pop());
									operadores.pop();
								}
								nfa1 = concat(concat_stack.pop(),
										concat_stack.pop());
								while (concat_stack.size() > 0) {
									nfa1 = concat(nfa1, concat_stack.pop());
								}
							} else {
								nfa1 = operandos.pop();
							}
							operandos.push(union(nfa1, nfa2));
						}
					}
				} else if (c == '*') {
					operandos.push(kleene(operandos.pop()));
					ccflag = true;
				} else if (c == '(') { // if any other operator: push
					operadores.push(c);
					para_count++;
				} else if (c == '|') {
					operadores.push(c);
					ccflag = false;
				}
			}
		}
		while (operadores.size() > 0) {
			if (operandos.empty()) {
				System.out.println("Error: imbalanace in operandos and "
						+ "operadores");
				System.exit(1);
			}
			op = operadores.pop();
			if (op == '.') {
				nfa2 = operandos.pop();
				nfa1 = operandos.pop();
				operandos.push(concat(nfa1, nfa2));
			} else if (op == '|') {
				nfa2 = operandos.pop();
				if (!operadores.empty() && operadores.peek() == '.') {
					concat_stack.push(operandos.pop());
					while (!operadores.empty() && operadores.peek() == '.') {
						concat_stack.push(operandos.pop());
						operadores.pop();
					}
					nfa1 = concat(concat_stack.pop(), concat_stack.pop());
					while (concat_stack.size() > 0) {
						nfa1 = concat(nfa1, concat_stack.pop());
					}
				} else {
					nfa1 = operandos.pop();
				}
				operandos.push(union(nfa1, nfa2));
			}
		}
		return operandos.pop();
	}

	public static void main(String[] args) throws Exception {

		// Lectura de fichero
		String[] regex;
		String[] lexema;
		String archivo = "C:/WorkspaceCompiladores/TP1-Compiladores/src/resourses/archivo.txt";
		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);

		regex = b.readLine().split(" ");
		lexema = b.readLine().split(" ");

		b.close();

		// Analizamos si los lexemas pertenecen a las ER
		int flag = 0;
		for (int i = 0; i < regex.length; i++) {
			for (int j = 0; j < lexema.length; j++) {
				// si un lexema pertenece a una regex true
				if (lexema[i].matches(regex[i])) {
					System.out.println(lexema[i]
							+ " es un lexema que pertenece a la clase "
							+ regex[i]);
					flag++;
					break;
				}
			}
		}
		if (flag == 0)
			System.out.println("el lexema no pertenece a ninguna clase");
		else
			for (int j = 0; j < regex.length; j++) {
				NFA nfa_of_input = compile(regex[j].toString());
				System.out.println("\nNFA:");
				nfa_of_input.display();
			}
	}
}
