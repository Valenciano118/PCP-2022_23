package la01;

class MiRun implements Runnable {
  final int miId;

  public MiRun(int miId){
    this.miId = miId;
  }
  public void run() {
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Auxiliar " +miId );
    }
  }
}

class EjemploCreacionRunnableSimple {
  public static void main( String args[] ) {
    MiRun r = new MiRun(0);
    MiRun r1 = new MiRun(1);

    Thread t = new Thread(r);
    Thread t1 = new Thread(r1);
    t.run( );
    t1.run();
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Principal" );
    }
  }
}
