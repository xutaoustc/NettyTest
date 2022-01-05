package nio.c1;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestByteBuffer {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);

            while(true) {
                int len = channel.read(buffer);
                if(len == -1) {
                    break;
                }

                buffer.flip();  //切换为读模式
                while(buffer.hasRemaining()){
                    byte b = buffer.get();
                    System.out.println((char)b);
                }

                buffer.clear();// 切换为写模式
            }
        } catch (IOException e) {
        }
    }
}
