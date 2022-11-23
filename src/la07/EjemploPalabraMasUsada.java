package la07;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

// ============================================================================
class EjemploPalabraMasUsada {
// ============================================================================

    // -------------------------------------------------------------------------
    public static void main(String args[]) {
        long t1, t2;
        double tt;
        int numHebras;
        String nombreFichero, palabraActual;
        Vector<String> vectorLineas;
        HashMap<String, Integer> hmCuentaPalabras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <fichero>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            nombreFichero = args[1];
        } catch (NumberFormatException ex) {
            numHebras = -1;
            nombreFichero = "";
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        // Lectura y carga de lineas en "vectorLineas".
        vectorLineas = readFile(nombreFichero);
        System.out.println("Numero de lineas leidas: " + vectorLineas.size());
        System.out.println();

        //
        // Implementacion secuencial sin temporizar.
        //
        hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
        for (int i = 0; i < vectorLineas.size(); i++) {
            // Procesa la linea "i".
            String[] palabras = vectorLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blanco.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra(hmCuentaPalabras, palabraActual);
                }
            }
        }
        //
        // Implementacion secuencial.
        //
        t1 = System.nanoTime();
        hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
        for (int i = 0; i < vectorLineas.size(); i++) {
            // Procesa la linea "i".
            String[] palabras = vectorLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blanco.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra(hmCuentaPalabras, palabraActual);
                }
            }
        }
        t2 = System.nanoTime();
        tt = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion secuencial: ");
        imprimePalabraMasUsadaYVeces(hmCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt);
        System.out.println("Num. elems. tabla hash: " + hmCuentaPalabras.size());
        System.out.println();


        //
        // Implementacion paralela 1: Uso de synchronizedMap.
        //
        int vectorLength = vectorLineas.size();
        t1 = System.nanoTime();
        HashMap<String, Integer> maCuentaPalabras = new HashMap<>(1000, 0.75F);
        MiHebra_1[] vecMihebra1 = new MiHebra_1[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra1[i] = new MiHebra_1(maCuentaPalabras, i, numHebras, vectorLength, vectorLineas);
            vecMihebra1[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra1[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt1 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 1: ");
        imprimePalabraMasUsadaYVeces(maCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt1 + " , Incremento " + tt / tt1);
        System.out.println("Num. elems. tabla hash: " + maCuentaPalabras.size());
        System.out.println();

        //
        // Implementacion paralela 2: Uso de Hashtable.
        t1 = System.nanoTime();
        Hashtable<String, Integer> htCuentaPalabras = new Hashtable<>(1000, 0.75F);
        MiHebra_2[] vecMihebra2 = new MiHebra_2[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra2[i] = new MiHebra_2(htCuentaPalabras, i, numHebras, vectorLength, vectorLineas);
            vecMihebra2[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra2[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt2 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 2: ");
        imprimePalabraMasUsadaYVeces(htCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt2 + " , Incremento " + tt / tt2);
        System.out.println("Num. elems. tabla hash: " + htCuentaPalabras.size());
        System.out.println();

        //
        // Implementacion paralela 3: Uso de ConcurrentHashMap
        t1 = System.nanoTime();
        ConcurrentHashMap<String, Integer> chmsCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);
        MiHebra_3[] vecMihebra3 = new MiHebra_3[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra3[i] = new MiHebra_3(chmsCuentaPalabras, i, numHebras, vectorLength, vectorLineas);
            vecMihebra3[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra3[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt3 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 3: ");
        imprimePalabraMasUsadaYVeces(chmsCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt3 + " , Incremento " + tt / tt3);
        System.out.println("Num. elems. tabla hash: " + chmsCuentaPalabras.size());
        System.out.println();
        //
        // Implementacion paralela 4: Uso de ConcurrentHashMap
        //
        t1 = System.nanoTime();
        ConcurrentHashMap<String, Integer> chmCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);
        MiHebra_4[] vecMihebra4 = new MiHebra_4[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra4[i] = new MiHebra_4(chmCuentaPalabras, i, numHebras, vectorLength, vectorLineas);
            vecMihebra4[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra4[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt4 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 4: ");
        imprimePalabraMasUsadaYVeces(chmCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt4 + " , Incremento " + tt / tt4);
        System.out.println("Num. elems. tabla hash: " + chmCuentaPalabras.size());
        System.out.println();

        //
        // Implementacion paralela 5: Uso de ConcurrentHashMap
        //
        t1 = System.nanoTime();
        ConcurrentHashMap<String, AtomicInteger> chmaCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);
        MiHebra_5[] vecMihebra5 = new MiHebra_5[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra5[i] = new MiHebra_5(chmaCuentaPalabras, i, numHebras, vectorLength, vectorLineas);
            vecMihebra5[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra5[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt5 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 5: ");
        HashMap<String, Integer> printablechmaCuentaPalabras = new HashMap<>();
        for (String clave : chmaCuentaPalabras.keySet()) {
            printablechmaCuentaPalabras.put(clave, chmaCuentaPalabras.get(clave).intValue());
        }
        imprimePalabraMasUsadaYVeces(printablechmaCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt5 + " , Incremento " + tt / tt5);
        System.out.println("Num. elems. tabla hash: " + chmaCuentaPalabras.size());
        System.out.println();

        //
        // Implementacion paralela 6: Uso de ConcurrentHashMap
        //
        t1 = System.nanoTime();
        ConcurrentHashMap<String, AtomicInteger> chmaCuentaPalabras2 = new ConcurrentHashMap<>(1000, 0.75F, 256);
        MiHebra_6[] vecMihebra6 = new MiHebra_6[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vecMihebra6[i] = new MiHebra_6(chmaCuentaPalabras2, i, numHebras, vectorLength, vectorLineas);
            vecMihebra6[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vecMihebra6[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        double tt6 = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implementacion paralela 6: ");
        HashMap<String, Integer> printablechmaCuentaPalabras2 = new HashMap<>();
        for (String clave : chmaCuentaPalabras2.keySet()) {
            printablechmaCuentaPalabras2.put(clave, chmaCuentaPalabras2.get(clave).intValue());
        }
        imprimePalabraMasUsadaYVeces(printablechmaCuentaPalabras2);
        System.out.println(" Tiempo(s): " + tt6 + " , Incremento " + tt / tt6);
        System.out.println("Num. elems. tabla hash: " + chmaCuentaPalabras2.size());
        System.out.println();


        //
        // Implementacion paralela 7: Uso de Streams
        t1 = System.nanoTime();
        Map<String, Long> stCuentaPalabras = vectorLineas.parallelStream()
                .filter(s -> s != null)
                .map(s -> s.split("\\W+"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(s -> (s.length() > 0))
                .collect(groupingBy(s -> s, counting()));
        t2 = System.nanoTime();
        double tt7 = ((double) (t2-t1))/ 1.0e9;
        System.out.print("Implementacion paralela 7: ");
        Map<String,Integer> printablestCuentaPalabras = new HashMap<>();
        for (String clave : stCuentaPalabras.keySet()){
            Long valor = stCuentaPalabras.get(clave);
            if (valor != null){
                printablestCuentaPalabras.put(clave, valor.intValue());
            }
        }
        imprimePalabraMasUsadaYVeces(printablestCuentaPalabras);
        System.out.println(" Tiempo(s): " + tt7 + " , Incremento " + tt/tt7);
        System.out.println("Num. elems. tabla hash: " + stCuentaPalabras.size());
        System.out.println();

        System.out.println("Fin de programa.");
    }

    // -------------------------------------------------------------------------
    public static Vector<String> readFile(String fileName) {
        BufferedReader br;
        String linea;
        Vector<String> data = new Vector<String>();

        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((linea = br.readLine()) != null) {
                //// System.out.println( "Leida linea: " + linea );
                data.add(linea);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    // -------------------------------------------------------------------------
    public static void contabilizaPalabra(
            HashMap<String, Integer> cuentaPalabras,
            String palabra) {
        Integer numVeces = cuentaPalabras.get(palabra);
        if (numVeces != null) {
            cuentaPalabras.put(palabra, numVeces + 1);
        } else {
            cuentaPalabras.put(palabra, 1);
        }
    }

    // --------------------------------------------------------------------------
    static void imprimePalabraMasUsadaYVeces(
            Map<String, Integer> cuentaPalabras) {
        Vector<Map.Entry> lista =
                new Vector<Map.Entry>(cuentaPalabras.entrySet());

        String palabraMasUsada = "";
        int numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for (int i = 0; i < lista.size(); i++) {
            String palabra = (String) lista.get(i).getKey();
            int numVeces = (Integer) lista.get(i).getValue();
            if (i == 0) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if (numVecesPalabraMasUsada < numVeces) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print("( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )");
    }

    // --------------------------------------------------------------------------
    static void printCuentaPalabrasOrdenadas(
            HashMap<String, Integer> cuentaPalabras) {
        int i, numVeces;
        List<Map.Entry> list = new Vector<Map.Entry>(cuentaPalabras.entrySet());

        // Ordena por valor.
        Collections.sort(
                list,
                new Comparator<Map.Entry>() {
                    public int compare(Map.Entry e1, Map.Entry e2) {
                        Integer i1 = (Integer) e1.getValue();
                        Integer i2 = (Integer) e2.getValue();
                        return i2.compareTo(i1);
                    }
                }
        );
        // Muestra contenido.
        i = 1;
        System.out.println("Veces Palabra");
        System.out.println("-----------------");
        for (Map.Entry e : list) {
            numVeces = ((Integer) e.getValue()).intValue();
            System.out.println(i + " " + e.getKey() + " " + numVeces);
            i++;
        }
        System.out.println("-----------------");
    }
}

class MiHebra_1 extends Thread {
    Map<String, Integer> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;


    public MiHebra_1(HashMap<String, Integer> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = Collections.synchronizedMap(cuentaPalabras);
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        for (int i = miId; i < vectorLength; i += numHebras) {
            String[] palabras = lineasFich.get(i).split("\\W+");

            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_1(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_1(String palabra) {
        synchronized (cuentaPalabras) {
            Integer numVeces = cuentaPalabras.get(palabra);
            if (numVeces != null) {
                cuentaPalabras.replace(palabra, numVeces + 1);
            } else {
                cuentaPalabras.put(palabra, 1);
            }
        }


    }
}

class MiHebra_2 extends Thread {
    Hashtable<String, Integer> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;


    public MiHebra_2(Hashtable<String, Integer> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = cuentaPalabras;
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        for (int i = miId; i < vectorLength; i += numHebras) {
            String[] palabras = lineasFich.get(i).split("\\W+");

            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_2(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_2(String palabra) {
        synchronized (cuentaPalabras) {
            Integer numVeces = cuentaPalabras.get(palabra);
            if (numVeces != null) {
                cuentaPalabras.put(palabra, numVeces + 1);
            } else {
                cuentaPalabras.put(palabra, 1);
            }
        }


    }
}

class MiHebra_3 extends Thread {
    ConcurrentHashMap<String, Integer> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;

    public MiHebra_3(ConcurrentHashMap<String, Integer> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = cuentaPalabras;
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        for (int i = miId; i < vectorLength; i += numHebras) {
            String[] palabras = lineasFich.get(i).split("\\W+");

            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_3(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_3(String palabra) {
        synchronized (cuentaPalabras) {
            Integer numVeces = cuentaPalabras.get(palabra);
            if (numVeces != null) {
                cuentaPalabras.put(palabra, numVeces + 1);
            } else {
                cuentaPalabras.put(palabra, 1);
            }
        }

    }
}

class MiHebra_4 extends Thread {
    ConcurrentHashMap<String, Integer> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;

    public MiHebra_4(ConcurrentHashMap<String, Integer> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = cuentaPalabras;
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        String[] palabras;
        for (int i = miId; i < vectorLength; i += numHebras) {
            palabras = lineasFich.get(i).split("\\W+");


            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_4(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_4(String palabra) {
        boolean sustit;

        Integer valIni = cuentaPalabras.putIfAbsent(palabra, 1);

        if (valIni != null) {
            int valAct = valIni;

            while (true) {
                sustit = cuentaPalabras.replace(palabra, valAct, valAct + 1);

                if (sustit) break;
                valAct = cuentaPalabras.get(palabra);
            }
        }
    }
}

class MiHebra_5 extends Thread {
    ConcurrentHashMap<String, AtomicInteger> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;

    public MiHebra_5(ConcurrentHashMap<String, AtomicInteger> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = cuentaPalabras;
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        String[] palabras;
        for (int i = miId; i < vectorLength; i += numHebras) {
            palabras = lineasFich.get(i).split("\\W+");


            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_5(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_5(String palabra) {
        boolean sustit;

        AtomicInteger valIni = cuentaPalabras.putIfAbsent(palabra, new AtomicInteger(1));

        if (valIni != null) {
            valIni.incrementAndGet();
        }
    }
}

class MiHebra_6 extends Thread {
    ConcurrentHashMap<String, AtomicInteger> cuentaPalabras;

    int miId, numHebras, vectorLength;

    Vector<String> lineasFich;

    public MiHebra_6(ConcurrentHashMap<String, AtomicInteger> cuentaPalabras, int miId, int numHebras, int vectorLength, Vector<String> lineasFich) {
        this.cuentaPalabras = cuentaPalabras;
        this.miId = miId;
        this.numHebras = numHebras;
        this.lineasFich = lineasFich;
        this.vectorLength = vectorLength;
    }

    public void run() {
        String palabraActual = "";
        String[] palabras;
        for (int i = miId; i < vectorLength; i += numHebras) {
            palabras = lineasFich.get(i).split("\\W+");


            for (String palabra : palabras) {
                palabraActual = palabra.trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra_6(palabraActual);
                }
            }
        }
    }

    void contabilizaPalabra_6(String palabra) {
        boolean sustit;

        AtomicInteger valIni = cuentaPalabras.putIfAbsent(palabra, new AtomicInteger(1));

        if (valIni != null) {
            valIni.incrementAndGet();
        }
    }
}