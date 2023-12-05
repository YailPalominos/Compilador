import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalisisOptimizacion {
    /**
     * @author Braulio Yail Palominos Patiño
     */
    private List<Operacion> operaciones;// Seguardan las operaciones ya realizadas.
    private List<Simbolo> simbolos;// Generamos un sub tipo de tabla de simbolos para realizar un mejor analisis.
    // Ejemplo 1
    // T1=X+10
    // T2=X-Y
    // T3=10
    // T3=T2/4.32
    // T4=T1+T3
    // T4=X+10

    // Ejemplo 2
    // T1=X+10
    // T2=Y+X+10

    // Ejemplo 3
    // T1=X+0
    // T2=Y+T1+10

    // Ejemplo 4
    // T1=3+6
    // T2=4*6
    // T3=1+0;
    // T4=T1+T2

    // Ejemplo 5
    // = T1 X
    // + T1 10
    // = T2 A
    // - T2 X
    // = T3 T2
    // + T3 3
    // = T1 10
    // = T5 T2
    // / T5 10
    // = T6 T1
    // + T6 T5

    public AnalisisOptimizacion(List<Operacion> operaciones, List<Simbolo> simbolos) {
        this.operaciones = operaciones;
        this.simbolos = simbolos;
    }

    public void Generar() {

        // Ellimina operaciones repetidas.
        ComprobarRepeticion();
        // Instrucciones dependendientes suceptibles de reorganización.
        ComprobarReorganizacion();
        // Operaciones matematicas inecesarias o reducibles.
        ComprobarOperaciones();
        // Operaciones que no se utilizan.
        // ---> Si esta en verdadero realizara las operaciones
        ComprobarUtilidad(true);

        // Finalmente generamos el contenido el cual seran las operaciones resultantes
        // despues de haber realizado todas las comprobaciones.
        String contenido = "";
        for (Operacion operacion : operaciones) {
            contenido += operacion.asinacion + "=" + operacion.operacion + "\n";
        }
        contenido = contenido.trim();
        Generacion.GenerarCodigoIntermedio(contenido);
    }

    private void ComprobarRepeticion() {
        List<Operacion> comprobarOperaciones = new ArrayList<Operacion>();
        comprobarOperaciones = operaciones;
        for (int z = 0; z < operaciones.size(); z++) {
            for (int x = 0; x < comprobarOperaciones.size(); x++) {
                if (z != x) {
                    String operacionEvaluar = operaciones.get(z).operacion.trim();
                    String operacionAVerificar = comprobarOperaciones.get(x).operacion.trim();
                    if (operacionEvaluar.equals(operacionAVerificar) == true) {
                        comprobarOperaciones.remove(x);
                    }
                }
            }
        }
        operaciones = comprobarOperaciones;
    }

    private void ComprobarReorganizacion() {
        List<Operacion> comprobarOperaciones = new ArrayList<Operacion>();
        comprobarOperaciones = operaciones;
        for (int z = 0; z < operaciones.size(); z++) {
            for (int x = 0; x < comprobarOperaciones.size(); x++) {
                if (z != x) {
                    String operacionEvaluar = operaciones.get(z).operacion.trim();
                    String operacionAVerificar = comprobarOperaciones.get(x).operacion.trim();
                    if (operacionEvaluar.contains(operacionAVerificar) == true) {
                        Operacion operacion = operaciones.get(z);
                        operacion.operacion = operacion.operacion.replace(operacionAVerificar,
                                comprobarOperaciones.get(x).asinacion);
                        operaciones.set(z, operacion);
                    }
                }
            }
        }
    }

    private void ComprobarOperaciones() {
        List<Operacion> comprobarOperaciones = new ArrayList<Operacion>();
        comprobarOperaciones = operaciones;
        for (int z = 0; z < operaciones.size(); z++) {
            for (int x = 0; x < comprobarOperaciones.size(); x++) {
                if (z != x) {
                    String operacionEvaluar = operaciones.get(z).operacion.trim();
                    String[] operadores = { "+", "-", "/", "*" };
                    for (String operador : operadores) {
                        if (operacionEvaluar.contains(operador + "0") == true) {
                            Operacion operacion = operaciones.get(z);
                            operacion.operacion = operacion.operacion.replace(operador + "0",
                                    "");
                            operaciones.set(z, operacion);
                        }
                    }
                }
            }
        }
    }

    private void ComprobarUtilidad(boolean realizarOperaciones) {
        // Resuelve operaciones y quita las variables que no fueron usadas.
        for (Operacion operacion : operaciones) {
            Simbolo simbolo = new Simbolo();
            simbolo.token = operacion.asinacion;
            simbolo.valor = operacion.operacion;
            simbolo.repeticion = 0;
            simbolos.add(simbolo);
        }
        operaciones.clear();
        int index = 0;
        for (Simbolo simbolo : simbolos) {
            Pattern pattern = Pattern.compile("([\\d.]+|[a-zA-Z]\\w*|\\S)");
            Matcher matcher = pattern.matcher(simbolo.valor);
            String[] operacionesSeparadas = new String[0];
            while (matcher.find()) {
                String[] nuevoArreglo = new String[operacionesSeparadas.length + 1];

                for (int i = 0; i < operacionesSeparadas.length; i++) {
                    nuevoArreglo[i] = operacionesSeparadas[i];
                }
                nuevoArreglo[operacionesSeparadas.length] = matcher.group();
                operacionesSeparadas = nuevoArreglo;
            }

            String[] nuevoArregloFinal = new String[0];
            for (String operacion : operacionesSeparadas) {
                // Buscamos los valores.
                if (!esNumeroOOperador(operacion)) {
                    for (Simbolo simboloB : simbolos) {
                        if (simboloB.token.equals(operacion)) {
                            simboloB.repeticion += 1;

                            if (realizarOperaciones == true) {
                                nuevoArregloFinal = Agregar(simboloB.valor, nuevoArregloFinal);
                            } else {
                                nuevoArregloFinal = Agregar(operacion, nuevoArregloFinal);
                            }

                            // Agrega repeticion a la variable que se esta asingando variables.
                            simbolos.get(index).repeticion += 1;
                        }
                    }

                } else {
                    nuevoArregloFinal = Agregar(operacion, nuevoArregloFinal);
                }
            }
            if (nuevoArregloFinal.length > 0) {
                operacionesSeparadas = nuevoArregloFinal;
            }
            index++;
            if (realizarOperaciones == true) {
                simbolo.valor = formatearNumero(Evaluador.resultado(operacionesSeparadas));
            } else {
                simbolo.valor = concatenarArreglo(operacionesSeparadas);
            }

        }

        // Añade las operaciones que se veran en el archivo txt que se comprobaron su
        // utilidad.
        for (Simbolo simbolo : simbolos) {
            if (simbolo.repeticion > 0) {
                Operacion operacion = new Operacion(simbolo.token, simbolo.valor);
                operaciones.add(operacion);
            }
        }

    }

    public String[] Agregar(String nuevoElemento, String[] nuevoArregloFinal) {
        String[] nuevoArreglo = new String[nuevoArregloFinal.length + 1];
        for (int i = 0; i < nuevoArregloFinal.length; i++) {
            nuevoArreglo[i] = nuevoArregloFinal[i];
        }
        nuevoArreglo[nuevoArregloFinal.length] = nuevoElemento;
        nuevoArregloFinal = nuevoArreglo;
        return nuevoArregloFinal;
    }

    // Mètodos auxiliares.

    /**
     * Concatena los elementos de un arreglo de cadenas en una sola cadena.
     *
     * @param arreglo Arreglo de cadenas a concatenar.
     * @return Una cadena que contiene el contenido del arreglo.
     */
    public String concatenarArreglo(String[] arreglo) {
        StringBuilder resultado = new StringBuilder();

        for (String elemento : arreglo) {
            resultado.append(elemento);
        }

        return resultado.toString();
    }

    /**
     * Formatea un número double a una cadena evitando el punto decimal y el cero si
     * el resultado es 1.0.
     *
     * @param numero Número double a formatear.
     * @return Cadena formateada.
     */
    public static String formatearNumero(double numero) {
        DecimalFormat formato = new DecimalFormat("#.#");
        return formato.format(numero);
    }

    /**
     * Verifica si una cadena es un número o uno de los símbolos +, -, /, *.
     *
     * @param cadena Cadena a verificar.
     * @return true si la cadena es un número o uno de los símbolos especificados,
     *         false en caso contrario.
     */
    public static boolean esNumeroOOperador(String cadena) {
        try {
            // Intenta convertir la cadena a un número
            Double.parseDouble(cadena);
            // Si no se produce una excepción, la cadena es un número
            return true;
        } catch (NumberFormatException e) {
            // Si la cadena no es un número, verifica si es uno de los símbolos
            // especificados
            return esOperador(cadena);
        }
    }

    /**
     * Verifica si una cadena es uno de los operadores +, -, /, *.
     *
     * @param cadena Cadena a verificar.
     * @return true si la cadena es uno de los operadores especificados, false en
     *         caso contrario.
     */
    private static boolean esOperador(String cadena) {
        return cadena.equals("+") || cadena.equals("-") || cadena.equals("*") || cadena.equals("/");
    }
}
