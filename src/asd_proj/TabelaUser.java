/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asd_proj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


/**
 *
 * @author USER
 */
public class TabelaUser {                   //klasa głowna
    class Pole{                             //klasa opisująca pole
        public String nazwa;
        public String typ;
        public String[] wlasciwosci;  //NOT NULL, Default, val(...) 
        Pole(String nazwa, String typ)
        {
            this.nazwa=nazwa;
            this.typ=typ;
        }
        Pole(String nazwa, String typ, String[] wlasciwosci)
        {
            this.nazwa=nazwa;
            this.typ=typ;
            this.wlasciwosci=wlasciwosci;
        }
    }
    //----------------------------------------
    Pole[] nazwyPol = {                                                             //opis pól
                        new Pole("id", "INT"), 
                        new Pole("name", "STRING",new String[]{"NOT NULL"}),
                        new Pole("lastname", "STRING",new String[]{"NOT NULL"}),
                        new Pole("position", "STRING",new String[]{"NOT NULL"}),
                        new Pole("salary_net", "INT",new String[]{"DEFAULT=3000"}),
                        new Pole("gender", "STRING",new String[]{"VAL(M,K)"}),
                        new Pole("birth_date", "DATE"),
                        new Pole("empl_date", "DATE"),
                        new Pole("activity", "BOOL")
    };
    Integer findColId(String name)                                              //znajduje kolumne po nazwie (metoda klasy głównej)
    {
        for(int i=0; i<nazwyPol.length; i++)//wszystkie pola
        {
            if(nazwyPol[i].nazwa.equals(name))
                return i;
        }
        return null;
    }
    String findColName(int id)                                                  //znajduje po id
    {
        if(id>=0 && id<nazwyPol.length)
            return nazwyPol[id].nazwa;
        else
            return null;
    }
    Scanner sc=sc = new Scanner(System.in);                                     //czytacz linii
    List<Wiersz> wiersz = new ArrayList<>();                                    //lista wierszy w tabeli
    Object stringToProperType(int i, String a)                                  //i - numer kolumny, a - wartosc ze Scannera
        {
            try {           
                switch(nazwyPol[i].typ.toUpperCase())                           //sprawdzamy jaki typ i wstawiamy odpowiedni obiekt
                {
                    case "INT": return Integer.parseInt(a);
                    case "DATE":             
                        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(a);
                        return d;               

                    case "BOOL": if(a.toUpperCase().equals("TRUE") || a.equals("1")) return true; else return false;
                    default: return a;
                } 
            } catch (Exception ex) {                                            //wyjątki - jak coś nie pasuje, zwraca null
                System.out.println(ex.toString());
                //Logger.getLogger(TabelaUser.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    class Wiersz{                                                               //klasa opisująca wiersz
        //Lista kolumn
        List<Object> kolumny = new ArrayList<>();
        
        
        //konstruktor domyslny generuje puste pola o danych typach
        Wiersz()
        {
            for(int i=0; i<nazwyPol.length; i++)                                //wszystkie pola
            {
                switch(nazwyPol[i].typ.toUpperCase())                           //sprawdzamy jaki typ i wstawiamy odpowiedni obiekt
                {
                    case "INT": Integer a = null; kolumny.add(a);break;
                    case "DATE": Date b = null; kolumny.add(b);break;
                    case "BOOL": Boolean c= null; kolumny.add(c);break;
                    default: String d = null; kolumny.add(d);
                }
            }
            setAutoIncrementId();                                               //dodaje autoinkrementacje do pola id
            setDefaults();                                                      //ustawia wszystkie domyslne wartosci
            setEveryNotNull();                                                  //prosi o wpisanie wszystkich ktore nie maja byc nullami
        }
        int notNullCount()                                                      //zwraca ile elementow ma wlasnosc notnull
        {
            int nn = 0;
            for(int i=0; i<nazwyPol.length; i++)//wszystkie pola
            {
                String[] wlasc = nazwyPol[i].wlasciwosci;
                for(int j=0; j<wlasc.length; j++)
                {
                    if(wlasc[j].toUpperCase().contains("NOT NULL"))
                    {
                        nn++;
                        break;
                    }
                }
            }
            return nn;
        }
        
        Object stringToProperTypeAndAdd(int i, String a)                        // wypełnia poszczególne kolumny w wierszu
        {
            Object res = stringToProperType(i, a);
            kolumny.set(i, res);
            return res;
        }
        class DoZrobienia implements Runnable{                                  //klasa do przekazywania funkcji (schemat funkcji)

            String[] wlasc;
            int i,j;
            
            void setValues(String[] wlasc, int i, int j)
            {
                this.wlasc=wlasc;
                this.i=i;
                this.j=j;
            }
            @Override
            public void run() {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
            
        }
        void whatShouldContain(String str, DoZrobienia whatToDo)                //co szukac, co wykonac; dalsza część schematu funkcji, do wyszukiwania typów
        {                                                                       //niestandardowych, np.default
                
            for(int i=0; i<nazwyPol.length; i++)//wszystkie pola
            {                 
                String[] wlasc = nazwyPol[i].wlasciwosci;
                if(wlasc!=null)
                {
                    for(int j=0; j<wlasc.length; j++)
                    {
                        if(wlasc[j].toUpperCase().contains(str))
                        {
                            whatToDo.setValues(wlasc, i, j);
                            whatToDo.run();
                            break;
                        }
                    }
                }                
            }
        }
        final void setDefaults()
        {              
            whatShouldContain("DEFAULT=", new DoZrobienia(){
                @Override
                public void run()
                {
                    String[] split = wlasc[j].split("=");
                        if(split.length>0)
                            stringToProperTypeAndAdd(i, split[1]);
                }
            });
        }
        
        List<Integer> notNullId = new ArrayList<>();                            //lista wszystkich not null'ów
        final void setEveryNotNull()                                            //ustawianie kazdego notnulla
        {
            
            try{
                whatShouldContain("NOT NULL", new DoZrobienia(){
                    @Override
                    public void run()
                    {
                        //prosi o wpisywanie
                        String wynik;
                        do                                                      //sprawdza, czy dobrze wprowadzono dane; jak nie, wprowadź jeszcze raz
                        {
                            System.out.println("Podaj wartosc dla: "+nazwyPol[i].nazwa+" ["+nazwyPol[i].typ.toUpperCase()+"]");
                            if(!sc.hasNextLine())
                                return;
                            wynik = sc.nextLine();
                        }while(stringToProperTypeAndAdd(i,wynik)==null);
                        
                        
                        //ustawianie wczytanego stringa jako poprawna wartosc typu
                        notNullId.add(i);//jest notnull wiec dodajemy do listy
                    }
                });
            }catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
        
        final boolean isInValRange(String[] valRange, String in)                //sprawdza, czy jest wprowadzane dane są w zakresie; pojedyncza wartosc in, ktora sprawdzamy
        {
            for(int i=0; i<valRange.length; i++)
            {
                if(in.equals(valRange[i]))
                    return true;
            }
            return false;
        }
        final void setValueOptions()                                            //ustawianie kazdego z value, np. m, k w każdym wierszu w kolumnie płeć 
        {
            
            try{
                whatShouldContain("VAL", new DoZrobienia(){
                    @Override
                    public void run()
                    {
                        String wl = nazwyPol[i].wlasciwosci[j].substring(4);    //obcina VAL(
                        wl=wl.substring(0, wl.length()-1);                      // obcina )
                        String l[] = wl.split(",");
                        
                        String wynik;                                           //dzieli przecinkami
                        do
                        {
                            System.out.println("Podaj wartosc dla: "+nazwyPol[i].nazwa+" ["+nazwyPol[i].typ.toUpperCase()+"] "+nazwyPol[i].wlasciwosci[j]);
                            if(!sc.hasNextLine())
                                return;
                            wynik = sc.nextLine();
                        }while(!isInValRange(l, wynik) || stringToProperTypeAndAdd(i,wynik)==null);
                        
                        
                    }
                });
            }catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
        
        final void setAutoIncrementId()                                         //dodaje do kolumny id wartosc statyczna i dodaje 1
        {
            kolumny.set(findColId("id"), incrementId++);
        }
        final boolean notInNotNullList(int i)                                   //sprawdza, czy dane id nie jest w not null'ach
        {
            for(int z =0; z<notNullId.size();z++)
            {
                if(notNullId.get(z)==i)
                    return false;
            }
            return true;
        }
        final void setTheRest()                                                 //wprowadzanie całej reszty, która nie została wcześniej wprowadzona
        {
            
            
                for(int i=0; i<nazwyPol.length; i++)//wszystkie pola niebedace notnull
                {
                    if(!nazwyPol[i].nazwa.equals("id") && !nazwyPol[i].nazwa.equals("gender") && notInNotNullList(i) )
                    {
                        try {
                            Object obj = null;
                            do{
                                System.out.println("Podaj wartosc dla: "+nazwyPol[i].nazwa+" ["+nazwyPol[i].typ.toUpperCase()+"]");
                                String wynik = sc.nextLine();
                                obj = stringToProperTypeAndAdd(i,wynik);
                            }while(obj==null);
                            
                            //ustawianie wczytanego stringa jako poprawna wartosc typu
                        }catch(Exception e)
                        {
                            System.err.println("asd"+e.toString());
                        }
                    }                    
                }
            
            setValueOptions();
        }
        
        
        
        
    }
    static int incrementId=1;                                                   //statyczne id odpowiedzialne za autoincrement
    
    String[] warunki = {"<", "<=", "=", ">=", ">"};
        //wybierz z warunkiem
        void setValue(String nazwa_pole_zmiana, Object obj_wartosc_zmieniona, String nazwa_pole_warunek, String warunek, Object obj_wartosc_warunku)
        {
            setValue(findColId(nazwa_pole_zmiana),obj_wartosc_zmieniona,findColId(nazwa_pole_warunek), warunek ,obj_wartosc_warunku);            //uruchamia drugie set value, przeciążenie funkcji
        }
        void setValue(Integer id_pole_zmiana, Object obj_wartosc_zmieniona, Integer id_pole_warunek, String warunek ,Object obj_wartosc_warunku)//zmiana wartosci pola (pod warunkiem)
        {
            if(warunek!=null)
            {
                                                                                //sprawdzanie czy warunek ma sens
                boolean dobry=false;
                for (String warunki1 : warunki) {
                    if (warunek.equals(warunki1)) {
                        dobry=true;
                        break;
                    }
                }
                if(!dobry)                                                      //jesli nie jest zadnym z warunkow
                {
                    System.out.println("Zły warunek");  
                    return;
                }
            }
            
                
            for(int i=0; i<wiersz.size(); i++)                                  //wyszstkie wiersze
            {
                for(int j=0; j<wiersz.get(i).kolumny.size(); j++)               //wszystkie kolumny
                {
                    if(id_pole_zmiana==j)
                    {
                        boolean zmien = true;
                        if(id_pole_warunek!=null && obj_wartosc_warunku!=null && warunek!=null)
                        {
                            Object objDoSpr = wiersz.get(i).kolumny.get(id_pole_warunek);
                            boolean jestLiczba = false;
                            if (objDoSpr instanceof Number && obj_wartosc_warunku instanceof Number) {
                                jestLiczba=true;
                             
                            if(jestLiczba)
                            {
                                Number q = (Number) objDoSpr;
                                Number w = (Number) obj_wartosc_warunku;
                                Integer o = q.intValue();
                                Integer p = w.intValue();
                                switch(warunek)
                                {
                                    case "<": if(o<p) zmien=true; else zmien = false; break;
                                    case "<=": if(o<=p) zmien=true; else zmien = false; break;
                                    case "=": if(o==p) zmien=true; else zmien = false; break;
                                    case ">=": if(o>=p) zmien=true; else zmien = false; break;
                                    case ">": if(o>p) zmien=true; else zmien = false; break;
                                    default: System.out.println("Zły warunek");
                                }
                            }                               
                            else
                            {
                                switch(warunek)
                                {
                                    case "=": if(objDoSpr.equals(obj_wartosc_warunku)) zmien=true; else zmien = false; break;
                                    default: System.out.println("Zły warunek. Obie komórki nie są liczbami.");
                                }
                            }
                            
                        }
                        }  
                        if(zmien)
                        {
                            wiersz.get(i).kolumny.set(j,obj_wartosc_zmieniona);
                        }
                    }                    
                }                
            }
            
        }
        //wybierz przyjmujac ze nie ma warunku
        void setValue(Integer id_pole_zmiana, Object obj_wartosc_zmieniona)     //zmiana wartości przez wywolanie funkcji z nullami na warunku
        {
            setValue(id_pole_zmiana, obj_wartosc_zmieniona,null,null,null);
        }        
        
        void pokazHelp()                                                        //pokazuje help
        {
            System.out.println("Komendy:");
            System.out.println("d - dodaj wiersz");
            System.out.println("dNN - dodaj wiersz z wartosciami NOT NULL");
            System.out.println("u - usun wiersz");
            System.out.println("uA - usun wiersze");
            System.out.println("z - zmien wartosc");
            System.out.println("zW - zmien wartosc z warunkiem");
            System.out.println("w - wyświetl tabele");
            System.out.println("h - wyświetl pomoc");
            System.out.println("x - zakończ program");
        }
        void start()                                                            //uruchomienie bazy danych
        {
            boolean isRunning = true;
            pokazHelp();
            while(isRunning)
            {
                System.out.print(">");
                switch(sc.nextLine())
                {
                    case "d":dodajWierszWszystkiePola();break;
                    case "dNN": dodajWiersz(); break;
                    case "u": System.out.println("Wpisz numer wiersza do usuniecia"); usunWiersz(sc.nextInt()); break;
                    case "uA": usunWiersze(); break;
                    case "z": zmienWartosc(); break;
                    case "zW": zmienWartoscZWarunkiem(); break;
                    case "w": wyswietlTabele(); break;
                    case "h": pokazHelp(); break;
                    case "x": isRunning=false; break;
                    default: System.out.println("Brak komendy");
                }
            }
        }
   
                                                                                //operacje
    void dodajWiersz()
    {
        Wiersz w = new Wiersz();
        wiersz.add(w);        
    }
    void dodajWierszWszystkiePola()                                             
    {      
        Wiersz w = new Wiersz();
        w.setTheRest();
        wiersz.add(w);
    }
    void usunWiersz(int id)
    {
        int idOfid=findColId("id");
        for(int i=0; i<wiersz.size(); i++)
        {
            if((int)wiersz.get(i).kolumny.get(idOfid)==id)
            {
                wiersz.remove(i);
            }
        }        
    }
    void usunWiersze()
    {
        wiersz.clear();       
    }
    void zmienWartosc()
    {
        String kolumna;
        String wartosc;
        //----------------
        System.out.println("Wprowadz nazwe kolumny do zmiany");
        kolumna = sc.nextLine();
        System.out.println("Wprowadz wartosc kolumny do zmiany");
        wartosc = sc.nextLine();
        
        //----------------
        Integer colId = findColId(kolumna);
        Object o = stringToProperType(colId, wartosc);
        setValue(colId, o);
    }
    void zmienWartoscZWarunkiem()
    {
        String kolumna;
        String wartosc;
        String kolumna_war;
        String warunek;
        String wartosc_war;
        //----------------
        System.out.println("Wprowadz nazwe kolumny do zmiany");
        kolumna = sc.nextLine();
        System.out.println("Wprowadz wartosc kolumny do zmiany");
        wartosc = sc.nextLine();
        System.out.println("Wprowadz nazwe kolumny dla warunku");
        kolumna_war = sc.nextLine();
        System.out.println("Wprowadz warunek [< <= = >= >]");
        warunek = sc.nextLine(); 
        System.out.println("Wprowadz wartosc kolumny dla warunku");
        wartosc_war = sc.nextLine();
        //----------------
        Integer colId = findColId(kolumna);
        Integer colWarId = findColId(kolumna_war);
        Object o = stringToProperType(colId, wartosc);
        Object p = stringToProperType(colWarId, wartosc_war);
        setValue(colId, o, colWarId,warunek,p);
    }
    void rysujKomorkeOWielkosci(int wiel, String tresc)                         //ustawia wielkość komórki 
    {
        int dlTekstu = tresc.length();
        if(dlTekstu>wiel)
        {
            String str = tresc.substring(0,wiel-1)+"#";
            System.out.print(str+"|");
        }else
        {
            int lewa=0, prawa=0;
            if(dlTekstu!=wiel)
            {
                lewa = (wiel-dlTekstu)/2;
                prawa = wiel - dlTekstu - lewa;
            }
            for(int i=0; i<lewa; i++)
                System.out.print(" ");
            System.out.print(tresc);
            for(int i=0; i<prawa; i++)
                System.out.print(" ");
            System.out.print("|");
        }
    }
    int sumaTabInt(int[] t)                                                     //maksymalna dlugosc wszystkich komorek
    {
        int suma=0;
        for(int i=0; i<t.length; i++)
        {
            suma+=t[i];
        }
        return suma;
    }
    void rysujKreske(int[] wielkosci)                                           //rysowanie tabeli
    {
        System.out.println("");
        for(int i=0; i<sumaTabInt(wielkosci)+wielkosci.length; i++)             //miejsca + ilosc kresek pionowych
        {
            System.out.print("-");
        }
        System.out.println("");
    }
    void wyswietlTabele()                                                       //wyswietla tabele
    {
        int[] wielkosci = {4,10,10,10,10,10,30,30,10};
        System.out.print("|");
        for(int i=0; i<nazwyPol.length; i++)
        {            
            rysujKomorkeOWielkosci(wielkosci[i] ,nazwyPol[i].nazwa);
        }
        
        rysujKreske(wielkosci);
        for(int i=0; i<wiersz.size(); i++)
        {    
            System.out.print("|");
            for(int j=0; j<nazwyPol.length; j++)
            {
                Object w=wiersz.get(i).kolumny.get(j);
                if(w==null)
                {
                    rysujKomorkeOWielkosci(wielkosci[j], "NULL");
                }else
                {
                    rysujKomorkeOWielkosci(wielkosci[j], w.toString());
                }
                
            }
            rysujKreske(wielkosci);
        }
    }
    
    
}
