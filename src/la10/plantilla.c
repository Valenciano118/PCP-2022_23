#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  // Inicializa MPI.
  MPI_Init( & argc, & argv );
  MPI_Comm_size( MPI_COMM_WORLD, & numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, & miId );

  // --------------------------------------------------------------------------
  // ... (A)

  // --------------------------------------------------------------------------

  // Finalización de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Fin de programa (%d) \n", miId );
  return 0;
}
