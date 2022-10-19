package la02.ej1;
class MiHebraBloques extends Thread{
  int inicio,fin;

  public MiHebraBloques(int inicio, int fin){
    this.inicio = inicio;
    this.fin = fin;
  }

  public void run(){
    for (int i = inicio; i< fin; i++){
      System.out.println(i+1);
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
    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    // Crea y arranca el vector de hebras.
    // ... 
    // Espera a que terminen las hebras.
    // ... 
  }
}
