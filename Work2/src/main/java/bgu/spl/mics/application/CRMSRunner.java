package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;//IMPORT GSON -> CAN READ FILE TYPE GiSON
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {

        FileReader reader = null;
        try {
            reader = new FileReader(args[0]);
        } catch (IOException e) {
            System.out.println("Exception"); //Not found/ not good file
        }
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

        //start taking all the info from gson
        JsonArray Jstudents = object.getAsJsonArray("Students");
        Student[] students = createStudents(Jstudents);
        createModels(students, Jstudents);

        JsonArray JGpus = object.getAsJsonArray("GPUS");
        GPU[] GPUS = createGPUs(JGpus);

        JsonArray JCpus = object.getAsJsonArray("CPUS");
        CPU[] CPUS = createCPUs(JCpus);

        JsonArray JConferences = object.getAsJsonArray("Conferences");
        ConfrenceInformation[] conferences = createConferences(JConferences);

        int TickTime = object.get("TickTime").getAsInt();
        int Duration = object.get("Duration").getAsInt();


        //create cluster & messageBus -> singleton
        Cluster cluster = Cluster.getInstance();
        MessageBus messageBus = MessageBusImpl.getInstance();

        //create all the services
        for (int i = 0; i < CPUS.length; i++) {
            cluster.addCPU(CPUS[i]);
            CPUService cpuService = new CPUService(CPUS[i]);
            cpuService.run();
        }
        for (int i = 0; i < GPUS.length; i++) {
            cluster.addGPU(GPUS[i]);
            GPUService gpuService = new GPUService(GPUS[i]);
            gpuService.run();
        }
        for (int i = 0; i < conferences.length; i++) {
            ConferenceService conferenceService = new ConferenceService(conferences[i]);
            conferenceService.run();
        }
        for (int i = 0; i < students.length; i++) {
            StudentService studentService = new StudentService(students[i].getName(),students[i]);
            studentService.run();
        }

        TimeService timeService = new TimeService(TickTime,Duration);

        try {
            PrintWriter a = new PrintWriter("./output.txt");
            a.println();
            a.close();
        }catch (IOException e){
            System.out.println("IOException");
        }

    }

    //helping methods to create all the objects
    public static Student[] createStudents(JsonArray students) {
        int size = students.size();
        Student[] Students = new Student[size];
        for (int i = 0; i < size; i++) {
            JsonObject student = students.get(i).getAsJsonObject();
            Students[i] = new Student(student.get("name").getAsString(), student.get("department").getAsString(), student.get("status").getAsString());
        }
        return Students;
    }

    public static void createModels(Student[] students, JsonArray Jstudents) {
        int size = Jstudents.size();
        for (int i = 0; i < size; i++) {//students
            JsonObject student = Jstudents.get(i).getAsJsonObject();
            JsonArray model = student.getAsJsonArray("models"); //array of models
            for (int j = 0; j < model.size(); j++) { //run over all the models
                JsonObject mod = model.get(j).getAsJsonObject();
                Data data = new Data(mod.get("type").getAsString(), mod.get("size").getAsInt());
                students[i].addModel(new Model(mod.get("name").getAsString(), data, students[i]));
            }
        }
    }

    public static GPU[] createGPUs(JsonArray gpus) {
        int size = gpus.size();
        GPU[] GPUS = new GPU[size];
        for (int i = 0; i < size; i++) {
            GPUS[i] = new GPU(gpus.get(i).getAsString(), null);
        }
        return GPUS;
    }

    public static CPU[] createCPUs(JsonArray cpus) {
        int size = cpus.size();
        CPU[] CPUS = new CPU[size];
        for (int i = 0; i < size; i++) {
            CPUS[i] = new CPU(cpus.get(i).getAsInt());
        }
        return CPUS;
    }

    public static ConfrenceInformation[] createConferences(JsonArray conferences) {
        int size = conferences.size();
        ConfrenceInformation[] Conferences = new ConfrenceInformation[size];
        for (int i = 0; i < size; i++) { //conferences
            JsonObject conference = conferences.get(i).getAsJsonObject();
            Conferences[i] = new ConfrenceInformation(conference.get("name").getAsString(), conference.get("date").getAsInt());
        }
            return Conferences;
    }
}



