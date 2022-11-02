package la04.ej2;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.*;
import javax.swing.event.*;


// ===========================================================================
class ZonaIntercambio {
// ===========================================================================
  AtomicLong tiempo;

  public ZonaIntercambio(long tiempo){
      this.tiempo = new AtomicLong(tiempo);
  }
  // -------------------------------------------------------------------------
  void setTiempo( long newTiempo ) {
    tiempo.set(newTiempo);
  }

  // -------------------------------------------------------------------------
  long getTiempo() {
    return tiempo.get();
  }
}



class HebraCalculadora extends Thread {
    boolean fin;
    JTextField textField;
    ZonaIntercambio intercambio;

    public HebraCalculadora(JTextField textField,ZonaIntercambio intercambio) {
        this.fin = false;
        this.textField = textField;
        this.intercambio = intercambio;
    }

    public void finalizaEjecucion() {
        this.fin = true;
    }

    public void iniciaEjecucion() {
        this.fin = false;
    }

    public void run() {
        long i = 1L;
        while (!fin) {
            if (GUISecuenciaPrimos.esPrimo(i)) {
                final long copy_i = i;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.setText(Long.valueOf(copy_i).toString());
                    }
                });
                try {
                    Thread.sleep(intercambio.getTiempo());
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            i += 1;

        }
    }
}

// ===========================================================================
public class GUISecuenciaPrimos {
    // ===========================================================================
    JFrame container;
    JPanel jpanel;
    JTextField txfMensajes;
    JButton btnComienzaSecuencia, btnCancelaSecuencia;
    JSlider sldEspera;
    HebraCalculadora t; // Ejercicio 2.2
    ZonaIntercambio   z; // Ejercicio 2.3

    // -------------------------------------------------------------------------
    public static void main(String args[]) {
        GUISecuenciaPrimos gui = new GUISecuenciaPrimos();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.go();
            }
        });
    }

    // -------------------------------------------------------------------------
    public void go() {
        // Constantes.
        final int valorMaximo = 1000;
        final int valorMedio = 500;

        // Variables.
        JPanel tempPanel;
        // Crea el JFrame principal.
        container = new JFrame("GUI Secuencia de Primos ");

        // Consigue el panel principal del Frame "container".
        jpanel = (JPanel) container.getContentPane();
        jpanel.setLayout(new GridLayout(3, 1));

        // Crea e inserta la etiqueta y el campo de texto para los mensajes.
        txfMensajes = new JTextField(20);
        txfMensajes.setEditable(false);
        tempPanel = new JPanel();
        tempPanel.setLayout(new FlowLayout());
        tempPanel.add(new JLabel("Secuencia: "));
        tempPanel.add(txfMensajes);
        jpanel.add(tempPanel);

        // Crea e inserta los botones de Comienza secuencia y Cancela secuencia.
        btnComienzaSecuencia = new JButton("Comienza secuencia");
        btnCancelaSecuencia = new JButton("Cancela secuencia");
        tempPanel = new JPanel();
        tempPanel.setLayout(new FlowLayout());
        tempPanel.add(btnComienzaSecuencia);
        tempPanel.add(btnCancelaSecuencia);
        jpanel.add(tempPanel);

        // Crea e inserta el slider para controlar el tiempo de espera.
        sldEspera = new JSlider(JSlider.HORIZONTAL, 0, valorMaximo, valorMedio);
        tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());
        tempPanel.add(new JLabel("Tiempo de espera: "));
        tempPanel.add(sldEspera);
        jpanel.add(tempPanel);

        // Activa inicialmente los 2 botones.
        btnComienzaSecuencia.setEnabled(true);
        btnCancelaSecuencia.setEnabled(false);
        this.z = new ZonaIntercambio(sldEspera.getValue());


        // Anyade codigo para procesar el evento del boton de Comienza secuencia.
        btnComienzaSecuencia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnComienzaSecuencia.setEnabled(false);
                btnCancelaSecuencia.setEnabled(true);
                t = new HebraCalculadora(txfMensajes,z);
                t.start();
            }
        });

        // Anyade codigo para procesar el evento del boton de Cancela secuencia.
        btnCancelaSecuencia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnComienzaSecuencia.setEnabled(true);
                btnCancelaSecuencia.setEnabled(false);
                t.finalizaEjecucion();
            }
        });

        // Anyade codigo para procesar el evento del slider " Espera " .
        sldEspera.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider sl = (JSlider) e.getSource();
                if (!sl.getValueIsAdjusting()) {
                    long tiempoMilisegundos = (long) sl.getValue();
                    System.out.println("JSlider value = " + tiempoMilisegundos);
                    z.setTiempo(tiempoMilisegundos);
                }
            }
        });

        // Fija caracteristicas del container.
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        System.out.println("% End of routine: go.\n");
    }

    // -------------------------------------------------------------------------
    static boolean esPrimo(long num) {
        boolean primo;
        if (num < 2) {
            primo = false;
        } else {
            primo = true;
            long i = 2;
            while ((i < num) && (primo)) {
                primo = (num % i != 0);
                i++;
            }
        }
        return (primo);
    }
}

