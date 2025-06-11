package n1luik.KAllFix.debug;

import java.io.*;
import java.util.Arrays;

//@AllArgsConstructor

public class ZipPacketTest$FF {
    public int pos;
    public final int lenMax;
    private final DataInputStream in;
    private byte[] bytearr = new byte[80];
    private char[] chararr = new char[80];

    public ZipPacketTest$FF(int pos, int lenMax, DataInputStream in) {
        this.pos = pos;
        this.lenMax = lenMax;
        this.in = in;
    }

    public byte readByte() throws IOException {
        if (pos >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-byte");
        pos++;
        return in.readByte();
    }
    public int[] readIntArray() throws IOException {
        if (pos+4 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-intArray");
        pos+=4;
        int len = in.readInt();
        if (len < 0)throw new RuntimeException("在["+pos+"]数据长度不应该小于0："+len);
        if (len == 0)return new int[0];
        if (pos+(len*4) > lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-intArray");
        pos+=(len*4);
        int[] ints = new int[len];
        for (int i = 0; i < len; i++) {
            ints[i] = in.readInt();
        }
        return ints;
    }
    public long[] readLongArray() throws IOException {
        if (pos+4 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-longArray");
        pos+=4;
        int len = in.readInt();
        if (len < 0)throw new RuntimeException("在["+pos+"]数据长度不应该小于0："+len);
        if (len == 0)return new long[0];
        if (pos+(len*8) > lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-longArray");
        pos+=(len*8);
        long[] ints = new long[len];
        for (int i = 0; i < len; i++) {
            ints[i] = in.readLong();
        }
        return ints;
    }
    public String readUTF() throws IOException {
        int utflen = readUnsignedShort();
        byte[] bytearr;
        char[] chararr;
        if (this.bytearr.length < utflen){
            this.bytearr = new byte[utflen*2];
            this.chararr = new char[utflen*2];
        }
        chararr = this.chararr;
        bytearr = this.bytearr;

        int c, char2, char3;
        int count = 0;
        int chararr_count=0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++]=(char)c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0, 1, 2, 3, 4, 5, 6, 7 -> {
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++]=(char)c;
                }
                case 12, 13 -> {
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                                "malformed input around byte " + count);
                    chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                }
                case 14 -> {
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = bytearr[count-2];
                    char3 = bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                                "malformed input around byte " + (count-1));
                    chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6)  |
                            ((char3 & 0x3F) << 0));
                }
                default ->
                    /* 10xx xxxx,  1111 xxxx */
                        throw new UTFDataFormatException(
                                "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        pos+=utflen;
        return new String(chararr, 0, chararr_count);
    }
    public int readInt() throws IOException {
        if (pos+4 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-int");
        pos+=4;
        return in.readInt();
    }
    public float readFloat() throws IOException {
        if (pos+4 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-float");
        pos+=4;
        return in.readFloat();
    }
    public long readLong() throws IOException {
        if (pos+8 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-long");
        pos+=8;
        return in.readLong();
    }
    public double readDouble() throws IOException {
        if (pos+8 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-double");
        pos+=8;
        return in.readDouble();
    }
    public short readShort() throws IOException {
        if (pos+2 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-short");
        pos+=2;
        return in.readShort();
    }
    public int readUnsignedShort() throws IOException {
        if (pos+2 >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-short");
        pos+=2;
        return in.readUnsignedShort();
    }
    public byte[] readNBytes(int len) throws IOException {
        if (pos+len >= lenMax)throw new RuntimeException("在【"+pos+"】位置超过了文件大小-short");
        pos+=len;
        return in.readNBytes(len);
    }
    public static void main(String[] args) throws IOException {//readRoot
        byte[] bytes = new FileInputStream(args[0]).readAllBytes();
        ZipPacketTest$FF t = new ZipPacketTest$FF(0, bytes.length, new DataInputStream(new ByteArrayInputStream(bytes)));
        while (true) {
            switch (t.readByte()) {
                case 1 -> {
                    switch (t.readByte()) {
                        case 1 -> readMoreBlockUp(t);
                        case 2 -> readBlockUp(t);
                        default -> throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的id-小区块");
                    }
                }
                case 2 -> readMoreBlockEntityUpAuto(t);
                case 3 -> {
                    if (t.pos == t.lenMax){
                        System.out.println("正常退出");
                    }else {
                        throw new RuntimeException("在["+t.pos+"]位置没有完全读取");
                    }
                    return;
                }
                default -> throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的id-readRoot");
            }
        }

    }

    public static void readMoreBlockEntityUpAuto(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("readMoreBlockEntityUpAuto");

        int rp = 0;
        short len = t.readShort();
        if (len < 0){
            throw new RuntimeException("在["+t.pos+"]数据长度不应该小于0："+len);
        }else if (len == 0){
            System.out.println("警告这个实体方块的数据列表大小是0");
        }
        try{
            for (; rp < len; rp++) {
                readMoreBlockEntityUp(t);
            }
        }catch (Throwable e){
            throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的异常："+e.getMessage()+
                    """
                    rp: %s
                    len： %s
                    start： %s
                    """.formatted(rp, len, start)
                    , e);
        }
        System.out.printf("readMoreBlockEntityUpAuto正常退出-start[%s], end[%s]%n", start, t.pos);

    }
    public static void readMoreBlockEntityUp(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("readMoreBlockEntityUp");
        System.out.println(pos48(t));
        System.out.printf("id: %s%n", t.readInt());
        if (t.readByte() != 0){
            System.out.println("nbt--------------");
            printNbt(t, true);
            System.out.println("nbt^^^^^^^^^^^^^^^");
        }else {
            System.out.println("没有nbt");
        }

        System.out.printf("readMoreBlockEntityUp正常退出-start[%s], end[%s]%n", start, t.pos);
    }

    public static void nbtCompoundTagType(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("[CompoundTag]");

        int rp = 0;

        try{
            byte dt;
            while ((dt = t.readByte()) != 0){
                System.out.printf("[%s]=", t.readUTF());
                nbtType(dt, t);
            }
            System.out.printf("<<<<-start[%s], end[%s]%n", start, t.pos);
        }catch (Throwable e){
            throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的异常："+e.getMessage()+
                    """
                    rp: %s
                    start： %s
                    """.formatted(rp, start)
                    , e);
        }

    }
    public static void nbtType(byte id, ZipPacketTest$FF t) throws IOException {
        switch (id) {
            case 0 -> {
                System.out.printf("警告在[%s]出现了nbt0%n",  t.pos-1);
                return;
            }
            case 1 -> {
                System.out.printf("Byte: %s%n", t.readByte());
            }
            case 2 -> {
                System.out.printf("Short: %s%n", t.readShort());
            }
            case 3 -> {
                System.out.printf("Int: %s%n", t.readInt());
            }
            case 4 -> {
                System.out.printf("Long: %s%n", t.readLong());
            }
            case 5 -> {
                System.out.printf("Float: %s%n", t.readFloat());
            }
            case 6 -> {
                System.out.printf("Double: %s%n", t.readDouble());
            }
            case 7 -> {
                System.out.printf("Byte Array: %s%n", Arrays.toString(t.readNBytes(t.readInt())));
            }
            case 8 -> {
                System.out.printf(">>>|%s|<<<%n", t.readUTF());
            }
            case 9 -> {
                nbtList(t);
            }
            case 10 -> {
                nbtCompoundTagType(t);
            }
            case 11 -> {
                System.out.printf("Int Array: %s%n", Arrays.toString(t.readIntArray()));
            }
            case 12 -> {
                System.out.printf("Long Array: %s%n", Arrays.toString(t.readLongArray()));
            }

            default -> {
                throw new RuntimeException("在[%s]出现了不该出现的nbt类型：%s".formatted(t.pos-1, id));
            }
        }

    }
    public static void nbtList(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("[nbtList]");

        byte dt =  t.readByte();
        int len = t.readInt();
        int rp = 0;

        if (len < 0){
            throw new RuntimeException("在["+t.pos+"]数据长度不应该小于0："+len);
        }
        try{
            for (; rp < len; rp++) {
                nbtType(dt, t);
            }
            System.out.printf("nbtList正常退出-start[%s], end[%s]%n", start, t.pos);
        }catch (Throwable e){
            throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的异常："+e.getMessage()+
                    """
                    dt: %s
                    rp: %s
                    len： %s
                    start： %s
                    """.formatted(dt, rp, len, start)
                    , e);
        }

    }
    public static void printNbt(ZipPacketTest$FF t, boolean root) throws IOException {
        byte b = t.readByte();
        if (root){
            System.out.printf("-----------[%s]----------------", t.readUTF());
        }
        nbtType(b, t);
    }

    public static void readBlockUp(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("readBlockUp");
        System.out.println(pos48(t));
        System.out.printf("id: %s%n", t.readInt());

        System.out.printf("readBlockUp正常退出-start[%s], end[%s]%n", start, t.pos);
    }

    public static void readMoreBlockUp(ZipPacketTest$FF t) throws IOException {
        int start = t.pos;
        System.out.println("readMoreBlockUp");
        System.out.println(sectionOf(t.readLong()));
        int prp = 0;
        int trp = 0;
        int len = t.readInt();
        if (len < 0){
            throw new RuntimeException("在["+t.pos+"]数据长度不应该小于0："+len);
        }else if (len == 0){
            System.out.println("警告这个区块的数据大小是0");
        }
        try{
            for (;prp < len; prp++) {
                t.readShort();
            }
            for (;trp < len; trp++) {
                t.readInt();
            }
            System.out.printf("readMoreBlockUp正常退出-start[%s], end[%s]%n", start, t.pos);
        }catch (Throwable e){
            throw new RuntimeException("在["+t.pos+"]位置出现了不该出现的异常："+e.getMessage()+
                    """
                    prp: %s
                    len： %s
                    trp： %s
                    start： %s
                    """.formatted(prp, len, trp, start)
                    , e);
        }
    }

    public static int sx(long p_123214_) {
        return (int)(p_123214_ << 0 >> 42);
    }

    public static int sy(long p_123226_) {
        return (int)(p_123226_ << 44 >> 44);
    }

    public static int sz(long p_123231_) {
        return (int)(p_123231_ << 22 >> 42);
    }

    public static Pos32 sectionOf(long p_123185_) {
        return new Pos32(sx(p_123185_), sy(p_123185_), sz(p_123185_));
    }
    public static Pos32 pos48(ZipPacketTest$FF f) throws IOException {
        return new Pos32(f.readInt(), f.readInt(), f.readInt());
    }

    public static record Pos32(int x, int y, int z) {
        @Override
        public String toString() {
            return "<" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '>';
        }
    }
}
