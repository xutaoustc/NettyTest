package netty.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TransferBetweenTwoChannel {
    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            // 零拷贝，最多2g数据
            long size = from.size();
            for(long left = size; left > 0; ){
                left = left - from.transferTo(size - left, left, to);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
