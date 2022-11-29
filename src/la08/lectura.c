#include <stdio.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  MPI_Init( & argc, & argv );

  MPI_Comm_size( MPI_COMM_WORLD, & numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, & miId );

  // ------ INICIO CODIGO A MODIFICAR -----------------------------------------
  int n = ( miId + 1 ) * numProcs;
  
  if ( miId == 0) {
    printf ("Dame un numero --> \n"); scanf ("%d", &n);
  }
  printf ("Proceso <%d> con n = %d\n", miId, n);

  // ------ FIN CODIGO A MODIFICAR --------------------------------------------

  MPI_Finalize();

  printf( "Fin de programa\n" );
  return 0;
}

