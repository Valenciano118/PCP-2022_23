#include <stdio.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  MPI_Init( & argc, & argv );

  MPI_Comm_size( MPI_COMM_WORLD, & numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, & miId );

  // ------ INICIO CODIGO A MODIFICAR -----------------------------------------
  int n = ( miId + 1 ) * numProcs, i;
  MPI_Status st;

  if ( miId == 0) {
    printf ("Dame un numero --> \n"); scanf ("%d", &n);
    for (i=1; i<numProcs; i++){
      MPI_Send(&n, 1, MPI_INT, i, 88, MPI_COMM_WORLD);
    }
  } else {
    MPI_Recv( &n, 1, MPI_INT, 0, 88, MPI_COMM_WORLD, &st);
  }
  printf ("Proceso <%d> con n = %d\n", miId, n);

  // ------ FIN CODIGO A MODIFICAR --------------------------------------------

  MPI_Finalize();

  printf( "Fin de programa\n" );
  return 0;
}

