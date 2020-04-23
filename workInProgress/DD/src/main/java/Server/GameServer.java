
package  Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.*;
import java .io.*;

//Here all the players are being connected,
/*

PROTOCOLS:
    CONNECT : DEFAULT (sprint 2)
    CREATING ROOM (sprint 2)
    JOINING ROOM (sprint 2)
    JOINING EXISTING GAME (later)
 */
public class GameServer {
    private Socket socket= null;
    private ServerSocket ss = null;

    private DataInputStream in=null;
    private DataOutputStream out = null;

    ServerRooms serverRooms;

    boolean open=true;
    int port;
    public GameServer  (int port){
        //INIT VARIABLES
        serverRooms = new ServerRooms();
        this.port = port;
        try {
            ss = new ServerSocket(port);
            Thread serverThread= new Thread(new Runnable() {
                //  rcv/snd data
                JSONObject payLoad; // payload
                byte [] data = new byte[1024];// payload
                @Override
                public void run() {
                    while(open){
                        try {
                            Socket s= ss.accept();
                            System.out.println("Accepted a client");
                            Thread clientThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        in  = new DataInputStream(s.getInputStream());
                                        out = new DataOutputStream(s.getOutputStream());
                                        ProtocolHandler protocolHandler = new ProtocolHandler(serverRooms);
                                        while(open) {
                                            //reading byte
                                            //cnt stores the nr of bytes of the incoming data
                                            int cnt = in.read(data);
                                            //each charactes has a byte representation
                                            //so the strData must be a string equal to the cnt value, no more ,no less
                                            //cuz the Json parsing does excetions otherwise cuz he starts reading nonsense
                                            byte[] actualData = new byte[cnt];
                                            for(int i =0;i<cnt;i++){
                                                actualData[i] = data[i];
                                            }
                                            //converting to string
                                            String strData = new String(actualData, "UTF-8");
                                            try {
                                                payLoad = (JSONObject) new  JSONParser().parse(strData);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            payLoad  = protocolHandler.response(payLoad);
                                            byte [] toSend = payLoad.toString().getBytes();
                                            out.write(toSend);
                                        }
                                        s.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            clientThread.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            })  ;
            serverThread.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
