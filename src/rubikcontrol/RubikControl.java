/*
 * Todo el proyecto tiene derechos de copyright asignados a:
 * Fernando Pérez Gutiérrez, el único creador del programa.
 *
 * Este es un programa de distribución gratuita y para todos los
 * públicos, pero, esta prohibida su venta y la modificación total
 * o parcial del código aquí disponible.
 *
 * Está prohibido crear otro programa copiando de manera total o parcial
 * el código de este mismo sin consentimiento del autor.
 * Para solicitar permiso contacten a: thecorplay@gmail.com
 */
package rubikcontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
import rubikcontrol.commons.Tiempo;
import rubikcontrol.window.Starter;

/**
 *
 * @author Fernando
 */
public class RubikControl {
//    private static final String direccion = "C:/Users/Fernando/Desktop/doc.txt";
    private static String direccion = "c:/Users/";
    private static Starter starter;
    /*
        Mapa que va del cubo que estamos mirando a:
            Mapa de sesión que miremos a:
                Lista de Tiempos
    */
    private static Map <String, Map <Integer, DefaultListModel <String> > > cubosSesion;
    /*
        Mapa que va del cubo que estamos mirando a:
            Mejor tiempo en milesimas
    */
    private static Map <String, Integer> cubosRecord;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        cubosSesion = new HashMap();
        initCubos();
        cubosRecord = new HashMap();
        
        direccion = direccionCargaGuardado();
        
        starter = new Starter();
        
