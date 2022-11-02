package la04.ej1;

import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula() {
    this.suma = 0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    this.suma += dato;
  }

  // -------------------------------------------------------------------------
  double dameDato() {
    return this.suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones( int miId, int numHebras, long numRectangulos,
                              Acumula a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x;
    for (int i =miId ; i<numRectangulos; i+=numHebras){
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      a.acumulaDato(EjemploNumeroPI.f(x));
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacion extends Thread {
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraUnaAcumulacion( int miId, int numHebras, long numRectangulos,
                            Acumula a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double suma = 0.0;
    double x;
    for (int i =miId ; i<numRectangulos; i+=numHebras){
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += EjemploNumeroPI.f(x);
    }
    a.acumulaDato(suma);
  }
}

// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread {
  int      miId, numHebras;
  long     numRectangulos;
  DoubleAdder suma;

  public MiHebraMultAcumulacionAtomica(int miId, int numHebras, long numRectangulos, DoubleAdder suma){
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.suma =suma;
  }
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x;
    for (int i =miId ; i<numRectangulos; i+=numHebras){
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      this.suma.add(EjemploNumeroPI.f(x));
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica extends Thread {
  int      miId, numHebras;
  long     numRectangulos;
  DoubleAdder suma;

  public MiHebraUnaAcumulacionAtomica(int miId, int numHebras, long numRectangulos,DoubleAdder suma){
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.suma = suma;
  }
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x;
    double suma_local = 0.0;
    for (int i =miId ; i<numRectangulos; i+=numHebras){
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma_local += EjemploNumeroPI.f(x);
    }
    this.suma.add(suma_local);
  }
}



// ===========================================================================
class EjemploNumeroPI {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    long                        t1, t2;
    double                      tSec, tPar;
    // Acumula                     a;
    // MiHebraMultAcumulaciones  vt[];

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Comienzo del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo secuencial (s.):        " + tSec );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra." );
    t1 = System.nanoTime();
    MiHebraMultAcumulaciones[] hilo_ciclico = new MiHebraMultAcumulaciones[numHebras];
    Acumula a_ciclico = new Acumula();
    for (int i =0; i< numHebras; i++){
      hilo_ciclico[i] = new MiHebraMultAcumulaciones(i,numHebras,numRectangulos,a_ciclico);
      hilo_ciclico[i].start();
    }
    for (int i =0; i< numHebras; i++){
      try {
        hilo_ciclico[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    pi = baseRectangulo + a_ciclico.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra.
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra." );
    t1 = System.nanoTime();
    MiHebraUnaAcumulacion[] hilo_ciclico_una_acumulacion = new MiHebraUnaAcumulacion[numHebras];
    Acumula a_ciclico_una_acumulacion = new Acumula();
    for (int i =0; i< numHebras; i++){
      hilo_ciclico_una_acumulacion[i] = new MiHebraUnaAcumulacion(i,numHebras,numRectangulos,a_ciclico_una_acumulacion);
      hilo_ciclico_una_acumulacion[i].start();
    }
    for (int i =0; i< numHebras; i++){
      try {
        hilo_ciclico_una_acumulacion[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    pi = baseRectangulo + a_ciclico.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra (Atomica)
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra (At)." );
    t1 = System.nanoTime();
    MiHebraMultAcumulacionAtomica[] mult_acc_atom = new MiHebraMultAcumulacionAtomica[numHebras];
    DoubleAdder suma_mult_acc_atom = new DoubleAdder();
    for (int i = 0; i<numHebras; i++){
      mult_acc_atom[i] = new MiHebraMultAcumulacionAtomica(i,numHebras,numRectangulos,suma_mult_acc_atom);
      mult_acc_atom[i].start();
    }
    for (int i =0; i< numHebras; i++){
      try {
       mult_acc_atom[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra (At)." );
    t1 = System.nanoTime();
    MiHebraUnaAcumulacionAtomica[] una_acc_atom = new MiHebraUnaAcumulacionAtomica[numHebras];
    DoubleAdder suma_una_acc_atom = new DoubleAdder();
    for (int i = 0; i<numHebras; i++){
      una_acc_atom[i] = new MiHebraUnaAcumulacionAtomica(i,numHebras,numRectangulos,suma_mult_acc_atom);
      una_acc_atom[i].start();
    }
    for (int i =0; i< numHebras; i++){
      try {
        una_acc_atom[i].join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

