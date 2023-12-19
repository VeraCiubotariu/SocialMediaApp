package ir.map.gr222.sem7.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// uses MD5 hashing algorithm
public class PasswordEncryption {
    public PasswordEncryption() {
    }

    public String encrypt(String initialPassword){
        String encryptedPassword = null;
        try{
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(initialPassword.getBytes());
            byte[] bytes = m.digest();

            StringBuilder s = new StringBuilder();
            for(int i=0;i<bytes.length;i++){
                s.append((Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)));
            }

            encryptedPassword = s.toString();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return encryptedPassword;
    }
}
