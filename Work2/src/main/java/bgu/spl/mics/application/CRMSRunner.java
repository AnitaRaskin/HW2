package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;//IMPORT GSON -> CAN READ FILE TYPE GiSON
import java.io.*;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws IOException {

        //<editor-fold desc="input-file>
        FileReader reader = null;
        try {
            reader = new FileReader(args[0]);
        } catch (IOException e) {
            System.out.println("Exception"); //Not found/ not good file
            System.exit(0);//exit - do not have a file empty
        }
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

        //start taking all the info from gson
        JsonArray JsonStudents = object.getAsJsonArray("Students");
        Student[] students = createStudents(JsonStudents);
        Model[] models = createModels(students, JsonStudents);

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
        for(CPU cpu:CPUS){
            cluster.addCPU(cpu);
            CPUService cpuService = new CPUService(cpu);
            Thread t = new Thread(cpuService);
            t.start();
        }
        for(GPU gpu:GPUS){
            cluster.addGPU(gpu);
            GPUService gpuService = new GPUService(gpu);
            Thread t = new Thread(gpuService);
            t.start();
        }
        for(ConfrenceInformation confrenceInformation:conferences){
            ConferenceService conferenceService = new ConferenceService(confrenceInformation);
            Thread t = new Thread(conferenceService);
            t.start();
        }
        for(Student student:students){
            StudentService studentService = new StudentService(student.getName(), student);
            Thread t = new Thread(studentService);
            t.start();
        }
        TimeService timeService = new TimeService(TickTime, Duration);
        Thread thread = new Thread(timeService);
        thread.start();

        //</editor-fold>

        //<editor-fold desc="output-file>
        File file = new File("./output.txt");
        FileWriter writer = new FileWriter(file);
        PrintWriter print = new PrintWriter(writer);
        print.println("{");
        print.println("    \"students\": [");
        print.println("        {");
        for(Student student:students) {
            print.println("            \"name\": " + student.getName() + ",");
            print.println("            \"department\": " + student.getDepartment() + ",");
            print.println("            \"status\": " + student.getStatus() + ",");
            print.println("            \"publications\": " + student.getPublications() + ",");
            print.println("            \"papersRead\": " + student.getPapersRead() + ",");
            print.println("            \"trainedModels\": [");
            for (Model model : models) {
                print.println("                {");
                print.println("                    \"name\": " + model.getName() + ",");
                print.println("                    \"name\": " + model.getName() + ",");
                print.println("                    \"data\": {");
                print.println("                        \"type\": " + model.getData().getType() + ",");
                print.println("                        \"size\": " + model.getData().getSize());
                print.println("                    },");
                print.println("                    \"status\": " + model.getStatus() + ",");
                print.println("                    \"results\": " + model.getResult());
                print.println("                },"); // check not everyone should get it!!!!!!!!!!!!!
            }
            print.println("        },");
        }
        print.println("    ],"); //end of all the students
        print.print("    \"conferences\": [");
        for(ConfrenceInformation confrenceInformation:conferences) {
            print.println("        {");
            print.println("            \"name\": " + confrenceInformation.getName() + ",");
            print.println("            \"date\": " + confrenceInformation.getDate() + ",");
            if(confrenceInformation.getConInfo().isEmpty()){ // no good models
                print.println("            \"publications\": []");
            }
            else {
                print.println("            \"publications\": [");
                for (Model model: confrenceInformation.getConInfo()){
                    print.println("                {");
                    print.println("                    \"name\": " + model.getName() +",");
                    print.println("                    \"data\": {");
                    print.println("                        \"type\": " + model.getData().getType()+",");
                    print.println("                        \"size\": " + model.getData().getSize());
                    print.println("                    },");
                    print.println("                    \"status\": " +model.getStatus() +",");
                    print.println("                    \"results\": " +model.getResult());
                    print.println("                }");
                }
                print.println("            ]");
            }
            print.println("        },");
        }//end of conf
        print.println("    ],");

        //Help function
        int cpuTime=0,gpuTime = 0;
        for(CPU cpu:CPUS){
            cpuTime = cpuTime + cpu.getRunTime();
        }
        for(GPU gpu:GPUS){
            gpuTime = gpuTime + gpu.getRunTime();
        }

        print.println("    \"cpuTimeUsed\": " + cpuTime + ",");
        print.println("    \"gpuTimeUsed\": " + gpuTime + ",");
        print.println("    \"batchesProcessed\": " +cluster.getDataBatchSize() );
        print.println("}");
        //</editor-fold>
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

    public static Model[] createModels(Student[] students, JsonArray JsonStudents) {
        int size = JsonStudents.size();
        Model[] models = new Model[size];
        for (int i = 0; i < size; i++) {//students
            JsonObject student = JsonStudents.get(i).getAsJsonObject();
            JsonArray model = student.getAsJsonArray("models"); //array of models
            for (int j = 0; j < model.size(); j++) { //run over all the models
                JsonObject mod = model.get(j).getAsJsonObject();
                Data data = new Data(mod.get("type").getAsString(), mod.get("size").getAsInt());
                models[i] = new Model(mod.get("name").getAsString(), data, students[i]);
                students[i].addModel(models[i]);
            }
        }
        return models;
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



