package cu.uci.coj.Application;

import android.content.Context;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cu.uci.coj.Application.Exceptions.NoLoginFileException;

/**
 * Created by osvel on 3/8/16.
 */
public class LoginData implements Serializable {
    private static String FILE_NAME = "user_data";

    private String user;
    private String pswd;
    private String token;

    /**
     * Create new LoginData object with user, password for encryption
     *
     * @param context Context for generate key
     * @param user User
     * @param pswd Password for encrypt and save
     * @param token User token after login
     */
    public LoginData(Context context, String user, String pswd, String token) {
        this.user = user;
        String key = KeyGenerator.generate(context);
        this.pswd = encrypt(pswd, key);
        this.token = token;
    }

    /**
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Encrypt password
     *
     * @param pswd to ecrypt
     * @param key secret for encrypt
     * @return encrypted password
     */
    private String encrypt(String pswd, String key){
        try {

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(pswd.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b: encrypted) {
                sb.append((char)b);
            }

            // the encrypted String
            return sb.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decrypt password with key
     *
     * @return decrypted password
     */
    public String decrypt(Context context){

        String key = getKey(context);

        try {
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            // now convert the string to byte array
            // for decryption
            byte[] bb = new byte[pswd.length()];
            for (int i = 0; i < pswd.length(); i++) {
                bb[i] = (byte) pswd.charAt(i);
            }

            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(bb));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save object on disc
     *
     * @param context for application write file
     * @return password saved or not
     */
    public boolean save(Context context){

        ObjectOutputStream oos;
        FileOutputStream fout;
        try{
            fout = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * Delete LoginFile
     *
     * @param context Application context
     * @return file deleted
     */
    public static boolean delete(Context context){

        try {
            String dir = context.getFilesDir().getAbsolutePath();
            new File(dir, FILE_NAME).delete();
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Read LoginData file
     *
     * @param context for application read file
     * @return LoginData object
     */
    public static LoginData read(Context context) throws NoLoginFileException {

        try {

            FileInputStream streamIn = context.openFileInput(FILE_NAME);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            LoginData loginData = (LoginData) objectinputstream.readObject();
            objectinputstream.close();
            return loginData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoLoginFileException();
    }

    public String getKey(Context context) {
        return KeyGenerator.generate(context);
    }

    public static class KeyGenerator{

        /**
         * Generate key for encrypt. The key is based on ANDROID_ID and private secret key
         *
         * @param context for get ANDROID_ID
         * @return security key
         */
        public static String generate(Context context){

            String key = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID) + "S3cur1tyK3y#1";

            while (key.length() < 32)
                key = "O"+key;

            return key;
        }

    }
}
