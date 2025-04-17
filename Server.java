import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;

public class Server {
    private Socket socket;

    public static void main(String[] args) throws Exception {
        new Server();
    }

    public Server() throws Exception {
        System.out.println("Server has started.");
        ServerSocket server = new ServerSocket(55555);
        System.out.println("Waiting for a connection...");
        socket = server.accept();
        System.out.println("Connected!");

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while(!socket.isClosed()) {
            System.out.println("Waiting to receive message...");
            String message = br.readLine();
            System.out.println("Received packet containing: "+message);

            File folder = new File("C:/Users/115423/IdeaProjects/FileTransfer/Files");
            File[] files = folder.listFiles();

            if(message.equalsIgnoreCase("list files")) {
                String fileList = "";
                for(File file:files) {
                    fileList += file.getName()+"@";
                }
                writer.println(fileList);
            }
            else if(message.startsWith("Download")) {
                String fileName = message.substring(message.indexOf(" ")+1);
                boolean hasFile = false;
                for(File file:files) {
                    if(file.getName().equals(fileName)) {
                        int size = (int)file.length();
                        writer.println("File of size "+size+" byte(s) exists!");
                        hasFile = true;

                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                        byte[] b = new byte[1024];
                        while(size>0) {
                            System.out.println("Bytes remaining to send: "+size);
                            bis.read(b,0,Math.min(size,b.length));
                            bos.write(b,0,Math.min(size,b.length));
                            bos.flush();
                            size -= Math.min(size,b.length);
                        }

                        bis.close();
                        System.out.println("File has been downloaded by the client.");
                        break;
                    }
                }
                if(!hasFile) {
                    writer.println("File does not exist.");
                }
            }
            else {
                writer.println("Command not recognized. Please try again.");
            }
        }
    }
}
