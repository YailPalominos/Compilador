import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalisisObjeto {
    /**
     * @author Braulio Yail Palominos Patiño
     */
    private List<Operacion> operaciones;
    private List<Simbolo> simbolos;
    private List<Operacion> operacionesFinal;
    private int IdTemporales = 1;// Identificador para las variables temporales.

    public AnalisisObjeto(List<Operacion> operaciones, List<Simbolo> simbolos) {
        this.operaciones = operaciones;
        this.simbolos = simbolos;
    }

    public void Generar() {
        operacionesFinal = new ArrayList<Operacion>();
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%70s", "Código intermedio: ");
        System.out.println();

        for (Operacion operacion : operaciones) {
            String asignacion = operacion.asinacion;
            String expresion = operacion.operacion;
            // Analizar la expresión y generar código intermedio
            generarCodigoExpresion(asignacion, expresion);

        }

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        AnalisisOptimizacion analisisOptimizacion = new AnalisisOptimizacion(operacionesFinal, simbolos);
        analisisOptimizacion.Generar();
    }

    private void generarCodigoExpresion(String variable, String valor) {
        // e=a+b*c/d;
        // g=f*2;
        String[] operandos = valor.split(" ");

        boolean guardado = false;
        boolean asignacion = true;
        String operador = "";

        agregarOperacion(operandos[0], "temp" + IdTemporales + "=");

        for (int x = 1; x < operandos.length; x++) {
            if (esOperador(operandos[x])) {
                operador = operandos[x];
            } else {

                if (guardado)
                    IdTemporales += 1;

                agregarOperacion(operandos[x], "temp" + IdTemporales + (asignacion == true ? operador : "="));

                if (guardado)
                    IdTemporales -= 1;

                if (operandos.length > 3) {
                    // = temp1 temp2
                    IdTemporales += 1;

                    agregarOperacion(operandos[x], "temp" + IdTemporales + (asignacion == true ? operador : "="));
                    guardado = true;

                }
                asignacion = false;
            }
        }

        agregarOperacion(variable, "temp=" + IdTemporales);
    }

    private String obtenerTemporal(String operando) {
        // Si el operando es un número o un símbolo, devuelve el valor directamente
        try {
            Double.parseDouble(operando);
            return operando; // Es un número, devuelve directamente
        } catch (NumberFormatException e) {
            // No es un número, busca el símbolo en la tabla de símbolos
            for (Simbolo simbolo : simbolos) {
                if (simbolo.token.equals(operando)) {
                    return "temp" + IdTemporales++;
                }
            }
            throw new IllegalArgumentException("Símbolo no encontrado: " + operando);
        }
    }

    private boolean esOperador(String caracteres) {
        if (caracteres.length() == 1) {
            if (caracteres.equals("+") || caracteres.equals("-") || caracteres.equals("*") || caracteres.equals("/")) {
                return true;
            } else {
                return false;
            }
        } else if (caracteres.length() > 1) {
            return false;
        } else {
            return false;
        }
    }

    private void agregarOperacion(String asignacion, String codigoIntermedio) {
        Operacion operacionOp = new Operacion(asignacion, codigoIntermedio);
        operacionesFinal.add(operacionOp);
    }

}
