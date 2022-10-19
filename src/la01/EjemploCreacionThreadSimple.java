package la01;

class MiHebra extends Thread {
  final int miId;
  public MiHebra( int miId){
    this.miId = miId;
  }
  public void run() {
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Auxiliar " + miId );
    }
  }
}

class EjemploCreacionThreadSimple {
  public static void main( String args[] ) {
    MiHebra t = new MiHebra( 0 );
    MiHebra d = new MiHebra(1);

    t.start();
    d.start();

    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Principal" );
    }
  }
}

