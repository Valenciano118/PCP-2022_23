package la02.ej2;

class MiHebraBloques extends Thread {
    double[] vectorX, vectorY;
    int miId, numHebras;

    public MiHebraBloques(double[] vectorX, double[] vectorY, int miId, int numHebras) {
        this.vectorY = vectorY;
        this.vectorX = vectorX;
        this.miId = miId;
        this.numHebras = numHebras;
    }

    static double evaluaFuncion(double x) {
        return Math.sin(Math.exp(-x) + Math.log1p(x));
    }

    public void run() {
        int tamBloq= (vectorX.length+numHebras-1)/numHebras;
        int inicio = tamBloq * miId;
        int fin = Math.min(inicio+tamBloq, vectorX.length);
        for (int i = inicio; i < fin; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }
    }
}

// ============================================================================
class EjemploFuncionCostosaBloques {
// ============================================================================

    // --------------------------------------------------------------------------
    public static void main(String args[]) {
        int n, numHebras;
        long t1, t2;
        double sumaX, sumaY, ts, tc, tb;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <tamanyo>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            numHebras = -1;
            n = -1;
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        // Crea los vectores.
        double vectorX[] = new double[n];
        double vectorY[] = new double[n];

        //
        // Implementacion secuencial (sin temporizar).
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        for (int i = 0; i < n; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }

        //
        // Implementacion secuencial.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }
        t2 = System.nanoTime();
        ts = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo secuencial (seg.):                    " + ts);
        //// imprimeResultado( vectorX, vectorY );
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X:          " + sumaX);
        System.out.println("Suma del vector Y:          " + sumaY);
/*
    //
    // Implementacion paralela ciclica.
    //
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela ciclica
    // ....
    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + ... );
    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    //
    // Implementacion paralela por bloques.
    //
    // ....
    //
*/
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        MiHebraBloques[] hilos = new MiHebraBloques[numHebras];
        t1 = System.nanoTime();
        for (int i = 0; i < numHebras; i++) {

            hilos[i] = new MiHebraBloques(vectorX, vectorY, i, numHebras);
            hilos[i].start();
        }

        for (int i = 0; i < numHebras; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tb = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo paralelo bloques (seg.):                    " + tb);
        //// imprimeResultado( vectorX, vectorY );
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X:          " + sumaX);
        System.out.println("Suma del vector Y:          " + sumaY);


        System.out.println("Fin del programa.");

        System.out.println("Fin del programa.");
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorX(double vectorX[]) {
        if (vectorX.length == 1) {
            vectorX[0] = 0.0;
        } else {
            for (int i = 0; i < vectorX.length; i++) {
                vectorX[i] = 10.0 * (double) i / ((double) vectorX.length - 1);
            }
        }
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorY(double vectorY[]) {
        for (int i = 0; i < vectorY.length; i++) {
            vectorY[i] = 0.0;
        }
    }

    // --------------------------------------------------------------------------
    static double sumaVector(double vector[]) {
        double suma = 0.0;
        for (int i = 0; i < vector.length; i++) {
            suma += vector[i];
        }
        return suma;
    }

    // --------------------------------------------------------------------------
    static double evaluaFuncion(double x) {
        return Math.sin(Math.exp(-x) + Math.log1p(x));
    }

    // --------------------------------------------------------------------------
    static void imprimeVector(double vector[]) {
        for (int i = 0; i < vector.length; i++) {
            System.out.println(" vector[ " + i + " ] = " + vector[i]);
        }
    }

    // --------------------------------------------------------------------------
    static void imprimeResultado(double vectorX[], double vectorY[]) {
        for (int i = 0; i < Math.min(vectorX.length, vectorY.length); i++) {
            System.out.println("  i: " + i +
                    "  x: " + vectorX[i] +
                    "  y: " + vectorY[i]);
        }
    }

}

