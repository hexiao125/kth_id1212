package se.kth.id1212.filemanage.client.view;

class ThreadSafePrint {
 
    synchronized void print(String output) {
        System.out.print(output);
    }

    synchronized void println(String output) {
        System.out.println(output);
    }
}
