import java.io.BufferedWriter;//Libreria para escribir el archivo
import java.io.FileWriter;//Libreria del archivo
import java.io.IOException;//Libreria de exepciones del programa

public class Generacion {
        public static void GenerarCodigoIntermedio(String contenido) {
                // Ruta del archivo de texto que se va a crear
                String rutaCompletaArchivo = System.getProperty("user.dir") + "\\Resultados.txt";
                try {
                        // Crear un objeto FileWriter para escribir en el archivo
                        FileWriter fileWriter = new FileWriter(rutaCompletaArchivo);
                        // Crear un objeto BufferedWriter para escribir de manera eficiente
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        // Escribir el contenido en el archivo
                        bufferedWriter.write(contenido);
                        // Cerrar el BufferedWriter (esto también cerrará el FileWriter)
                        bufferedWriter.close();
                        System.out.println("Se ha creado el archivo exitosamente.");
                } catch (IOException e) {
                        System.err.println("Error al crear el archivo: " + e.getMessage());
                }
        }
}
