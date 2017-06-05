package github.stuhua.nio.utils;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by liulh on 2017/6/2 18:35 星期五
 */

public class RWTest {
    public void read() {
        try {
            RandomAccessFile aFile = new RandomAccessFile("f:\\test.txt", "rw");
            FileChannel inChannel = aFile.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(48);
            int bytesRead = inChannel.read(buffer);
            while (bytesRead != -1) {
                Log.d("RWTest", bytesRead + "");
                buffer.flip();
                while (buffer.hasRemaining()){
                    Log.d("RWTest", (char) buffer.get() + "");
                }
                buffer.clear();
                bytesRead=inChannel.read(buffer);
            }
            aFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
}
