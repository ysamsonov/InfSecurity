package me.academeg;

import java.util.Arrays;

/**
 * Описание
 * https://ru.wikipedia.org/wiki/Ранцевая_криптосистема_Меркла_—_Хеллмана
 * https://sites.google.com/site/anisimovkhv/learning/kripto/lecture/tema8#p83
 * https://webcache.googleusercontent.com/search?q=cache:0pgprGqnvaoJ:https://asoiu.files.wordpress.com/2010/02/d0b7d0b0d0b4d0b0d187d0b0-d0be-d180d18ed0bad0b7d0b0d0bad0b5.ppt+&cd=8&hl=ru&ct=clnk&gl=ru
 * https://ru.wikipedia.org/wiki/%D0%97%D0%B0%D0%B4%D0%B0%D1%87%D0%B0_%D0%BE_%D1%80%D0%B0%D0%BD%D1%86%D0%B5_%D0%B2_%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D0%BE%D0%B3%D1%80%D0%B0%D1%84%D0%B8%D0%B8
 */
@SuppressWarnings("unused")
public class BackpackCode {

    private static int SYMBOL_SIZE = 16;

    int q;
    int r;
    private int[] privateKey;

    private int[] publicKey;

    public BackpackCode(int[] privateKey) {
        this.privateKey = privateKey;
        generatePublicKey();
    }

    public int[] getPublicKey() {
        return publicKey;
    }

    private void generatePublicKey() {
        int totalSumPrivateKey = Arrays.stream(privateKey).sum();
        q = PrimaryNumberUtils.getPrimeNumber(totalSumPrivateKey);
        r = q / 2;
        publicKey = new int[privateKey.length];
        for (int i = 0; i < privateKey.length; i++) {
            publicKey[i] = r * privateKey[i] % q;
        }
    }

    public int[] encode(String text) {
        String binaryText = textToBinaryString(text);
        int keySize = publicKey.length;
        int[] code = new int[binaryText.length() / keySize];
        int posCode = 0;
        for (int i = 0; i < binaryText.length(); i += keySize) {
            for (int j = 0; j < keySize; j++) {
                if (binaryText.charAt(i + j) == '1') {
                    code[posCode] += publicKey[j];
                }
            }
            posCode++;
        }
        return code;
    }

    public String decode(int[] code) {
        StringBuilder builder = new StringBuilder();
        int inverseR = multiInverse(r, q);
        for (int el : code) {
            el = el * inverseR % q;
            builder.append(decodeElement(el));
        }
        return textFromBinaryString(builder.toString());
    }

    private char[] decodeElement(int el) {
        char[] bites = new char[privateKey.length];
        for (int i = 0; i < bites.length; i++) {
            bites[i] = '0';
        }
        for (int i = privateKey.length - 1; i >= 0 && el > 0; i--) {
            if (el / privateKey[i] > 0) {
                bites[i] = '1';
                el -= el / privateKey[i] * privateKey[i];
            }
        }
        return bites;
    }

    private String textFromBinaryString(String binaryText) {
        StringBuilder builder = new StringBuilder();
        while (binaryText.length() % SYMBOL_SIZE != 0) {
            binaryText = binaryText + '0';
        }
        for (int i = 0; i < binaryText.length(); i += SYMBOL_SIZE) {
            String binarySymbol = binaryText.substring(i, i + SYMBOL_SIZE);
            char c = (char) Integer.valueOf(binarySymbol, 2).shortValue();
            builder.append(c);
        }
        return builder.toString();
    }

    private String textToBinaryString(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String binary = Integer.toBinaryString((int) text.charAt(i));
            int countBits = SYMBOL_SIZE - binary.length();
            for (int j = 0; j < countBits; j++) {
                builder.append('0');
            }
            builder.append(binary);
        }

        int keySize = privateKey.length;
        if (builder.length() % keySize != 0) {
            int count = keySize - builder.length() % keySize;
            for (int i = 0; i < count; i++) {
                builder.append('0');
            }
        }
        return builder.toString();
    }

    /**
     * Стандартный алгоритм Евклида решает задачу для выражения: ax+by=d
     *
     * @return мультипликативно обратное число для a, по модулю b
     */
    private int multiInverse(long aa, long bb) {
        long a = aa;
        long b = bb;
        long x = 0;
        long y = 1;
        long lastX = 1;
        long lastY = 0;
        long temp;
        while (b != 0) {
            long q = a / b;
            long r = a % b;

            a = b;
            b = r;

            temp = x;
            x = lastX - q * x;
            lastX = temp;

            temp = y;
            y = lastY - q * y;
            lastY = temp;
        }
        return (int) ((lastX % bb + bb) % bb);
    }
}