        cargar();
        starter.setList(cubosSesion.get("2x2").get(1));
    }
    
    public static String direccionCargaGuardado() {
        String myDocuments = null;

        try {
            Process p =  Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
            p.waitFor();

            InputStream in = p.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();

            myDocuments = new String(b);
            myDocuments = myDocuments.split("\\s\\s+")[4];

        } catch(Throwable t) {
            t.printStackTrace();
        }
        
        File folder = new File(myDocuments + "\\RubikControl");
        if (!folder.exists())
            folder.mkdir();
        
        return myDocuments + "\\RubikControl\\times.txt";
    }
    
    public static void cargar() {
        try {
            FileReader reader = new FileReader(direccion);
            try (BufferedReader buffer = new BufferedReader(reader)) {
                String cubo = buffer.readLine();
                while (cubo != null) {
                    if (!cubo.equals("2x2") && !cubo.equals("3x3") && !cubo.equals("4x4") && !cubo.equals("5x5")) {
                        starter.addCubo(cubo);
                    }
                    
                    String [] line = buffer.readLine().split (" ");
                    Map <Integer, DefaultListModel <String> > mapaSesion = new HashMap();
                    
                    while (!line[0].equals("-")) {
                        int sesion = Integer.parseInt(line[0]);
                        
                        if (cubo.equals("2x2"))
                            starter.actualizarSesiones(sesion);
                        
                        DefaultListModel <String> lista = new DefaultListModel ();
                        
                        for (int i = 1; i < line.length; i++) {
                            lista.addElement(line[i]);
                            
                            int tiempo = transformarText(line[i]);
                            if (!cubosRecord.containsKey(cubo) || tiempo < cubosRecord.get(cubo)) {
                                cubosRecord.put(cubo, tiempo);
                                if (cubo.equals("2x2"))
                                    starter.setRecord(tiempo);
                            }
                        }
                        
                        mapaSesion.put(sesion, lista);
                        cubosSesion.put(cubo, mapaSesion);
                        line = buffer.readLine().split (" ");
                    }
                    
                    cubo = buffer.readLine();
                }
            }
        } catch (IOException ex) {
            // Si no lo encuentra, ya terminó de cargar (no hay nada)
        }
    }
    
    private static int transformarText(String time) {
        String [] tiempo = time.split(":");
        
        return  Integer.parseInt(tiempo[0])*60000 +
                Integer.parseInt(tiempo[1])*1000 +
                Integer.parseInt(tiempo[2]);
        
    }
    
    public static void guardar () {
        try {
            FileWriter writer = new FileWriter (direccion);
            try (BufferedWriter buffer = new BufferedWriter(writer)) {
                final Iterator <Entry<String, Map<Integer, DefaultListModel<String> > > > it = cubosSesion.entrySet().iterator();
                Entry<String, Map<Integer, DefaultListModel<String> > > entry;
                
                String claveOut;
                Map<Integer, DefaultListModel<String> > valorOut;
                
                while (it.hasNext()) {
                    entry = it.next();
                    claveOut = entry.getKey();
                    if (claveOut != null) {
                        buffer.write(claveOut);
                        buffer.newLine();

                        valorOut = entry.getValue();
                        // Ahora toca recorrer todo el valor
                        final Iterator <Entry <Integer, DefaultListModel<String> > > itIn = valorOut.entrySet().iterator();
                        Entry<Integer, DefaultListModel<String> > entryIn;

                        Integer claveIn;
                        DefaultListModel<String> valorIn;

                        while (itIn.hasNext()) {
                            entryIn = itIn.next();
                            claveIn = entryIn.getKey();
                            buffer.write(claveIn + " ");

                            valorIn = entryIn.getValue();
                            // Ahora toca recorre toda la lista del valor
                            for (int i = 0; i < valorIn.size(); i++) {
                                buffer.write (valorIn.get(i) + " ");
                            }
                            buffer.newLine();
                        }
                        buffer.write("-");
                        buffer.newLine();
                    }
                }
                buffer.flush();
            }

        } catch (IOException ex) {
            // No guardamos y listo
        }
    }
    
    private static void initCubos() {
        Map <Integer, DefaultListModel <String> > aux = new HashMap();
        DefaultListModel <String> list = new DefaultListModel ();
        aux.put(1, list);
        
        cubosSesion.put("2x2", aux);
    }
    
    public static void addTime(String cubo, int sesion, Tiempo time) {
        if (cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
            
            if (sesionAux.containsKey(sesion)) {
                DefaultListModel <String> listaAux = sesionAux.get(sesion);
                
                listaAux.addElement(time.toString());
                starter.setList(listaAux);
                int tiempo = transformarText(time.toString());
                if (!cubosRecord.containsKey(cubo) || tiempo < cubosRecord.get(cubo)) {
                    cubosRecord.put(cubo, tiempo);
                    starter.setRecord(tiempo);
                }
            }
            else {
                DefaultListModel <String> listaAux = new DefaultListModel<String> ();
                // Añadimos el tiempo a la lista de tiempos auxiliar 
                listaAux.addElement(time.toString());
                // Añadimos la lista de tiempos auxiliar al mapa de sesiones
                sesionAux.put(sesion,listaAux);
                // Ponemos el cambio en la lista
                starter.setList(listaAux);
                int tiempo = transformarText(time.toString());
                if (!cubosRecord.containsKey(cubo) || tiempo < cubosRecord.get(cubo)) {
                    cubosRecord.put(cubo, tiempo);
                    starter.setRecord(tiempo);
                }
            }
        }
        else {
            Map <Integer, DefaultListModel <String> > sesionAux = new HashMap();
            DefaultListModel <String> listaAux = new DefaultListModel<String> ();
            // Añadimos el tiempo a la lista de tiempos auxiliar 
            listaAux.addElement(time.toString());
            // Añadimos la lista de tiempos auxiliar al mapa de sesiones auxiliar
            sesionAux.put(sesion, listaAux);
            // Añadimos el mapa de sesiones auxiliar al mapa de cubos
            cubosSesion.put(cubo, sesionAux);
            // Ponemos el cambio en la lista
            starter.setList(listaAux);
            int tiempo = transformarText(time.toString());
            if (!cubosRecord.containsKey(cubo) || tiempo < cubosRecord.get(cubo)) {
                cubosRecord.put(cubo, tiempo);
                starter.setRecord(tiempo);
            }
        }
        actualizaMediaSesion(cubo, sesion);
        actualizaMediaCubo(cubo, starter.ultimaSesion());
    }
    
    public static void actualizaMediaSesion(String cubo, int sesion) {
        Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
        DefaultListModel <String> listaAux = sesionAux.get(sesion);
        int minutos = 0, segundos = 0, milesimas = 0;
        for (int i = 0; i < listaAux.size(); i++) {
            String elem = listaAux.get(i);
            String[] vector = elem.split(":");
            minutos     += Integer.parseInt(vector[0]);
            segundos    += Integer.parseInt(vector[1]);
            milesimas   += Integer.parseInt(vector[2]);
        }
        // Recolocamos los datos en milesimas
        while (minutos > 0) {
            minutos--;
            milesimas += 60000;
        }
        while (segundos > 0) {
            segundos--;
            milesimas += 1000;
        }
        if (listaAux.size() != 0) {
            milesimas = milesimas/listaAux.size();
            // Volvemos a minutos segundos y milesimas
            while (milesimas >= 1000) {
                milesimas -= 1000;
                segundos++;
                if (segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
            }
            
            starter.setMediaSesion(minutos,segundos,milesimas);
        } else {
            starter.setMediaSesion(0,0,0);
        }
    }
    
    public static void actualizaUltimosSesion (String cubo, int sesion, int ultimos) {
        Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
        DefaultListModel <String> listaAux = sesionAux.get(sesion);
        int minutos = 0, segundos = 0, milesimas = 0;
        
        if (listaAux == null) {
            DefaultListModel <String> aux = new DefaultListModel();
            sesionAux.put(sesion, aux);
            listaAux = sesionAux.get(sesion);
        }
        
        for (int i = (listaAux.size() > ultimos)? listaAux.size()-ultimos:0; i < listaAux.size(); i++) {
            String elem = listaAux.get(i);
            String[] vector = elem.split(":");
            minutos     += Integer.parseInt(vector[0]);
            segundos    += Integer.parseInt(vector[1]);
            milesimas   += Integer.parseInt(vector[2]);
        }
        
        // Recolocamos los datos en milesimas
        while (minutos > 0) {
            minutos--;
            milesimas += 60000;
        }
        while (segundos > 0) {
            segundos--;
            milesimas += 1000;
        }
        
        if (ultimos != 0) {
            if (listaAux.size() > ultimos) {
                milesimas = milesimas/ultimos;
            }
            else if (listaAux.size() != 0) {
                milesimas = milesimas/listaAux.size();
            }
            
            // Volvemos a minutos segundos y milesimas
            while (milesimas >= 1000) {
                milesimas -= 1000;
                segundos++;
                if (segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
            }
            
            starter.setUltimosSesion(minutos,segundos,milesimas);
        } else {
            starter.setUltimosSesion(0,0,0);
        }
    }
    
    public static void actualizaMediaCubo (String cubo, int ultimaSesion) {
        Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
        int minutos = 0, segundos = 0, milesimas = 0, total = 0;
        for (int i = ultimaSesion; i > 0; i--) {
            DefaultListModel <String> listaAux = sesionAux.get(i);
            
            for (int j = 0; j < listaAux.size(); j++) {
                String elem = listaAux.get(j);
                String[] vector = elem.split(":");
                minutos     += Integer.parseInt(vector[0]);
                segundos    += Integer.parseInt(vector[1]);
                milesimas   += Integer.parseInt(vector[2]);
                total++;
            }
        }
        
        // Lo pasamos a milesimas
        while (minutos > 0) {
            minutos--;
            milesimas += 60000;
        }
        while (segundos > 0) {
            segundos--;
            milesimas += 1000;
        }
        
        // Calculamos entre el total
        if (total != 0) {
            milesimas = milesimas/total;
            // Reconvertimos a minutos, segundos y milesimas
            while (milesimas >= 1000) {
                segundos++;
                milesimas-=1000;
                if (segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
            }
            // Mandamos la actualización
            starter.setMediaCubo(minutos, segundos, milesimas);
        } else {
            starter.setMediaCubo(0,0,0);
        }
    }
    
    public static void actualizaUltimosCubo (String cubo, int ultimaSesion, int ultimos) {
        Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
        int minutos = 0, segundos = 0, milesimas = 0, total = 0;
        for (int i = ultimaSesion; i > 0 && total < ultimos; i--) {
            DefaultListModel <String> listaAux = sesionAux.get(i);
            
            for (int j = listaAux.size() - 1; j >= 0 && total < ultimos; j--) {
                String elem = listaAux.get(j);
                String[] vector = elem.split(":");
                minutos     += Integer.parseInt(vector[0]);
                segundos    += Integer.parseInt(vector[1]);
                milesimas   += Integer.parseInt(vector[2]);
                total++;
            }
        }
        
        // Lo pasamos a milesimas
        while (minutos > 0) {
            minutos--;
            milesimas += 60000;
        }
        while (segundos > 0) {
            segundos--;
            milesimas += 1000;
        }
        
        // Calculamos entre el total
        if (total != 0) {
            milesimas = milesimas/total;
            // Reconvertimos a minutos, segundos y milesimas
            while (milesimas >= 1000) {
                segundos++;
                milesimas-=1000;
                if (segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
            }
            // Mandamos la actualización
            starter.setUltimosCubo(minutos, segundos, milesimas);
        } else {
            starter.setUltimosCubo(0,0,0);
        }
    }
    
    public static void changeList (String cubo, int sesion) {
        if (cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
            
            if (sesionAux.containsKey(sesion)) {
                starter.setList(sesionAux.get(sesion));
                return;
            }
        }
        actualizaMediaSesion(cubo, sesion);
        actualizaMediaCubo(cubo, starter.ultimaSesion());
        starter.setList(new DefaultListModel<String>());
    }
    
    public static int numSesiones(String cubo) {
        if (cubosSesion.containsKey(cubo)) {
            //System.out.println(cubosSesion.get(cubo).size());
            return cubosSesion.get(cubo).size();
        }
        else {
            Map <Integer, DefaultListModel <String> > sesionAux = new HashMap();
            DefaultListModel <String> listaAux = new DefaultListModel<String> ();
            sesionAux.put(1, listaAux);
            cubosSesion.put(cubo, sesionAux);
            return 1;
        }
    }
    
    public static void addSesion (String cubo, int sesion) {
        if (cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > sesionAux =cubosSesion.get(cubo);
            if (!sesionAux.containsKey(sesion)) {
                DefaultListModel <String> listaAux = new DefaultListModel<String> ();
                // Añadimos la lista de tiempos auxiliar al mapa de sesiones
                sesionAux.put(sesion,listaAux);
                // Ponemos el cambio en la lista
                starter.setList(listaAux);
            }
        }
        actualizaMediaSesion(cubo, sesion);
    }
    
    public static boolean addCubo(String cubo) {
        if (!cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > sesionAux = new HashMap();
            DefaultListModel <String> listaAux = new DefaultListModel<String>();
            sesionAux.put(1, listaAux);
            cubosSesion.put(cubo, sesionAux);
            actualizaMediaCubo(cubo, 1);
            actualizaUltimosCubo(cubo, 1, 0);
            actualizaMediaSesion(cubo, 1);
            return true;
        }
        return false;
    }
    
    public static boolean existeSesion(String cubo, int sesion) {
        if (cubosSesion.containsKey(cubo))
            return cubosSesion.get(cubo).containsKey(sesion);
        
        return false;
    }
    
    public static void eliminarTiempo (String cubo, int sesion, int posList) {
        if (cubosSesion.containsKey(cubo) && cubosSesion.get(cubo).containsKey(sesion)) {
            cubosSesion.get(cubo).get(sesion).remove(posList);
            actualizaMediaSesion(cubo,sesion);
            actualizaMediaCubo(cubo, starter.ultimaSesion());
        }
    }
    
    public static void getRecord (String cubo) {
        if (cubosRecord.containsKey(cubo)) {
            starter.setRecord(cubosRecord.get(cubo));
        } else {
            starter.setRecord(0);
        }
    }
    
    public static void actualizaRecord (String cubo) {
        boolean cambio = false;
        if (cubosSesion.containsKey(cubo)) {
            final Iterator <Entry <Integer, DefaultListModel<String> > > itIn = cubosSesion.get(cubo).entrySet().iterator();
            Entry<Integer, DefaultListModel<String> > entryIn;

            Integer claveIn;
            DefaultListModel<String> valorIn;

            while (itIn.hasNext()) {
                entryIn = itIn.next();
                claveIn = entryIn.getKey();

                valorIn = entryIn.getValue();
                if (valorIn.size()>0) {
                    if (!cambio) {
                        cubosRecord.put(cubo, transformarText(valorIn.get(0)));
                        cambio = true;
                    }
                    // Ahora toca recorre toda la lista del valor
                    for (int i = 1; i < valorIn.size(); i++) {
                        if (transformarText(valorIn.get(i)) < cubosRecord.get(cubo)) {
                            cubosRecord.put(cubo, transformarText(valorIn.get(i)));
                        }
                    }
                }
            }
            
            if (!cambio) {
                starter.setRecord(0);
                cubosRecord.remove(cubo);
            }
            else {
                starter.setRecord(cubosRecord.get(cubo));
            }
        }
        else {
            starter.setRecord(0);
        }
    }
    
    public static void resetearCubo (String cubo) {
//        cubosSesion.remove(cubo);
        if (cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > sesion = new HashMap();
            DefaultListModel <String> lista = new DefaultListModel();
            sesion.put(1, lista);
            cubosSesion.put(cubo, sesion);
            changeList(cubo,1);
            actualizaRecord(cubo);
            //System.out.println("Tamaño de la sesion al resetear: "+cubosSesion.get(cubo).size());
        }
    }
    
    public static void resetearSesion (String cubo, int sesion) {
        if (cubosSesion.containsKey(cubo)) {
            Map <Integer, DefaultListModel <String> > mapSesion = cubosSesion.get(cubo);
            
            if (mapSesion.containsKey(sesion)) {
                DefaultListModel <String> lista = new DefaultListModel();
                mapSesion.put(sesion, lista);
                cubosSesion.put(cubo,mapSesion);
                starter.setList(lista);
                
                actualizaMediaSesion(cubo, sesion);
                actualizaMediaCubo(cubo, starter.ultimaSesion());
            }
        }
    }
    
    public static void resetearPrograma () {
        cubosSesion = new HashMap();
        initCubos();
        cubosRecord = new HashMap();
        
        starter.setList(cubosSesion.get("2x2").get(1));
    }
}
