import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class user {

    public static void main(String[] args){
        try{
            //socket for node1
            ServerSocket ss3 = new ServerSocket(6658);
            ServerSocket ss4 = new ServerSocket(6659);
            //establishes connection
            Socket s3= ss3.accept();
            Socket s4= ss4.accept();
            //calculates start time
            double start = System.currentTimeMillis();

            //generating random value(Nonce) for Gid

            Random random = new Random();
            int b = random.nextInt();
            int Uid = random.nextInt();
            int B_i = random.nextInt();
            int P_i = random.nextInt();

            System.out.println(" UID => "+Uid);
            System.out.println(" Biometric => "+B_i);
            System.out.println("password => "+P_i);


            String PID = Hash.toHexString(Hash.getSHA( String.valueOf( Uid+b ) ));
            System.out.println("PID => "+PID);

            String hash_Bi=((Hash.toHexString(Hash.getSHA(String.valueOf(B_i)))).substring(0,5));
            long Ai =Long.parseLong(String.valueOf(P_i)) ^ ((Integer.parseInt(hash_Bi,16)));
            System.out.println("Ai => "+Ai);

            long Omega = b ^ Ai;
            System.out.println("Omega => "+Omega);

            //Sending
            DataOutputStream dout=new DataOutputStream(s3.getOutputStream());
            dout.writeUTF( String.valueOf( Uid ) );
            dout.writeUTF(String.valueOf(PID));
            dout.writeUTF(String.valueOf(Ai));


            //receiving from control server

            DataInputStream din=new DataInputStream(s3.getInputStream());
            String  Ci = din.readUTF();
            System.out.println("Ci received from control server = "+Ci);
            String  Ei = din.readUTF();
            System.out.println("Ei received from control server = "+Ei);
            String Hi = din.readUTF();
            System.out.println("H(i) => "+Hi);


            //generate ID',Pi',Bi'
            long Ai_dash = P_i ^ Long.parseLong(hash_Bi,16);
            System.out.println("Ai' => "+Ai_dash);
            String ci_dash = Hash.toHexString(Hash.getSHA( String.valueOf( Uid+Ai_dash ) ));
            System.out.println("ci' =>"+ci_dash);
            long N =random.nextLong();
            System.out.println("Random number N => "+N);

            long Sid_m =random.nextLong();
            System.out.println("Chosen Sid_m  => "+Sid_m);

            //generate and check timestamp
            long TS = TimeStamp.getTimeStampStatus();
            System.out.println("TimeStamp "+TS);
            long TSi = 0;
            Boolean TsStatus = TimeStamp.isValidTimestamp(TS,TSi);
            if(TsStatus == true)
                System.out.println("Generating a random number..");

            else
                System.out.println("Connection Rejected");

            //Generating b,PIDi,Di,Gi,Fi,Zi

            long b_i = Omega ^ Ai;
            System.out.println("bi =>"+b_i);
            long D_i = Long.parseLong(Ei) ^ Ai;
            System.out.println("Di =>"+D_i);
            String G_i = Hash.toHexString(Hash.getSHA( String.valueOf( PID+Sid_m+N+TS+D_i) ));
            System.out.println("Gi =>"+G_i);
            long F_i = D_i ^ N;
            System.out.println("F_i => "+F_i);
            long part_z = (Long.parseLong(Hash.toHexString(Hash.getSHA( String.valueOf( D_i + N) )).substring( 0,5),16));
            long Z_i = Sid_m ^ part_z;
            System.out.println("Z_i =>"+Z_i);

            //sending to cloud server
            DataOutputStream Usr_out=new DataOutputStream(s4.getOutputStream());
            Usr_out.writeUTF( String.valueOf( G_i ) );
            Usr_out.writeUTF(String.valueOf(F_i));
            Usr_out.writeUTF(String.valueOf(Z_i));
            Usr_out.writeUTF(String.valueOf(PID));
            Usr_out.writeUTF(String.valueOf(TS));



            dout.close();
            ss3.close();
            Usr_out.close();
            ss4.close();

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

