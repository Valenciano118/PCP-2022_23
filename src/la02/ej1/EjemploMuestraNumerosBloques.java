package la02.ej1;

class MiHebraBloques extends Thread {
  int n, numHebras,miId;

  public MiHebraBloques(int n, int numHebras, int miId) {
    this.n = n;
    this.numHebras = numHebras;
    this.miId = miId;
  }

  public void run() {
    int tamBloq= (n+numHebras-1)/numHebras;
    int inicio = tamBloq * miId;
    int fin = Math.min(inicio+tamBloq, n);
    for (int i = inicio; i < fin; i++) {
      System.out.println(i + 1);
    }
  }
}
// ============================================================================
class EjemploMuestraNumerosBloques {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  n, numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <n>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    MiHebraBloques[] hilos = new MiHebraBloques[numHebras];

    for (int i = 0; i < numHebras; i++) {
      hilos[i] = new MiHebraBloques(n, numHebras, i);;
      hilos[i].start();
    }

    for (int i = 0; i< numHebras; i++) {
      try {
        hilos[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
