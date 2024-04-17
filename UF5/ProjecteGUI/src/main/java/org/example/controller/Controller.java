package org.example.controller;

import org.example.app.LaMeuaExcepcio;
import org.example.model.Model;
import org.example.view.Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

public class Controller implements PropertyChangeListener { //1. Implementació de interfície PropertyChangeListener

    //2. Propietat lligada per controlar quan genro una excepció
    public static final String PROP_EXCEPCIO="excepcio";
    private LaMeuaExcepcio excepcio;

    public LaMeuaExcepcio getExcepcio() {
        return excepcio;
    }

    public void setExcepcio(LaMeuaExcepcio excepcio) {
        LaMeuaExcepcio valorVell=this.excepcio;
        this.excepcio = excepcio;
        canvis.firePropertyChange(PROP_EXCEPCIO, valorVell,excepcio);
    }


    //3. Propietat PropertyChangesupport necessària per poder controlar les propietats lligades
    PropertyChangeSupport canvis=new PropertyChangeSupport(this);


    //4. Mètode on posarem el codi de tractament de les excepcions --> generat per la interfície PropertyChangeListener
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LaMeuaExcepcio rebuda=(LaMeuaExcepcio)evt.getNewValue();

        try {
            throw rebuda;
        } catch (LaMeuaExcepcio e) {
            //Aquí farem ele tractament de les excepcions de l'aplicació
            switch(evt.getPropertyName()){
                case PROP_EXCEPCIO:

                    switch(rebuda.getCodi()){
                        case 1:
                            JOptionPane.showMessageDialog(null, rebuda.getMissatge());
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(null, rebuda.getMissatge());
                            //this.view.getCampNom().setText(rebuda.getMissatge());
                            this.view.getCampNom().setSelectionStart(0);
                            this.view.getCampNom().setSelectionEnd(this.view.getCampNom().getText().length());
                            this.view.getCampNom().requestFocus();

                            break;
                    }


            }
        }
    }


    private Model model;
    private Vista view;

    public Controller(Model model, Vista view) {
        this.model = model;
        this.view = view;

        //Mètode per lligar la vista i el model
        lligarVistaModel();

        //Assigno el codi dels listeners
        assignarCodiListeners();

        //5. Necessari per a que Controller reaccione davant de canvis a les propietats lligades
        canvis.addPropertyChangeListener(this);
    }

    private void assignarCodiListeners() {
        Model modelo=this.model;

        DefaultTableModel model=this.model.getModel();

        JButton insertarButton=view.getInsertarButton();
        JButton borrarButton=view.getBorrarButton();
        JButton modificarButton=view.getModificarButton();

        JTextField campNom=view.getCampNom();
        JTextField campPes=view.getCampPes();
        JCheckBox caixaAlumne=view.getCaixaAlumne();

        JTable taula=view.getTaula();


        insertarButton.addActionListener(
                new ActionListener() {
                    /**
                     * Invoked when an action occurs.
                     *
                     * @param e the event to be processed
                     */
                    @Override
                    public void actionPerformed(ActionEvent e){


                        //Comprovem que totes les caselles continguen informació
                        if(campNom.getText().isBlank() || campPes.getText().isBlank()){
                            JOptionPane.showMessageDialog(null,"Falta omplir alguna dada!!");
                        }
                        else{
                            try{
                                NumberFormat num=NumberFormat.getNumberInstance(Locale.getDefault());   //Creem un número que entèn la cultura que utilitza l'aplicació
                                double pes= num.parse(campPes.getText().trim()).doubleValue();  //intentem convertir el text a double
                                if(pes<1 || pes>800) throw new ParseException("",0);
                                model.addRow(new Object[]{campNom.getText(),pes,caixaAlumne.isSelected()});
                                campNom.setText("Pepito");
                                campNom.setSelectionStart(0);
                                campNom.setSelectionEnd(campNom.getText().length());
                                campPes.setText("75");
                                campNom.requestFocus();         //intentem que el foco vaigue al camp del nom
                            }catch(ParseException ex){
                                //6. Enlloc de llançar l'excepció (no ho puc fer!!), canviem el valor de la propietat lligada usant el setter
                                setExcepcio(new LaMeuaExcepcio(1,"Has d'introduir un pes correcte (>=1 i <=800!!"));

                                //throw new LaMeuaExcepcio(1,"Has d'introduir un pes correcte (>=1 i <=800!!");
                                //JOptionPane.showMessageDialog(null, "Has d'introduir un pes correcte (>=1 i <=800!!");
                                campPes.setSelectionStart(0);
                                campPes.setSelectionEnd(campPes.getText().length());
                                campPes.requestFocus();
                            }
                        };


                    }
                }
        );

        borrarButton.addActionListener(
                new ActionListener() {
                    /**
                     * Invoked when an action occurs.
                     *
                     * @param e the event to be processed
                     */
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Mirem si tenim una fila de la taula seleccionada
                        int filaSel=taula.getSelectedRow();

                        if(filaSel!=-1){
                            model.removeRow(filaSel);
                            //Posem els camps de text en blanc
                            campNom.setText("");
                            campPes.setText("");
                        }
                        else JOptionPane.showMessageDialog(null, "Per borrar una fila l'has de seleccionar a la taula");
                    }
                }
        );

        modificarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                //Mirem si tenim una fila de la taula seleccionada
                int filaSel=taula.getSelectedRow();

                if(filaSel!=-1){
                    //Abans de modoficar comprovem que tenim els camps omplits
                    //Comprovem que totes les caselles continguen informació
                    if(campNom.getText().isBlank() || campPes.getText().isBlank()){
                        JOptionPane.showMessageDialog(null,"Falta omplir alguna dada!!");
                    }
                    else{
                        model.removeRow(filaSel);
                        model.insertRow(filaSel, new Object[]{Double.valueOf(campPes.getText()),caixaAlumne.isSelected()});
                        //Posem els camps de text en blanc
                        campNom.setText("");
                        campPes.setText("");
                        campNom.requestFocus();         //intentem que el foco vaigue al camp del nom
                    };




                }
                else JOptionPane.showMessageDialog(null, "Per modificar una fila l'has de seleccionar a la taula");
            }
        });

        taula.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //Al seleccionar la taula omplim els camps de text en els valors de la fila seleccionada
                int filaSel=taula.getSelectedRow();

                if(filaSel!=-1){        //Tenim una fila seleccionada
                    //Posem els valors de la fila seleccionada als camps respectius
                    campNom.setText(model.getValueAt(filaSel,0).toString());
                    campPes.setText(model.getValueAt(filaSel,1).toString().replaceAll("\\.",","));
                    caixaAlumne.setSelected((Boolean)model.getValueAt(filaSel,2));
                }else{                  //Hem deseleccionat una fila
                    //Posem els camps de text en blanc
                    campNom.setText("");
                    campPes.setText("");
                }
            }
        });

        campPes.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                //Comprovem que el valor introduït és el d'un pes correcte
                try{
                    NumberFormat num=NumberFormat.getNumberInstance(Locale.getDefault());   //Creem un número que entèn la cultura que utilitza l'aplicació
                    double pes= num.parse(campPes.getText().trim()).doubleValue();  //intentem convertir el text a double
                    if(pes<1 || pes>800) throw new ParseException("",0);
                    //campPes.setText(campPes.getText().replaceAll(".",""));  //eliminem los punts del número decimal
                }catch(ParseException ex){
                    JOptionPane.showMessageDialog(null, "Has d'introduir un pes correcte (>=1 i <=800!!");
                    campPes.setSelectionStart(0);
                    campPes.setSelectionEnd(campPes.getText().length());
                    campPes.requestFocus();

                }
            }
        });

        view.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                //Aquí haurem de guardar les dades de la taula al fitxer
                modelo.escriureDadesFitxer();
            }
        });

        campNom.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String regex1="^[A-ZÀ-ÚÑÇ][a-zà-úñç]+\\s+[A-ZÀ-ÚÑÇ][a-zà-úñç]+\\s+[A-ZÀ-ÚÑÇ][a-zà-úñç]+$",
                        regex2="^[A-ZÀ-ÚÑÇ][a-zà-úñç]+(\\s*,\\s*)[A-ZÀ-ÚÑÇ][a-zà-úñç]+\\s+[A-ZÀ-ÚÑÇ][a-zà-úñç]+$";;
                //String regex="[À-ú]";
                //Pattern pattern = Pattern.compile(regex);
                if(campNom.getText().isBlank() || (!campNom.getText().matches(regex1) && !campNom.getText().matches(regex2))){
                    setExcepcio(new LaMeuaExcepcio(2,"El nom ha de ser..."));
                }
            }
        });
        //throw new LaMeuaExcepcio(1,"Ha petat la base de dades");

    }

    private void lligarVistaModel() {
        //Obtinc els objectes de la vista
        JTable taula=view.getTaula();

        //Obtinc els objectes del model
        DefaultTableModel model=this.model.getModel();
        taula.setModel(model);

        //Forcem a que només se pugue seleccionar una fila de la taula
        taula.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


    }


}
