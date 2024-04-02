package org.example.model;

import org.example.model.Alumne;

import java.io.*;

public class Fitxers {

    //propietat constant per definir el nom del fitxer una sola vegada dins l'aplicació
    public static final String FITXER="./src/main/resources/dades2.dat";

    public static void main(String[] args) {

        File f=new File(FITXER);        //apuntador al fitxer, però no el crea

        //Mirem si el fitxer ja existix (o no)
        if(f.exists()){
            //Llegim el fitxer i guardem les seues dades a la JTable
            System.out.println("Fitxer existent");


            //Try-with-resources
            try(ObjectInputStream entrada=new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));) {
                //Objecte que em permet llegir els objectes del fitxer

                while(true){

                    try {
                        Alumne c=(Alumne)entrada.readObject();   //al projecte guardarem este object al JTable
                        System.out.println(c);

                    } catch (ClassNotFoundException e) {
                        break;
                        //throw new RuntimeException(e);
                    } catch (IOException e){
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al llegir el fitxer");
                //throw new RuntimeException(e);
            }


        }else{      //Com el fitxer no existix, el creem posant-li unes dades inventades
            Alumne[] dadesFicticies=new Alumne[]{
                    new Alumne("Pepe", 60.7)};

            ObjectOutputStream sortida=null;
            try {
                //Creem un objecte que ens permetrà escriure al fitxer
                sortida=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));

                //Anem a escriure els objectes de les dadesFicticies al fitxer
                for (int i = 0; i < dadesFicticies.length; i++) {
                    sortida.writeObject(dadesFicticies[i]);
                }

            } catch (IOException e) {
                System.out.println("Hi ha hagut algun problema a l'intentar obrir el fitxer");
                //throw new RuntimeException(e);
            } finally {
                //Intentem tancar el objecte sortida
                try {
                    if(sortida!=null) sortida.close();
                } catch (IOException e) {
                    System.out.println("Error en intentar tancar el fitxer d'escriptura");
                    //throw new RuntimeException(e);
                }
            }

        }








    }










}
