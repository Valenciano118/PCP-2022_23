#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main(int argc, char *argv[])
{
    int numProcs, miId;

    // Inicializa MPI.
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
    MPI_Comm_rank(MPI_COMM_WORLD, &miId);

    // --------------------------------------------------------------------------
    // ... (A)
    int dato = numProcs - miId + 1;
    int suma, datoAux;
    MPI_Status s;

    datoAux = (miId % 2 == 0) ? dato : 0;

    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Reduce(&datoAux, &suma, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    if (miId == 0)
    {
        printf("Soy el proceso %d. mi dato es %d y la suma total es %d\n", miId, dato, suma);
    }
    else
    {
        printf("Soy el proceso %d y mi dato es %d\n", miId, dato);
    }
    // --------------------------------------------------------------------------

    // Finalización de MPI.
    MPI_Finalize();

    // Fin de programa.
    printf("Fin de programa (%d) \n", miId);
    return 0;
}
