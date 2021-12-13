package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.*;
import com.google.gson.*;//IMPORT GSON -> CAN READ FILE TYPE GiSON
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {

        FileReader reader= null;
        try{
            reader= new FileReader(args[0]);
        } catch (IOException e) {
            System.out.println(""); //Not found/ not good file
            //exit - do not have a file empty System.exit(0);
        }

        JsonObject object= JsonParser.parseReader(reader).getAsJsonObject();

        //start taking all the info from gson
        JsonArray Jstudents = object.getAsJsonArray("Students");
        Student[] students = createStudents(Jstudents);
        ArrayList<Model> models = createModels(students,Jstudents);

        JsonArray JGpus = object.getAsJsonArray("GPUS");
        GPU[] GPUS = createGPUs(JGpus);

        JsonArray JCpus = object.getAsJsonArray("CPUS");
        CPU[] CPUS = createCPUs(JCpus);

        JsonArray JConferences = object.getAsJsonArray("Conferences");
        CPU[] conferences = createconferences(JConferences);

        int TickTime = object.get("TickTime").getAsInt();
        int Duration = object.get("Duration").getAsInt();
    }

    //helping methods to create all the objects
    public static Student[] createStudents(JsonArray students){
        int size = students.size();
        Student[] Students = new Student[size];
        for(int i=0; i<size ; i++){
            JsonObject student = students.get(i).getAsJsonObject();
            Students[i] = new Student(student.get("name").getAsString(), student.get("department").getAsString(),
                    student.get("status").getAsString());
        }
        return Students;
    }

    public static ArrayList <Model> createModels(Student[] students, JsonArray Jstudents){
        ArrayList<Model> models = new ArrayList<Model>();
        int size = Jstudents.size();
        for(int i = 0; i < size; i++){//students
            JsonObject student = Jstudents.get(i).getAsJsonObject();
            JsonArray model = student.getAsJsonArray("models"); //array of models
            for(int j = 0; j<model.size() ; j++){ //run over all the models
                JsonObject mod = model.get(j).getAsJsonObject();
                Data data = new Data(mod.get("type").getAsString(), mod.get("size").getAsInt());
                models.set(i, new Model(mod.get("name").getAsString(), data, students[i]));
            }
        }
        return models;
    }

    public static GPU[] createGPUs(JsonArray gpus){
        int size = gpus.size();
        GPU[] GPUS = new GPU[size];
        for(int i = 0; i<size ; i++){
            GPUS[i] = new GPU(gpus.get(i).getAsString(), null);
        }
        return GPUS;
    }

    public static CPU[] createCPUs(JsonArray cpus){
        int size = cpus.size();
        CPU[] CPUS = new CPU[size];
        for(int i = 0; i<size ; i++){
            CPUS[i] = new CPU(cpus.get(i).getAsInt());
        }
        return CPUS;
    }


    public static CPU[] createconferences(JsonArray conferences){

    }


}
