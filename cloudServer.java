import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class cloudServer {

        public static void main(String[] args){
            try{
                //socket for node1
                ServerSocket ss = new ServerSocket(6656);

                Socket userPort = new Socket("localhost",6659);
                //establishes connection
                Socket s1=ss.accept();



                //calculates start time
                double start = System.currentTimeMillis();

                //generating random value(Nonce) for Gid

                Random random = new Random();

                long SID = random.nextLong();
                long d = random.nextLong();

                System.out.println(" SID =>"+SID);
                System.out.println(" d =>"+d);


                //sending id
                DataOutputStream CtrlSnd = new DataOutputStream(s1.getOutputStream());
                CtrlSnd.writeUTF(String.valueOf(SID));
                System.out.println("SID sent to Control server");

                CtrlSnd.writeUTF(String.valueOf(d));
                System.out.println("d sent to Control server");

                /*
                DataOutputStream din2 = new DataOutputStream(s2.getOutputStream());
                din2.writeUTF(String.valueOf(SID));
                System.out.println("SID sent to User");


                 */
                //receiving from control server

                DataInputStream dout=new DataInputStream(s1.getInputStream());
                String  ID_ctrl = dout.readUTF();
                System.out.println("Control server ID received from control server = "+ID_ctrl);
                String  Bsm_value = dout.readUTF();
                System.out.println("Bsm received from control server = "+Bsm_value);




                //receiving from user
                DataInputStream user_in=new DataInputStream(userPort.getInputStream());

                String  Gi=user_in.readUTF();
                System.out.println("Gi received from cloud server => "+Gi);
                String  Fi=user_in.readUTF();
                System.out.println("Fi received from cloud server => "+Fi);
                String  Zi=user_in.readUTF();
                System.out.println("Zi received from cloud server => "+Zi);
                String  PID=user_in.readUTF();
                System.out.println("PIDi received from cloud server => "+PID);
                String  Ts=user_in.readUTF();
                System.out.println("Ts received from cloud server => "+Ts);

                //generate and check timestamp
                long TS = TimeStamp.getTimeStampStatus();
                System.out.println("TimeStamp "+TS);
                long TSi = 0;
                Boolean TsStatus = TimeStamp.isValidTimestamp(TS,TSi);
                if(TsStatus == true)
                    System.out.println("Generating a random number..");
                else
                    System.out.println("Connection Rejected");

                /*
                //receiving from node2
                DataInputStream dout2=new DataInputStream(s2.getInputStream());
                String  secret2=dout2.readUTF();
                System.out.println("secret s2 received from node2 = "+secret2);



                 */
                CtrlSnd.flush();
              //  din2.flush();
                dout.close();
               // dout2.close();
                CtrlSnd.close();
                //din2.close();
                ss.close(); //closing port of node1
               // ss2.close();



                Runtime runtime = Runtime.getRuntime();
                // Run the garbage collector
                runtime.gc();



                // Calculate the used memory
                long memory = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Memory used => "+memory/1024+" KB");

                long end = System.currentTimeMillis();
                System.out.println("Total time =>  "+((end-start))+"ms");
                System.out.println("Total time =>  "+((end-start)/1000)+"sec");

            }catch(Exception e){System.out.println(e);}
        }
    }

