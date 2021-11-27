package jmjumper.railfencecipher;

import java.util.Scanner;

// Diese Klasse implementiert die Rail Fence Cipher
public class RailFenceCipher {

    private String encryption[][];
    private Scanner sc;
    private int plain_length, k_bottom;

    public String inputPlaintext () {
        sc = new Scanner(System.in);
        System.out.println("Enter the plaintext to be encrypted: ");
        return sc.nextLine();
    }

    public String encrypt (String plain) {
        if (plain.isEmpty()) {
            System.out.println("Something went wrong while encrypting. Plaintext might be empty.");
            return "ERROR";
        }

        plain = plain.replaceAll("\\s+",""); // Delete all whitespaces
        // Choose the cipher's key
        plain_length = plain.length();
        System.out.println("What do you want to be the cipher's key? (This key will be needed to decrypt the message.)");
        int key = sc.nextInt();
        while (key > plain.length() / 2 || key == 0) {
            System.out.println("Zu groß. Key darf nur kleiner als die Hälfte des zu verschlüsselen Texts sein.");
            key = sc.nextInt();
        }
        System.out.println("Encrypting with the following cipher's key: " + key);

        // Adjust length to be a mutliple of K (a component for decryption depends on key and length)
        k_bottom = 2 * (key - 1); // k_bottom is part of k (k will be used to split the encrypted text)
        plain = makeMultiple(k_bottom, plain);

        // Start encryption
        encryption = new String[plain_length][key];
        int key_pos = 0;
        boolean inc = true;
        StringBuilder encryptedText = new StringBuilder();

        for (int i = 0; i<plain_length; i++) {
            String currentStringOnPos = String.valueOf(plain.charAt(i));
            encryption[i][key_pos] = currentStringOnPos;

            if (key_pos == key - 1) inc = false;
            if (key_pos == 0) inc = true;
            key_pos += calcKeyPos(key_pos, inc);
        }
        System.out.println("... done with the encryption: ");

        for (int i = 0; i<key; i++) {
            for (String[] strings : encryption) {
                if (strings[i] != null) encryptedText.append(strings[i]);
            }
        }
        System.out.println(encryptedText.toString());
        return encryptedText.toString();
    }

    private int calcKeyPos (int key_pos, boolean isInc) {
        if (isInc) return 1;
        else return -1;
    }

    private StringBuilder decryption (String encryptedText, int key) {
        System.out.println("Start to decrypt...");
       int k = encryptedText.length() / ( 2 * ( key - 1 ) ); // k is a component for decryption
        String firstPart = encryptedText.substring( 0, k );
        String lastPart = encryptedText.substring( encryptedText.length() - ( k ), encryptedText.length() );
        encryptedText = encryptedText.replace(firstPart, "");
        encryptedText = encryptedText.replace(lastPart, "");
        // System.out.println("FirstPart: " + firstPart);
        // System.out.println("LastPart: " + lastPart);
        // System.out.println("Insgesamt: " + encryptedText);

        int howOftenToSplit = timesMultiple(encryptedText, 2 * k); // Könnte eigentlich auch key minus 2 sein, weil ja first- und last-Part schon ein Teil sind und es maximal key Parts geben kann
        String splittedIntermediate[] = new String[howOftenToSplit];
        for (int i = 0; i<howOftenToSplit; i++) {
            splittedIntermediate[i] = encryptedText.substring(0, 2 * k);
            encryptedText = encryptedText.replace(splittedIntermediate[i], "");
        }
        String decryptionArray[][] = new String[splittedIntermediate[0].length()][key];

        for ( int i = 1; i<= howOftenToSplit; i++ ) {
            int first_last_counter = 0;
            for ( int j = 0; j<splittedIntermediate[0].length(); j++) {
                if ( j % 2 == 0 && first_last_counter < firstPart.length() ) {
                    decryptionArray[j][0] = String.valueOf(firstPart.charAt(  first_last_counter ));
                    decryptionArray[j][key - 1] = String.valueOf(lastPart.charAt( first_last_counter ) );
                    first_last_counter++;
                } else {
                    decryptionArray[j][0] = "-";
                    decryptionArray[j][key - 1] = "-";
                }
                decryptionArray[j][i] = String.valueOf(splittedIntermediate[i - 1].charAt(j));
            }
        }

        StringBuilder decryptedText = new StringBuilder();

        try {
            for (int i = 0; i < splittedIntermediate[0].length(); i++) {
                if (i % 2 == 0) {
                    for (int j = 0; j < key; j++) {
                        if (!decryptionArray[i][j].equals("-") && decryptionArray[i][j] != null)
                            decryptedText.append(decryptionArray[i][j]);
                    }
                } else {
                    for (int j = key - 1; j >= 0; j--) {
                        if (!decryptionArray[i][j].equals("-") && decryptionArray[i][j] != null)
                            decryptedText.append(decryptionArray[i][j]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler. Key zu groß.");
        }
        System.out.println("Done with decryption: " + decryptedText);
        return decryptedText;
    }

    private int timesMultiple (String toCheck, int two_k) {
        return toCheck.length() / two_k;
    }

    // makeMultiple checks, if plain_length is a multiple of 2(key - 1) and adjust the length, if necessary.
    private String makeMultiple (int k_bottom, String plain) {
        if (plain_length % k_bottom == 0) return plain;
        else {
            plain += '?';
            plain_length++;
            return makeMultiple(k_bottom, plain);
        }
    }

    public StringBuilder forDecryption (String encrypted) {
        System.out.println("Provide the Cipher's key:");
        int keyInput = sc.nextInt();
        if (keyInput > encrypted.length() / 2 || keyInput == 0) {
            System.out.println("Zu groß. Key darf nur kleiner als die Hälfte des zu entschlüsselen Texts sein.");
            forDecryption(encrypted);
        }
        sc.close();
        return decryption(encrypted, keyInput);
    }

}
