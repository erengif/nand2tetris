import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
public class Main {

    public static void parseClass(String path){
        try {  
            JackCompiler pe = new JackCompiler();
            FileReader file = new FileReader(path);
            BufferedReader reader = new BufferedReader(file);
            String line = reader.readLine();
            String expresion = "";
            
        while(line != null){
            expresion += line + "\n";
            line = reader.readLine();
        }

        pe.parser(expresion);
       // System.out.println(pe.getXml());
        String str = pe.getXml();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.substring(0,path.length() -4) + "xml"));
        writer.write(str);
        writer.close();
        
            
        } catch (JackParserException cepe) {
            System.err.println(cepe);
            //System.exit(1);
           
        } catch (Exception e) {
            System.err.println(e);
            //System.exit(1);

        }
    }
    
    public static void main(String[] args){
        File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        if(!folder.isFile()){
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (file.getName().substring(file.getName().length()-4).equals("jack")){
                        System.out.println(file.getName());
                        parseClass(args[0]+"/"+file.getName());
                    }
                }
            }  
            return;
        }
        else{
            parseClass(args[0]);
        }
    }
}