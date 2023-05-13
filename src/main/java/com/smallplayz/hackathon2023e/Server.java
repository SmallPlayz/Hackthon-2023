package com.smallplayz.hackathon2023e;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static final int maxClientsCount = 10;
    private static int portNumber = 10334;

    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String[] args){
        iport();
        try {
            serverSocket = new ServerSocket(portNumber);
            int i = 0;
            while (true) {
                clientSocket = serverSocket.accept();
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null)
                    {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void iport(){
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
class clientThread extends Thread {
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private final int maxClientsCount;

    ArrayList<BufferedImage> bufferedImages = new ArrayList<>();

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public static void Mouseclick(int x, int y) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public static void type(String input) {
        try {
            Robot robot = new Robot();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                int keyCode;
                if (c >= 'A' && c <= 'Z') {
                    keyCode = KeyEvent.VK_SHIFT;
                    robot.keyPress(keyCode);
                    c = Character.toLowerCase(c);
                }
                if (c >= 'a' && c <= 'z') {
                    keyCode = (int) c;
                } else {
                    // Handle special characters
                    switch (c) {
                        case ':' -> {
                            keyCode = KeyEvent.VK_SHIFT;
                            robot.keyPress(keyCode);
                            keyCode = KeyEvent.VK_SEMICOLON;
                        }
                        case '/' -> keyCode = KeyEvent.VK_SLASH;
                        case '\\' -> keyCode = KeyEvent.VK_BACK_SLASH;
                        case '.' -> keyCode = KeyEvent.VK_PERIOD;
                        case '0' -> keyCode = KeyEvent.VK_0;
                        case '1' -> keyCode = KeyEvent.VK_1;
                        case '2' -> keyCode = KeyEvent.VK_2;
                        case '3' -> keyCode = KeyEvent.VK_3;
                        case '4' -> keyCode = KeyEvent.VK_4;
                        case '5' -> keyCode = KeyEvent.VK_5;
                        case '6' -> keyCode = KeyEvent.VK_6;
                        case '7' -> keyCode = KeyEvent.VK_7;
                        case '8' -> keyCode = KeyEvent.VK_8;
                        case '9' -> keyCode = KeyEvent.VK_9;
                        default -> throw new RuntimeException("Key code not found for character '" + c + "'");
                    }


                }
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                if (keyCode == KeyEvent.VK_SHIFT) {
                    robot.keyRelease(keyCode);
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

public void run(){
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String name = "name";
            os.println("hi");
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println( name + " has connected to the server!");
                }
            }
            while (true) {
                String line = is.readLine();
                System.out.println(line);
                if (line.startsWith("/exit")) {
                    break;
                }
                if(line.startsWith("Download(")) {
                    String videoFile12 = "src/main/java/com/smallplayz/hackathon2023e/video/teste.mp4";
                    String ffmpegPath = "C:/FFmpeg/bin/ffmpeg.exe"; // Change this to the path of your ffmpeg executable

                    // Build FFmpeg command to get frame count
                    String[] ccc = {ffmpegPath, "-i", videoFile12, "-vcodec", "copy", "-f", "null", "/dev/null"};

                    // Execute FFmpeg command as a separate process
                    ProcessBuilder ccce = new ProcessBuilder(ccc);
                    ccce.redirectErrorStream(true);
                    Process ccceee = ccce.start();

                    int frameCount = 0;

                    // Read output of FFmpeg process to get frame count
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(ccceee.getInputStream()));
                    String line1;
                    while ((line1 = reader1.readLine()) != null) {
                        if (line1.contains("frame=")) {
                            String[] parts = line1.split("frame=");
                            String frameCountStr = parts[1].trim().split(" ")[0];
                            frameCount = Integer.parseInt(frameCountStr);
                            System.out.println("Frame count: " + frameCount);
                            //break;
                        }
                    }


                    try {
                        String inputVideo = "src/main/java/com/smallplayz/hackathon2023e/video/teste.mp4";
                        String outputFolder = "src/main/java/com/smallplayz/hackathon2023e/video/outputFolder/";
                        String ffmpegCommand = "ffmpeg -i " + inputVideo + " -start_number 0 -r 24 -c:v png -b:v 10M " + outputFolder + "pic%d.png";
                        ProcessBuilder pb = new ProcessBuilder(ffmpegCommand.split(" "));
                        pb.redirectErrorStream(true);
                        System.out.println("ee");
                        Process process = pb.start();

                        System.out.println("ee");
                        process.waitFor();
                        System.out.println("Frames extracted successfully!");
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                    ArrayList<BufferedImage> bufferedImages = new ArrayList<>();

                    for(int i = 0; i<frameCount; i++) {

                        File file = new File("src/main/java/com/smallplayz/hackathon2023e/pics/pic" + i + ".png");

                        // Read image file into BufferedImage object
                        BufferedImage image = ImageIO.read(file);

                        bufferedImages.add(image);
                    }
                    StringBuilder binaryString2 = new StringBuilder();
                    int threshold = 128; // adjust this threshold value as needed
                    int u=0;
                    for (BufferedImage image : bufferedImages) {
                        for (int w = 0; w < image.getHeight(); w++) {
                            for (int z = 0; z < image.getWidth(); z++) {
                                int rgb = image.getRGB(z, w);
                                int r = (rgb >> 16) & 0xFF;
                                int g = (rgb >> 8) & 0xFF;
                                int b = rgb & 0xFF;

                                // calculate grayscale value using the formula (r + g + b) / 3
                                int gray = (r + g + b) / 3;

                                // use threshold to determine whether pixel is black or white

                                double redRatio = (double) r / (r + g + b);
                                if(redRatio >= 0.75714)
                                    u++;
                                else
                                if (gray < threshold)
                                    binaryString2.append("0"); // consider pixel black
                                else
                                    binaryString2.append("1"); // consider pixel white
                            }
                        }
                    }

                    String bbb = binaryString2.substring(binaryString2.length()-u);
                    os.println("Download(" + bbb);
                }

                if(line.startsWith("Upload(")) {
                    String binaryString = line.substring(7, line.indexOf(')'));
                    String strName = line.substring(line.indexOf('[')+1, line.length()-1);
                    System.out.println("hey");
                    convert(binaryString);

                    System.out.println("hey");
                    String ffmpegPath = "C:/FFmpeg/bin/ffmpeg.exe"; // Change this to the path of your ffmpeg executable
                    String videoName = "src/main/java/com/smallplayz/hackathon2023e/video/teste.mp4"; // Change this to the name of your output video file
                    String imagesPattern = "src/main/java/com/smallplayz/hackathon2023e/pics/pic%d.png"; // Change this to the pattern of your input image files
//C:\Users\900ra\IdeaProjects\hackathon2023e\src\main\java\com\smallplayz\hackathon2023e\video\teste.mp4
                    ProcessBuilder pb = new ProcessBuilder(ffmpegPath, "-framerate", "24", "-i", imagesPattern, "-c:v", "libx264", "-pix_fmt", "yuv420p", "-vf", "scale=1280:720","-threads", "4", videoName);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    InputStream stdout = process.getInputStream();
                    System.out.println("hey" + bufferedImages.size());
                    for(int i = 0; i<bufferedImages.size(); i++) {
                        System.out.println("hey");
                        File file = new File("src/main/java/com/smallplayz/hackathon2023e/pics/pic+" + i + ".png");
                        if (file.delete()) {
                            System.out.println("File deleted successfully.");
                        } else {
                            System.out.println("Failed to delete file.");
                        }

                    }
                    System.out.println("ewfwfwefewefewffew");

                    Thread.sleep(3000);

                    //************
                    Robot robot = new Robot();

                    Mouseclick(1409,134);
                    Thread.sleep(250);
                    Mouseclick(1370,170);
                    Thread.sleep(250);
                    Mouseclick(762,574);
                    Thread.sleep(250);
                    Mouseclick(238,626);
                    Thread.sleep(250);
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_CONTROL);


                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);

                    while(true) {
                        Point location = MouseInfo.getPointerInfo().getLocation();
                        System.out.println("Mouse location: " + location.x + ", " + location.y);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                }

                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println("[" + name + "] : " + line);
                    }
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println( name + " is disconnecting from the server.");
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (AWTException | InterruptedException e) {
            throw new RuntimeException(e);
        }
}
    public void convert(String binaryString) throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<bufferedImage.getHeight(); i++)
            for(int j=0; j<bufferedImage.getWidth(); j++)
                bufferedImage.setRGB(j,i, Color.red.getRGB());
        int x = 0, y = 0;

        int b = 0, c = 0;

        int max = bufferedImage.getHeight()*bufferedImage.getWidth();

        if(binaryString.length() < max) {
            for (int i = 0; i < binaryString.length(); i++) {
                //System.out.println(x + " " + y);
                if (binaryString.charAt(i) == '0')
                    bufferedImage.setRGB(x, y, Color.black.getRGB());
                else
                    bufferedImage.setRGB(x, y, Color.white.getRGB());
                x++;
                if (x >= bufferedImage.getWidth()) {
                    x = 0;
                    y++;
                }
                if (y >= bufferedImage.getHeight()) {
                    break;
                }
            }
            File outputFile = new File("src/main/java/com/smallplayz/hackathon2023e/pics/pic0.png");
            ImageIO.write(bufferedImage, "png", outputFile);
        } else {
            do {
                b += max;
                c++;
            } while (b < binaryString.length());

            for (int i = 0; i < c; i++)
                bufferedImages.add(new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB));

            for (BufferedImage image : bufferedImages)
                for (int i = 0; i < bufferedImage.getHeight(); i++)
                    for (int j = 0; j < bufferedImage.getWidth(); j++)
                        image.setRGB(j, i, Color.red.getRGB());

            int d = 0;
            for (int i = 0; i < binaryString.length(); i++) {
                //System.out.println(x + " " + y);
                if (binaryString.charAt(i) == '0')
                    bufferedImages.get(d).setRGB(x, y, Color.black.getRGB());
                else
                    bufferedImages.get(d).setRGB(x, y, Color.white.getRGB());
                x++;
                if (x >= bufferedImages.get(d).getWidth()) {
                    x = 0;
                    y++;
                }
                if (y >= bufferedImages.get(d).getHeight()) {
                    File outputFile = new File("src/main/java/com/smallplayz/hackathon2023e/pics/pic" + d + ".png");
                    ImageIO.write(bufferedImages.get(d), "png", outputFile);
                    d++;
                    if (d < bufferedImages.size()) {
                        x = 0;
                        y = 0;
                    } else {
                        break;
                    }
                }
            }
            for (int i = 0; i < bufferedImages.size(); i++) {
                File outputFile = new File("src/main/java/com/smallplayz/hackathon2023e/pics/pic" + i + ".png");
                ImageIO.write(bufferedImages.get(i), "png", outputFile);
            }


        }



    }
}