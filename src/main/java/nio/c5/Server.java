package nio.c5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static nio.util.ByteBufferUtil.debugRead;

public class Server {
    public static void main(String[] args) throws IOException{
        // 1. 创建Selector，管理多个channel
        Selector selector = Selector.open();

        // 2. 建立selector和channel的联系
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while(true) {
            // 3. 没有事件发生，线程阻塞，有事件，线程恢复运行
            // select在事件未处理时，它不会阻塞，事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            // 4. 处理事件，selectedKeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key时，需要从集合中删除，因为NIO不会帮我们删
                iter.remove();
                // 5. 区分事件类型
                if(key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0 ,null);
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        int read = channel.read(buffer);
                        if(read == -1){
                            key.cancel();
                        } else {
                            buffer.flip();
                            debugRead(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }

            }
        }
    }
}
