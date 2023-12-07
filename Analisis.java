import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analisis {
    /**
     * @author Braulio Yail Palominos Patiño
     */
    private String codigoFuente = ""; // Codigo fuente resibido de la clase de ejecutador.
    private List<Simbolo> tablaSimbolos;// Lista de tablaSimbolos donde se guardara toda la información.
    private List<Errores> tablaErrores;// Lista de tablaErrores donde se guaradara todos los errores
    private List<String> operaciones;// Resultado de las operaciones.
    private int posLectura = 0; // Posición de lectura con respecto al codigo fuente.
    private int linea = 1; // Linea en la que va la posicion de lectura con respecto al codigo fuente.
    private int Id = 1;// Identificardor auto incremental para las variables.
    private int IdConstantes = 500;// Identificardor auto incremental para las variables.
    private List<Operacion> operacionesObjeto;// Seguardan las operaciones ya realizadas.

    public Analisis(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    // Genera las clases con los simbolos
    public void Generar() {

        // Inicializamos la tabla de simbolos
        tablaSimbolos = new ArrayList<Simbolo>();
        tablaErrores = new ArrayList<Errores>();
        operaciones = new ArrayList<String>();
        operacionesObjeto = new ArrayList<Operacion>();
        // Codigo fuente a chart para leer parte por parte
        var letras = this.codigoFuente.toCharArray();
        String palabra = "";
        String palabras = "";

        // Expreciones regulares:
        Pattern cambioLinea = Pattern.compile("\n");
        Pattern iniciarVariable = Pattern.compile("((Real|Entero) ([a-zA-Z0-9]*)\\;)");
        Pattern iniciarVariableConValor = Pattern.compile("((Real|Entero) ([a-zA-Z0-9]*)\\=\\d*\\;)");
        Pattern iniciarVariables = Pattern
                .compile("((Real|Entero) (([a-zA-Z0-9]+)\\,)(([a-zA-Z0-9]+)\\,)*(([a-zA-Z0-9]+)\\;))");
        Pattern iniciarVariablesConValor = Pattern.compile(
                "((Real|Entero) (([a-zA-Z0-9]+\\=\\d*)\\,)(([a-zA-Z0-9]+\\=\\d*)\\,)*(([a-zA-Z0-9]+\\=\\d*)\\;))");
        Pattern leerOEscribirVariables = Pattern.compile("((Leer|Escribir)\\(([a-zA-Z0-9]*\\))\\;)");
        Pattern realizarOperacion = Pattern.compile(
                "(\\w+)\\s*=\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*((?:[-+*/]\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*)+)(\\s*(\\([^()]+\\)|\\[[^\\[\\]]+\\]))?\\s*;");

        /*
         * 1 ((Real|Entero) ([a-zA-Z0-9]*)\;)
         * 2 ((Real|Entero) ([a-zA-Z0-9]*)\=\d*\;)
         * 3 ((Real|Entero) (([a-zA-Z0-9]+)\,)(([a-zA-Z0-9]+)\,)*(([a-zA-Z0-9]+)\;))
         * 4 ((Real|Entero)
         * 5 (([a-zA-Z0-9]+\=\d*)\,)(([a-zA-Z0-9]+\=\d*)\,)*(([a-zA-Z0-9]+\=\d*)\;))
         * 6 (Leer\(([a-zA-Z0-9]*\))\;)
         * 7
         * 8 (Escribir\(([a-zA-Z0-9]*\))\;)
         * Completa
         * [a-zA-Z0-9]+\=+(([a-zA-Z0-9.]+(\+|\/|\*|\-)+[a-zA-Z0-9.]+\;)|([a-zA-Z0-9.]+(\
         * +|\/|\*|\-)+\(+[a-zA-Z0-9.]+(\+|\-|\/|\*)+[a-zA-Z0-9.]+\)+\;))
         * 1 [a-zA-Z0-9]+\=+(([a-zA-Z0-9.]+(\+|\/|\*|\-)+[a-zA-Z0-9.]+\;)|)
         * 2
         * ([a-zA-Z0-9.]+(\+|\/|\*|\-)+\(+[a-zA-Z0-9.]+(\+|\-|\/|\*)+[a-zA-Z0-9.]+\)+\;)
         * Final [a-zA-Z0-9]+\=+([a-zA-Z0-9.]+((\+|\/|\*|\-)+[a-zA-Z0-9.]+)*\;)
         * Real cuenta,numero,resultado;
         * Entero valor;
         * Leer(valor);
         * cuenta=23+(numero-valor);
         * numero=cuenta/123.99;
         * resultado=numero+cuenta;
         * Escribir(resultado);
         */

        // Recorre palabra por palabra encontrada
        for (int y = 0; y < letras.length; y++) {

            palabra += letras[y];
            posLectura++;
            // Para detectar el cambio de linea
            Matcher matcherCambioLinea = cambioLinea.matcher(codigoFuente);
            matcherCambioLinea.region(0, y);
            int lineaV = 1;
            while (matcherCambioLinea.find()) {
                lineaV++;
            }
            if (lineaV > linea) {
                linea = lineaV;
                posLectura = 0;
            }

            if (palabra.split("\\s").length > 0) {
                palabra = palabra.trim();

                char letra = letras[y];
                var x = ((letra + "").replace("", " ").trim());

                if (x.length() == 0 | x.equals(";")) {

                    if (palabras.length() == 0) {
                        palabras += palabra;
                    } else {
                        palabras += " " + palabra;
                    }
                    palabra = "";

                    Matcher matcherIniciarVariable = iniciarVariable.matcher(palabras);
                    Matcher matcherIniciarVariableConValor = iniciarVariableConValor.matcher(palabras);
                    Matcher matcherIniciarVariables = iniciarVariables.matcher(palabras);
                    Matcher matcherIniciarVariablesConValor = iniciarVariablesConValor.matcher(palabras);
                    Matcher matcherLeerEscribirVariables = leerOEscribirVariables.matcher(palabras);
                    Matcher matcherRealizaroperaciones = realizarOperacion.matcher(palabras);

                    if (matcherIniciarVariable.find()) {
                        // Es una asignación de una sola variable sin valor asignado.
                        // Ejemplo: Real variable;
                        String[] arregloPalabras = palabras.split(" ");
                        AgregarSimbolo(arregloPalabras[1].replace(";", ""), arregloPalabras[0], Id, 1, "" + linea,
                                "0");
                        palabras = "";
                        Id++;
                    }

                    if (matcherIniciarVariableConValor.find()) {
                        // Es una asignación de una sola variable con una valor asignado.
                        // Ejemplo: Real variable=12;
                        String[] arregloPalabras = palabras.split(" ");
                        String[] valorYNombre = arregloPalabras[1].split("=");
                        AgregarSimbolo(valorYNombre[0], arregloPalabras[0], Id, 1, "" + linea,
                                valorYNombre[1].replace(";", ""));
                        palabras = "";
                        Id++;
                    }

                    if (matcherIniciarVariables.find()) {
                        // Es una asignación de varias variables a la vez sin un valor inicial.
                        // Ejemplo: Real cuenta,numero,resultado;
                        String tipo = "";

                        if (palabras.contains("Real ")) {
                            palabras = palabras.replace("Real ", "");
                            tipo = "Real";
                        }
                        if (palabras.contains("Entero ")) {
                            palabras = palabras.replace("Entero ", "");
                            tipo = "Entero";
                        }
                        palabras = palabras.replace(";", "");

                        String[] arregloPalabras = palabras.split(",");
                        for (String variable : arregloPalabras) {
                            AgregarSimbolo(variable, tipo, Id, 1,
                                    "" + linea, "0");
                            Id += 1;
                        }
                        palabras = "";
                    }

                    if (matcherIniciarVariablesConValor.find()) {
                        // Es una asignación de varias variables a la vez con un valor inicial.
                        // Ejemplo: Entero x=1,y=2,z=0;
                        String tipo = "";

                        if (palabras.contains("Real ")) {
                            palabras = palabras.replace("Real ", "");
                            tipo = "Real";
                        }
                        if (palabras.contains("Entero ")) {
                            palabras = palabras.replace("Entero ", "");
                            tipo = "Entero";
                        }
                        palabras = palabras.replace(";", "");

                        String[] arregloPalabras = palabras.split(",");
                        for (String variable : arregloPalabras) {
                            String[] valorYNombre = variable.split("=");
                            AgregarSimbolo(valorYNombre[0], tipo, Id, 1,
                                    "" + linea, valorYNombre[1]);
                            Id += 1;
                        }
                        palabras = "";
                    }

                    if (matcherLeerEscribirVariables.find()) {
                        // Es para leer o escribir las variables
                        // Ejemplo: Leer(valor);
                        // Ejemplo: Escribir(valor);
                        palabras = palabras.replace("Leer", "");
                        palabras = palabras.replace("Escribir", "");
                        palabras = palabras.replace("(", "");
                        palabras = palabras.replace(")", "");
                        palabras = palabras.replace(";", "");

                        if (ComprobarToken(palabras) == false) {
                            GenerarError("No se encontro el token", palabras);
                        }
                        palabras = "";
                    }

                    if (matcherRealizaroperaciones.find()) {
                        // Es para validar operaciones
                        // cuenta=23+(numero-valor);
                        // numero=cuenta/123.99;
                        // resultado=numero+cuenta;

                        // Primero separamos la parte de la asignación
                        palabras = palabras.replace(";", "");
                        String[] asignacion = palabras.split("=");
                        String tipo = "";

                        if (ComprobarToken(asignacion[0]) == false) {
                            GenerarError("No se encontro el token al cual se le esta asignando", palabras);
                        } else {
                            Simbolo simbolo = VerSimbolo(asignacion[0]);
                            tipo = simbolo.tipo;
                        }

                        String operacionSeparada = "";
                        String parteOperacionSeparada = asignacion[1];

                        parteOperacionSeparada = parteOperacionSeparada.replace("+", " + ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("-", " - ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("/", " / ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("*", " * ");
                        operacionSeparada = operacionSeparada + parteOperacionSeparada;

                        String var = RecorridoPrefijo.realizarPrefijo(operacionSeparada);
                        operaciones.add(var);

                        Operacion operacionOp = new Operacion(operacionSeparada, var);
                        operacionesObjeto.add(operacionOp);

                        String operacion = asignacion[1];
                        operacion = operacion.replace("(", "");
                        operacion = operacion.replace(")", "");
                        operacion = operacion.replace(";", "");

                        operacion = operacion.replace("+", " ");
                        operacion = operacion.replace("-", " ");
                        operacion = operacion.replace("/", " ");
                        operacion = operacion.replace("*", " ");

                        String[] variables = operacion.split(" ");

                        for (String variable : variables) {
                            if (VerificarConstante(variable) == false) {
                                if (ComprobarToken(variable) == false) {
                                    GenerarError("No se encontro el token", variable);
                                } else {
                                    Simbolo simbolo = VerSimbolo(variable);
                                    if (tipo != simbolo.tipo) {
                                        GenerarError(
                                                "No se puede realizar la asignación si se utiliza una variable de diferente tipo al de la asignacion '"
                                                        + tipo + "' -> '" + simbolo.tipo + "'",
                                                variable);
                                    }
                                }
                            } else {
                                // Agregamos constante en la tabla de simbolos
                                // Expresión regular para String
                                // Expresión regular para float
                                String regexFloat = "-?\\d*\\.\\d+|-?\\d+\\.\\d*";
                                // Expresión regular para int
                                String regexInt = "-?\\d+";

                                // Validar float
                                if (Pattern.matches(regexFloat, variable)) {
                                    AgregarSimbolo(variable, "Real", IdConstantes, 1, "" + linea, variable);
                                    IdConstantes++;
                                }
                                // Validar int
                                else if (Pattern.matches(regexInt, variable)) {
                                    AgregarSimbolo(variable, "Entero", IdConstantes, 1, "" + linea, variable);
                                    IdConstantes++;
                                }
                                // Error
                                else {
                                    GenerarError("No se encontro el tipo de la variable 'Real' 'Entero' ",
                                            variable);
                                }

                            }
                        }
                        palabras = "";
                    }

                }
            }
        }

        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%70s", "Expresiónes prefijas: ");
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        for (int y = 0; y < operaciones.size(); y++) {
            String operacion = operaciones.get(y);
            System.out.println(operacion);
        }
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        // Imprimimos tabla de errores
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%70s", "Tabla de errores");
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.format("%-30s %-30s %-30s%n", "Error", "Variable", "Linea");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        for (int x = 0; x < tablaErrores.size(); x++) {
            Errores oErrores = tablaErrores.get(x);

            // Creamos un arreglo de String para poder dividir la linea
            String[] lineasTexto = dividirTexto(oErrores.textoE, 30);

            for (int i = 0; i < lineasTexto.length; i++) {
                String texto = lineasTexto[i];

                // Imprimir las columnas solo en la primera línea
                if (i == 0) {
                    System.out.format("%-30s %-30s %-30s%n", texto, oErrores.variable, oErrores.linea);
                } else {
                    System.out.format("%-30s %-30s %-30s%n", texto, "", "");
                }
            }
        }
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        // Imprimimos la tabla de simbolos.
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%70s", "Tabla de simbolos");
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%10s %10s %10s %10s %10s %10s", "Token", "Tipo", "Id", "Repeticiones", "Linea", "Valor");
        System.out.println();
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        for (

                int x = 0; x < tablaSimbolos.size(); x++) {
            Simbolo oSimbolo = tablaSimbolos.get(x);
            System.out.format("%10s %10s %10s %10s %10s %10s", oSimbolo.token, oSimbolo.tipo, oSimbolo.idToken,
                    oSimbolo.repeticion, oSimbolo.linea, oSimbolo.valor);
            System.out.println();
        }
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------");

        AnalisisObjeto analisisObjeto = new AnalisisObjeto(operacionesObjeto, tablaSimbolos);
        analisisObjeto.Generar();
    }

    public boolean ComprobarToken(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                AñadirRepeticion(token);
                return true;
            }
        }
        return false;
    }

    public void AñadirRepeticion(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                simbolo.repeticion++;
                simbolo.linea = simbolo.linea + "," + linea;
            }
        }
    }

    public void GenerarError(String error, String palabra) {

        AgregarError(error, palabra, "" + linea);

        System.out.println();
        System.out.format("%10s %10s %10s %10s",
                " \033[31mError " + error + ": \033[0m" + palabra, "Linea " + linea,
                " Inicia " + (posLectura - 1), " Termina " + (posLectura - 1 + palabra.length()));
        System.out.println();

        return;
    }

    public boolean VerificarConstante(String constante) {
        Pattern constantes = Pattern
                .compile("-?\\d+(\\.\\d+)?");
        Matcher matcherConstantes = constantes.matcher(constante);
        if (matcherConstantes.find()) {
            return true;
        } else {
            return false;
        }
    }

    public Simbolo VerSimbolo(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                return simbolo;
            }
        }
        return null;
    }

    public void EscribirSimbolo(Simbolo oSimbolo) {
        for (int x = 0; x < tablaSimbolos.size(); x++) {
            if (tablaSimbolos.get(x).token.equals(oSimbolo.token)) {
                tablaSimbolos.set(x, oSimbolo);
            }
        }
    }

    public void AgregarSimbolo(String token, String tipo, int idToken, int repeticiones, String linea,
            String valor) {
        Simbolo oSimbolo = new Simbolo();
        oSimbolo.token = token;
        oSimbolo.tipo = tipo;
        oSimbolo.idToken = idToken;
        oSimbolo.repeticion = repeticiones;
        oSimbolo.linea = linea;
        oSimbolo.valor = valor;
        tablaSimbolos.add(oSimbolo);
    }

    public void AgregarError(String textoE, String variable, String linea) {
        Errores oErrores = new Errores();

        oErrores.textoE = textoE;
        oErrores.variable = variable;
        oErrores.linea = linea;
        tablaErrores.add(oErrores);
    }

    public static String[] dividirTexto(String texto, int longitudMaxima) {
        return texto != null ? texto.replaceAll("(.{1," + longitudMaxima + "})(\\s+|$)", "$1\n").split("\n")
                : new String[] {};
    }
}