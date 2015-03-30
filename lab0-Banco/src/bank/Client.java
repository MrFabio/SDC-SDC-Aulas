package bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) throws IOException {

        BankStub bstub = new BankStub();

        String inputLine;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        System.out.println("--------------------");
        System.out.println("---   Bank Ops   ---");
        System.out.println("--------------------");
        System.out.println("- 1 : Get Balance  -");
        System.out.println("- 2 : Move Money   -");
        System.out.println("- 9 : Quit         -");
        System.out.println("--------------------\n\n");

        while (true) {

            System.out.print("#Op:");
            inputLine = br.readLine();

            switch (Integer.parseInt(inputLine)) {
                case 1:
                    int balance = bstub.getBalance();
                    if (balance >= 0) {
                        System.out.println("Current Balance: " + balance + "\n");
                    } else {
                        System.out.println("ERROR getting Current Balance!\n");
                    }
                    break;

                case 2:
                    System.out.print("How many?:");
                    inputLine = br.readLine();
                    int ammount = Integer.parseInt(inputLine);
                    boolean ok = bstub.move(ammount);
                    if (ok) {
                        System.out.println(ammount + " moved successfully");
                    } else {
                        System.out.println("ERROR moving " + ammount + "\n");
                    }
                    break;

                case 9:
                    bstub.quit();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Operation!");
                    break;

            }

        }

    }

}
