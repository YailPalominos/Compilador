import java.util.Stack;

public class Evaluador {

    public static double resultado(String[] operaciones) {
        return evaluarExpresion(operaciones);
    }

    /**
     * Evalúa una expresión matemática dada en forma de arreglo de operandos y
     * operadores.
     *
     * @return Resultado de la evaluación de la expresión.
     */
    public static double evaluarExpresion(String[] expresion) {
        Stack<Double> operandos = new Stack<>();
        Stack<String> operadores = new Stack<>();

        for (String token : expresion) {
            if (esNumero(token)) {
                operandos.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                while (!operadores.isEmpty() && precedencia(operadores.peek()) >= precedencia(token)) {
                    realizarOperacion(operandos, operadores.pop());
                }
                operadores.push(token);
            }
        }

        while (!operadores.isEmpty()) {
            realizarOperacion(operandos, operadores.pop());
        }

        return operandos.pop();
    }

    /**
     * Verifica si un token es un número.
     *
     * @param token Token a verificar.
     * @return true si el token es un número, false en caso contrario.
     */
    private static boolean esNumero(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si un token es un operador.
     *
     * @param token Token a verificar.
     * @return true si el token es un operador, false en caso contrario.
     */
    private static boolean esOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    /**
     * Retorna la precedencia de un operador.
     *
     * @param operador Operador cuya precedencia se va a obtener.
     * @return Valor numérico que representa la precedencia del operador.
     */
    private static int precedencia(String operador) {
        switch (operador) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Realiza una operación aritmética con los dos últimos operandos de la pila y
     * el último operador.
     * El resultado se coloca de nuevo en la pila de operandos.
     *
     * @param operandos Pila de operandos.
     * @param operador  Último operador.
     */
    private static void realizarOperacion(Stack<Double> operandos, String operador) {
        double segundoOperando = operandos.pop();
        double primerOperando = operandos.pop();

        switch (operador) {
            case "+":
                operandos.push(primerOperando + segundoOperando);
                break;
            case "-":
                operandos.push(primerOperando - segundoOperando);
                break;
            case "*":
                operandos.push(primerOperando * segundoOperando);
                break;
            case "/":
                operandos.push(primerOperando / segundoOperando);
                break;
        }
    }
}
