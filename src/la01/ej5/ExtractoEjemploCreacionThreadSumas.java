package la01.ej5;

class MiHebra extends Thread {
  final int miId;
  final int n1;
  final int n2;

  public MiHebra( int miId, int n1, int n2 ) {
    this.miId = miId;
    this.n1 = n1;
    this.n2 = n2;
  }
  public void run() {
    int suma = 0;
    for( int i = n1; i <= n2; i++ ) {
      suma += i;
    }
    System.out.println( "Hebra Auxiliar " + miId + " , suma: " + suma);
  }
}

class EjemploCreacionThreadSumas {
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    System.out.println( "numHebras: " + numHebras );

    // --------  CODIGO PRINCIPAL TRAS PROCESAR PARAMETROS  -------------------

    System.out.println( "Hebra Principal inicia" );
    // Crear un vector (v) de tipo MiHebra con numHebras elementos
    MiHebra v[] = new MiHebra[ numHebras ];
    // Crear y arrancar las hebras y almacenarlas en v
    for (int i =0; i< numHebras; i++){
      v[i] = new MiHebra(i,1,1_000_000);
      v[i].start();
    }
    // ...
    // Esperar a que terminen todas las hebras almacenadas en v
    for (int i =0; i< numHebras; i++){
      try {
        v[i].join();
      }catch (InterruptedException e ){
        e.printStackTrace();
      }
    }
    // ...
    System.out.println( "Hebra Principal finaliza" );
  }
}

