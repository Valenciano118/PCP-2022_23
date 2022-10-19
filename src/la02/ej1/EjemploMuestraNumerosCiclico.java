package la02.ej1;


class MiHebraCiclos extends Thread{
  int miId, salto, limite;

  public MiHebraCiclos(int miId, int salto, int limite){
    this.miId = miId;
    this.salto = salto;
    this.limite = limite;
  }

  public void run(){
    for (int i = miId; i< limite; i+=salto){
      System.out.println(i+1);
    }
  }
}
// ============================================================================
class EjemploMuestraNumerosCiclico {
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
    MiHebraCiclos[] hilos = new MiHebraCiclos[numHebras];

    for (int i = 0; i< numHebras; i++){
      hilos[i] = new MiHebraCiclos(i,numHebras,n);
      hilos[i].start();
    }

    for (int i = 0; i< numHebras; i++) {
      try {
        hilos[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    // Crea y arranca el vector de hebras.
    // ... 
    // Espera a que terminen las hebras.
    // ... 
  }
}
