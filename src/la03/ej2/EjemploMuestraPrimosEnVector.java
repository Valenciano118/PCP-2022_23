package la03.ej2;

import java.util.concurrent.atomic.AtomicInteger;

import static la03.ej2.EjemploMuestraPrimosEnVector.esPrimo;

// ===========================================================================
public class EjemploMuestraPrimosEnVector {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     numHebras, vectOpt;
    boolean option = true;
    long    t1, t2;
    double  ts, tc, tb, td;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <vectorOption>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      vectOpt   = Integer.parseInt( args[ 1 ] );
      if ( ( vectOpt != 0 ) && ( vectOpt != 1 ) ) {
        System.out.println( "ERROR: vectorOption should be 0 or 1.");
        System.exit( -1 );
      } else {
        option = (vectOpt == 0);
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    //
    // Eleccion del vector de trabajo
    //
    VectorNumeros vn = new VectorNumeros(option);
    long vectorNumeros[] = vn.vector;

    //
    // Implementacion secuencial.
    //
    System.out.println( "" );
    System.out.println( "Implementacion secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < vectorNumeros.length; i++ ) {
      if( esPrimo( vectorNumeros[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
      }
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + ts );

    //
    // Implementacion paralela ciclica.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela ciclica." );
    t1 = System.nanoTime();
    la03.ej2.MiHebraPrimoDistCiclica[] hilos = new la03.ej2.MiHebraPrimoDistCiclica[numHebras];
    for (int i =0; i< numHebras; i++){
      hilos[i] = new la03.ej2.MiHebraPrimoDistCiclica(i,numHebras,vectorNumeros);
      hilos[i].start();
    }

    for (int i =0; i< numHebras; i++){
      try {
        hilos[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + ts/tc );
    //
    // Implementacion paralela por bloques.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela por bloques." );
    t1 = System.nanoTime();
    la03.ej2.MiHebraPrimoDistPorBloques[] hilos_bloq = new la03.ej2.MiHebraPrimoDistPorBloques[numHebras];
    for (int i =0; i< numHebras; i++){
      hilos_bloq[i] = new la03.ej2.MiHebraPrimoDistPorBloques(i,numHebras,vectorNumeros);
      hilos_bloq[i].start();
    }

    for (int i =0; i< numHebras; i++){
      try {
        hilos_bloq[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela Bloques (seg.):              " + tb );
    System.out.println( "Incremento paralela por Bloques:                 " + ts/tb );

    //
    // Implementacion paralela dinamica.
    //
    // ....
    System.out.println( "" );
    System.out.println( "Implementacion paralela por dinamica." );
    t1 = System.nanoTime();
    la03.ej2.MiHebraPrimoDistPorDinamica[] hilos_din = new la03.ej2.MiHebraPrimoDistPorDinamica[numHebras];
    AtomicInteger temp = new AtomicInteger(0);
    for (int i =0; i< numHebras; i++){
      hilos_din[i] = new la03.ej2.MiHebraPrimoDistPorDinamica(temp,vectorNumeros);
      hilos_din[i].start();
    }

    for (int i =0; i< numHebras; i++){
      try {
        hilos_din[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    td = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela dinamica (seg.):              " + td );
    System.out.println( "Incremento paralela por dinamica:                 " + ts/td );
  }
  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean primo;
    if( num < 2 ) {
      primo = false;
    } else {
      primo = true;
      long i = 2;
      while( ( i < num )&&( primo ) ) {
        primo = ( num % i != 0 );
        i++;
      }
    }
    return( primo );
  }

}

class MiHebraPrimoDistCiclica extends Thread {
  int miId, numHebras;
  long[] vector;

  public MiHebraPrimoDistCiclica(int miId, int numHebras, long[] vector){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vector = vector;
  }
  public void run(){
    for(int i= miId; i< vector.length; i+= numHebras){
      if( esPrimo(vector[i])){
        System.out.println("Encontrado primo: "+vector[i]);
      }
    }
  }
}

class MiHebraPrimoDistPorBloques extends Thread {
  int miId, numHebras;
  long[] vector;

  public MiHebraPrimoDistPorBloques(int miId, int numHebras, long[] vector){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vector = vector;
  }
  public void run(){
    int tamBloq= (vector.length+numHebras-1)/numHebras;
    int inicio = tamBloq * miId;
    int fin = Math.min(inicio+tamBloq, vector.length);
    for(int i= inicio; i< fin; i++){
      if(esPrimo(vector[i])){
        System.out.println("Encontrado primo: "+vector[i]);
      }
    }
  }
}

class MiHebraPrimoDistPorDinamica extends Thread {
  AtomicInteger indice;
  long[] vector;

  public MiHebraPrimoDistPorDinamica(AtomicInteger indice, long[] vector) {
    this.indice = indice;
    this.vector = vector;
  }

  public void run() {
    int pos = indice.getAndIncrement();
    while (pos < vector.length) {
      if (esPrimo(vector[pos])) {
        System.out.println("Encontrado primo: " + vector[pos]);
      }
      pos = indice.getAndIncrement();
    }
  }
}
// ===========================================================================
class VectorNumeros {
// ===========================================================================
  long    vector[];

  // -------------------------------------------------------------------------
  public VectorNumeros (boolean caso) {
    if (caso) {
      vector = new long [] {
      200000033L, 200000039L, 200000051L, 200000069L,
      200000081L, 200000083L, 200000089L, 200000093L,
      200000107L, 200000117L, 200000123L, 200000131L,
      200000161L, 200000183L, 200000201L, 200000209L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
			};
    } else {
      vector = new long [] {
      200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000107L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000117L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000123L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000131L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000161L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000183L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000201L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000209L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L 
      };
    }
  }
}

