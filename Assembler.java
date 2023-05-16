import java.io.*;
import java.util.*;
public class Assembler {
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    static int variableNumber = 15;
    static String Line;
    static int linenum = 0;
    static HashMap <String,Integer> Builtin = new HashMap<String,Integer>();
    static HashMap<String,Integer> SymbolTable = new HashMap<String,Integer>();
    public static void CostructTable(BufferedReader myfile) throws IOException{
        int tempnum= 15;
        Builtin.put("R0", 0);
        Builtin.put("R1", 1);
        Builtin.put("R2", 2);
        Builtin.put("R3", 3);
        Builtin.put("R4", 4);
        Builtin.put("R5", 5);
        Builtin.put("R6", 6);
        Builtin.put("R7", 7);
        Builtin.put("R8", 8);
        Builtin.put("R9", 9);
        Builtin.put("R10", 10);
        Builtin.put("R11", 11);
        Builtin.put("R12", 12);
        Builtin.put("R13", 13);
        Builtin.put("R14", 14);
        Builtin.put("R15", 15);
        Builtin.put("SCREEN", 16384);
        Builtin.put("KBD", 24576);
        Builtin.put("SP", 0);
        Builtin.put("ARG", 1);
        Builtin.put("LCL", 2);
        Builtin.put("THIS", 3);
        Builtin.put("THAT", 4);
        while ((Line1 = myfile.readLine()) != null) {
            Line1 = Line1.replace(" ", ""); // use replaceAll to remove all spaces
            Line1 = Line1.trim();
            if (Line1.startsWith("//") || Line1.trim().isEmpty()) {
                continue;
            } else if (Line1.startsWith("(")) {
                int inc = Line1.indexOf(")");
                String hmm = Line1.substring(1,inc);
                SymbolTable.put(hmm, linenum);
            } else {
                if (Line1.startsWith("@")) {
                    if (isNumeric(Line1.substring(1))){
                        linenum++;
                        continue;
                    }
                    String var;
                    int commentIndex = Line1.indexOf("/");
                    if (commentIndex > 0) {

                        var = Line1.substring(1, commentIndex);
                        var = var.trim();
                    } else {
                        var = Line1.substring(1);
                        var = var.trim();
                    }
                    if (Character.isLowerCase(var.charAt(0)) && !Builtin.containsKey(var) && !SymbolTable.containsKey(var) ) {
                        variableNumber++;
                        SymbolTable.put(var, variableNumber);
                    }
                }
                linenum++;
            }
        }
    }
    public static String A_ins2Binary(String NumOrSym){

        if (isNumeric(NumOrSym)){
            int var = Integer.parseInt(NumOrSym);
            String binaryString = Integer.toBinaryString(var);
            binaryString = String.format("%16s", binaryString).replace(' ', '0');
            return binaryString;
        }
        else if(Builtin.containsKey(NumOrSym)){
           int mad = Builtin.get(NumOrSym);
            String binaryString = Integer.toBinaryString(mad);
            binaryString = String.format("%16s", binaryString).replace(' ', '0');
            return binaryString;
        }
        else {
            int label = SymbolTable.get(NumOrSym);
            String binaryString = Integer.toBinaryString(label);
            binaryString = String.format("%16s", binaryString).replace(' ', '0');
            return binaryString;
        }
    }
    public static String Cins2Binary(String CInstruction){

        HashMap<String, String> destDict = new HashMap<String, String>();
        destDict.put("", "000");
        destDict.put("M", "001");
        destDict.put("D", "010");
        destDict.put("MD", "011");
        destDict.put("A", "100");
        destDict.put("AM", "101");
        destDict.put("AD", "110");
        destDict.put("AMD", "111");

        HashMap<String, String> compDict = new HashMap<String, String>();
        compDict.put("0", "0101010");
        compDict.put("1", "0111111");
        compDict.put("-1", "0111010");
        compDict.put("D", "0001100");
        compDict.put("A", "0110000");
        compDict.put("!D", "0001101");
        compDict.put("!A", "0110001");
        compDict.put("-D", "0001111");
        compDict.put("-A", "0110011");
        compDict.put("D+1", "0011111");
        compDict.put("A+1", "0110111");
        compDict.put("D-1", "0001110");
        compDict.put("A-1", "0110010");
        compDict.put("D+A", "0000010");
        compDict.put("D-A", "0010011");
        compDict.put("A-D", "0000111");
        compDict.put("D&A", "0000000");
        compDict.put("D|A", "0010101");
        compDict.put("M", "1110000");
        compDict.put("!M", "1110001");
        compDict.put("-M", "1110011");
        compDict.put("M+1", "1110111");
        compDict.put("M-1", "1110010");
        compDict.put("D+M", "1000010");
        compDict.put("D-M", "1010011");
        compDict.put("M-D", "1000111");
        compDict.put("D&M", "1000000");
        compDict.put("D|M", "1010101");

        HashMap<String, String> jumpDict = new HashMap<String, String>();
        jumpDict.put("", "000");
        jumpDict.put("JGT", "001");
        jumpDict.put("JEQ", "010");
        jumpDict.put("JGE", "011");
        jumpDict.put("JLT", "100");
        jumpDict.put("JNE", "101");
        jumpDict.put("JLE", "110");
        jumpDict.put("JMP", "111");
        String dest = "";
        String comp = "";
        String jump = "";
        if(CInstruction.contains("=")){
            String[] destcompsplit = CInstruction.split("=");
            dest = destcompsplit[0];
            comp = destcompsplit[1];
        }
        else {
            comp = CInstruction;
        }
        if (comp.contains(";")){
            String[] compjumpsplit = CInstruction.split(";");
            comp = compjumpsplit[0];
            jump = compjumpsplit[1];
        }

        return "111" + compDict.get(comp) + destDict.get(dest) + jumpDict.get(jump) ;
    }
    static String Line1;
    public static void main(String[] args) throws IOException {
        BufferedReader myAsmFile = new BufferedReader(new FileReader("Assignment2/Fill.asm"));
        FileWriter myFinalFile = new FileWriter("assemble.hack");
        CostructTable(myAsmFile);
        myAsmFile.close();
        BufferedReader myAsmFile1 = new BufferedReader(new FileReader("Assignment2/Fill.asm"));
            while ((Line = myAsmFile1.readLine()) != null){
            Line = Line.replace(" ","");
            if (Line.trim().startsWith("//") || Line.trim().isEmpty()){
                continue;
            }
            else if(Line.trim().startsWith("(")){
                continue;
            }
            else if (Line.startsWith("@")){
                if (Line.contains("/")){
                    int hmm = Line.indexOf("/");
                    String A_ins = Line.substring(1,hmm);
                    String A_insBinary = A_ins2Binary(A_ins.trim());
                    myFinalFile.write(A_insBinary + "\n");
                }
                else {
                    String A_ins = Line.substring(1);
                    String A_insBinary = A_ins2Binary(A_ins);
                    myFinalFile.write(A_insBinary + "\n");
                }
            }
            else {
                if(Line.contains("/")){
                    int hmm1= Line.indexOf("/");
                    String C_ins = Line.substring(0,hmm1);
                    C_ins = C_ins.trim();
                    String BinaryC = Cins2Binary(C_ins);
                    myFinalFile.write(BinaryC + "\n");
                }
                else {
                    String C_inss = Line;
                    C_inss = C_inss.trim();
                    String BinaryCins = Cins2Binary(C_inss);
                    myFinalFile.write(BinaryCins + "\n");
                }
            }
        }
        System.out.println(SymbolTable);
            myFinalFile.close();
    }
}
