//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vmn.application.ultilities;

import vmn.application.Main.HandlerApp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class TextSecurity {

    private static final Logger logger = Logger.getLogger(String.valueOf(TextSecurity.class));

    private static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private String share_key = null;
    private static TextSecurity mMe = null;

    public void setShareKey(String str) {
        this.share_key = str;
    }

    public void computeDigest(String input, TextSecurity.MessageDigestAlgorithm alogorithm) {
        MessageDigest msgdig;
        try {
            msgdig = MessageDigest.getInstance(alogorithm.name());
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }

        msgdig.update(input.getBytes());
        byte[] mdbytes = msgdig.digest();
    }

    public long computeCheckSum(String input, TextSecurity.CheckSumAlgorithm algorithm) {
        long sum = -1L;
        Checksum cs = null;
        if (algorithm.equals(TextSecurity.CheckSumAlgorithm.Adler32)) {
            cs = new Adler32();
        } else if (algorithm.equals(TextSecurity.CheckSumAlgorithm.CRC32)) {
            cs = new CRC32();
        }

        ((Checksum)cs).update(input.getBytes(), 0, input.length());
        sum = ((Checksum)cs).getValue();
        return sum;
    }

    public String hexEncode(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            byte byteValue = bytes[i];
            buffer.append(digits[(byteValue & 240) >> 4]);
            buffer.append(digits[byteValue & 15]);
        }

        return buffer.toString();
    }

    public byte[] hexStringToByteArray(String s) {
        if (s != null && !s.isEmpty()) {
            int len = s.length();
            byte[] data = new byte[len / 2];

            for(int i = 0; i < len; i += 2) {
                data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }

            return data;
        } else {
            return null;
        }
    }

    public byte[] encrypt(String clearText, Cipher cipher, SecretKey secretKey, IvParameterSpec ivVector) {
        Object var5 = null;

        try {
            cipher.init(1, secretKey, ivVector);
            byte[] ciphertext = cipher.doFinal(clearText.getBytes());
            return ciphertext;
        } catch (Exception var7) {
            throw new RuntimeException(var7);
        }
    }

    public String decrypt(byte[] cipherText, Cipher cipher, SecretKey secretKey, IvParameterSpec ivVector) {
        Object var5 = null;

        byte[] cleartext;
        try {
            cipher.init(2, secretKey, ivVector);
            cleartext = cipher.doFinal(cipherText);
        } catch (Exception var7) {
            throw new RuntimeException(var7);
        }

        return new String(cleartext);
    }

    public byte[] encryptPass2(String pass) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        this.computeDigest("SecretWord", TextSecurity.MessageDigestAlgorithm.MD5);
        this.computeDigest("SecretWord", TextSecurity.MessageDigestAlgorithm.SHA);
        this.computeCheckSum("SecretWord", TextSecurity.CheckSumAlgorithm.Adler32);
        this.computeCheckSum("SecretWord", TextSecurity.CheckSumAlgorithm.CRC32);
        DESedeKeySpec keyspec = new DESedeKeySpec(this.share_key.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TextSecurity.EncryptionAlgorithm.DESede.name());
        SecretKey deskey = keyfactory.generateSecret(keyspec);
        byte[] iv = new byte[]{-114, 18, 57, -100, 7, 114, 111, 90};
        IvParameterSpec ivVector = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(TextSecurity.EncryptionAlgorithm.DESede.name() + "/" + TextSecurity.EncryptionMode.CBC.name() + "/" + TextSecurity.EncryptionPadding.PKCS5Padding.name());
        Object var8 = null;

        try {
            cipher.init(1, deskey, ivVector);
            byte[] ciphertext = cipher.doFinal(pass.getBytes());
            return ciphertext;
        } catch (Exception var10) {
            throw new RuntimeException(var10);
        }
    }

    public String decryptPass2(byte[] input) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, UnsupportedEncodingException {
        if (input != null && input.length != 0) {
            this.computeDigest("SecretWord", TextSecurity.MessageDigestAlgorithm.MD5);
            this.computeDigest("SecretWord", TextSecurity.MessageDigestAlgorithm.SHA);
            this.computeCheckSum("SecretWord", TextSecurity.CheckSumAlgorithm.Adler32);
            this.computeCheckSum("SecretWord", TextSecurity.CheckSumAlgorithm.CRC32);
            DESedeKeySpec keyspec = new DESedeKeySpec(this.share_key.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TextSecurity.EncryptionAlgorithm.DESede.name());
            SecretKey deskey = keyfactory.generateSecret(keyspec);
            byte[] iv = new byte[]{-114, 18, 57, -100, 7, 114, 111, 90};
            IvParameterSpec ivVector = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TextSecurity.EncryptionAlgorithm.DESede.name() + "/" + TextSecurity.EncryptionMode.CBC.name() + "/" + TextSecurity.EncryptionPadding.PKCS5Padding.name());
            Object var8 = null;

            byte[] cleartext;
            try {
                cipher.init(2, deskey, ivVector);
                cleartext = cipher.doFinal(input);
            } catch (Exception var10) {
                System.out.println("Exception while decrypting data:" + this.hexEncode(input) + " Exception:" + var10);
                throw new RuntimeException(var10);
            }

            return new String(cleartext);
        } else {
            return null;
        }
    }

    public String Encrypt(String str) {
        String result = null;

        try {
            result = this.hexEncode(this.encryptPass2(str));
        } catch (Exception var4) {
            //logger.Error("Error while trying to Encrypt '" + str + "'");
            result = null;
        }

        return result;
    }

    public String Decrypt(String str) {
        String result = null;

        try {
            result = this.decryptPass2(this.hexStringToByteArray(str));
        } catch (Exception var4) {
            //logger. error("Error while trying to Encrypt '" + str + "'");
            result = null;
        }

        return result;
    }

    private TextSecurity() {
    }

    public static TextSecurity getInstance() {
        if (mMe == null) {
            mMe = new TextSecurity();
            mMe.share_key = "JKHFYI**(#&$jkskjdfowe;;924782po202348274029340JHUTk,.mn,.kns";
        }

        return mMe;
    }

    public static void main(String[] args) throws Exception {
        TextSecurity sec = getInstance();
        String str = "ttcacheadm156";
        String en = sec.Encrypt(str);
        System.out.println("'" + str + "' -> '" + en + "'");
        String desc = sec.Decrypt(en);
        System.out.println("'" + en + "' -> '" + desc + "'");
    }

    public static enum EncryptionPadding {
        NoPadding,
        PKCS5Padding,
        SSL3Padding;

        private EncryptionPadding() {
        }
    }

    public static enum EncryptionMode {
        NONE,
        CBC,
        CFB,
        ECB,
        OFB,
        PCBC;

        private EncryptionMode() {
        }
    }

    public static enum EncryptionAlgorithm {
        DES,
        SHA,
        DESede,
        RC2,
        RC4,
        RC5;

        private EncryptionAlgorithm() {
        }
    }

    public static enum MessageDigestAlgorithm {
        MD5,
        SHA;

        private MessageDigestAlgorithm() {
        }
    }

    public static enum CheckSumAlgorithm {
        Adler32,
        CRC32;

        private CheckSumAlgorithm() {
        }
    }
}

