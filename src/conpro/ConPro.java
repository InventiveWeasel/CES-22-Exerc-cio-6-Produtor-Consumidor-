/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conpro;

import java.io.FileInputStream;

/**
 *
 * @author Gabriel Brito
 */
public class ConPro {
    
    
    public static void main(String[] args) {
        Buffer b = new Buffer();
        Produtor produtor1 = new Produtor(1, "producao1.txt", b);
        Consumidor consumidor1 = new Consumidor(1, b);
        Consumidor consumidor2 = new Consumidor(2, b);
        Consumidor consumidor3 = new Consumidor(3, b);
        Consumidor consumidor4 = new Consumidor(4, b);
        Consumidor consumidor5 = new Consumidor(5, b);
        Consumidor consumidor6 = new Consumidor(6, b);
        Consumidor consumidor7 = new Consumidor(7, b);
        
        produtor1.start();
        consumidor1.start();
        consumidor2.start();
        consumidor3.start();
        consumidor4.start();
        consumidor5.start();
        consumidor6.start();
        consumidor7.start();
    }
    
    static class Buffer{
        final int MAXSIZE = 512;
        char conteudo[];
        int count, first, last;
        
        Buffer(){
            conteudo = new char[MAXSIZE];
            count = 0;
            first = last = 0;
        }
        
        public synchronized char get(int id){
            while(count == 0){
                try{
                    System.out.println("O consumidor "+Integer.toString(id)+" esta esperando...");
                    wait();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            char p;
            p = conteudo[first];
            first = (first+1)%MAXSIZE;
            count--;
            System.out.println("O consumidor "+Integer.toString(id)+" consumiu: "+p);
            notifyAll();
            return p;
        }
        
        public synchronized void put(int id, char p){
            while(count == MAXSIZE){
                try{
                    System.out.println("O produtor "+Integer.toString(id)+"esta esperando...");
                    wait();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            conteudo[last] = p;
            last = (last+1)%MAXSIZE;
            System.out.println("O produtor "+Integer.toString(id)+" produziu "+p);
            count++;
            notifyAll();
        }
    }
    
    static class Produtor extends Thread{
        Buffer b;
        int iD;
        FileInputStream producao;
        
        Produtor(int id, String producao, Buffer b){
            this.b = b;
            this.iD = id;
            try{
                this.producao = new FileInputStream(producao);
            }catch(Exception e){
                System.err.println("Não conseguiu criar a stream do produtor "+Integer.toString(iD)+" a partir do arquivo "+producao);
            }
        }
        
        public void run(){
            int x;
            try{
                while((x = producao.read()) != -1)
                    b.put(iD, (char) x);
            }catch(Exception e){
                System.err.println("Não conseguiu ler o arquivo do produtor "+Integer.toString(iD));
            }
        }
    }
    
    static class Consumidor extends Thread{
        int id;
        Buffer b;
        
        Consumidor(int id, Buffer b){
            this.id = id;
            this.b = b;
        }
        
        public void run(){
            char x;
            x = b.get(id);
        }
    }
}
