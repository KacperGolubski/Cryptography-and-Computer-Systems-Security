import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;

public class Solution {

    static char is_lower_upper(char currentChar){
        if(Character.isLowerCase(currentChar)){
            return 'a';
        } else {
            return 'A';
        }
    }
    static String decryptMessage(String message, String key) {
        StringBuilder result = new StringBuilder();
        int indexOfKey = 0;
        int keyLength = key.length();

        for(int i = 0; i < message.length(); i++){
            char currentChar = message.charAt(i);
            if(Character.isLetter(currentChar)){
                int keyValue = key.charAt(indexOfKey) - 'a';
                int position = (currentChar - is_lower_upper(currentChar) - keyValue + 26) % 26;
                char vigenere = (char)(position + is_lower_upper(currentChar));
                result.append(vigenere);
                indexOfKey = (indexOfKey + 1) % keyLength;
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    static double calculateIC(String text) {
        double N = 0;
        double IC = 0;
        HashMap<Character, Integer> map = new HashMap<>();
        
        for (int i = 0; i < text.length(); i++ ){
            char c = text.charAt(i);
            if(Character.isLetter(c)){
                char letter = Character.toLowerCase(c);
                if(map.containsKey(letter)){
                    map.put(letter, map.get(letter) + 1);
                } else map.put(letter, 1);
                N++;
            }
        }
        for(Integer v : map.values()){
            IC += v * (v - 1) / (N * (N - 1));
        } 
        return IC;
    }

    static TreeSet<Integer> getKasiskiDividers(String encoded) {
        int d=0;
        HashMap<String, Integer> map = new HashMap<>();
        Set<Integer> distance_list = new HashSet<>();
        TreeSet<Integer> divider_list = new TreeSet<>();
        String encoded_lower = encoded.toLowerCase();
        String encoded_lower_wo_space = encoded_lower.replaceAll("[^a-z]", "");
        for (int i = 0; i + 2 < encoded_lower_wo_space.length(); i++){
            String substring_s = encoded_lower_wo_space.substring(i, i+3);
            if(map.containsKey(substring_s)){
                d = i - map.get(substring_s);
                distance_list.add(d);
            }
            map.put(substring_s, i);
        } 
        
        for (Integer distance : distance_list){
            for(int i = 2; i <= 10; i++){
                if(distance % i == 0){
                    divider_list.add(i);
                }
            }
        }
        return divider_list;
    }

    static int findBestCaesarShift(String column) {
        double[] englishFreqs = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,
            0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 
            0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 
            0.00978, 0.02360, 0.00150, 0.01974, 0.00074
        };

        int bestShift = 0;
        double minChiSquare = Double.MAX_VALUE;

        for (int shift = 0; shift < 26; shift++) {
            double chiSquare = 0;
            int[] counts = new int[26];
            int totalLetters = 0;

            for (int i = 0; i < column.length(); i++) {
                char c = column.charAt(i);
                if (Character.isLetter(c)) {
                    int decryptedCharIdx = (Character.toLowerCase(c) - 'a' - shift + 26) % 26;
                    counts[decryptedCharIdx]++;
                    totalLetters++;
                }
            }

            if (totalLetters > 0) {
                for (int i = 0; i < 26; i++) {
                    double expected = totalLetters * englishFreqs[i];
                    if (expected > 0) {
                        chiSquare += Math.pow(counts[i] - expected, 2) / expected;
                    }
                }
            }

            if (chiSquare < minChiSquare) {
                minChiSquare = chiSquare;
                bestShift = shift;
            }
        }
        return bestShift;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String encodedString = scanner.nextLine();
        TreeSet<Integer> candidateLengths = getKasiskiDividers(encodedString);
        String bestKey = "";
        String bestDecryptedMessage = "";
        double minIcDifference = Double.MAX_VALUE;

        for (int L : candidateLengths) {

            StringBuilder[] groups = new StringBuilder[L];
            for(int i = 0; i < L; i++){
                groups[i] = new StringBuilder();
            }
            int letterIndex = 0;
            for(int i = 0; i < encodedString.length(); i++){
                char c = encodedString.charAt(i);
                if (Character.isLetter(c)) {
                    groups[letterIndex % L].append(c);
                    letterIndex++;
                }
            }

            StringBuilder candidateKeyBuilder = new StringBuilder();
            for (int i = 0; i < L; i++) {
                int shift = findBestCaesarShift(groups[i].toString());
                candidateKeyBuilder.append((char)('a' + shift));
            }
            String candidateKey = candidateKeyBuilder.toString();

            String candidateDecrypted = decryptMessage(encodedString, candidateKey);
            double currentIC = calculateIC(candidateDecrypted);
            
            double diff = Math.abs(currentIC - 0.0667);
            if (diff < minIcDifference) {
                minIcDifference = diff;
                bestKey = candidateKey;
                bestDecryptedMessage = candidateDecrypted;
            }
        }

        System.out.println(bestDecryptedMessage);
        System.out.println(bestKey);
    }
}
