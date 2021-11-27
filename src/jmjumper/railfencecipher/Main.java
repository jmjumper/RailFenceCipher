package jmjumper.railfencecipher;

// Startingpoint, hier beginnt der Spaß
public class Main {

    public static void main(String[] args) {
        RailFenceCipher run = new RailFenceCipher();
        String plaintext = run.inputPlaintext();
        String encrypted = run.encrypt(plaintext);
        System.out.println(run.forDecryption(encrypted));
    }

}
