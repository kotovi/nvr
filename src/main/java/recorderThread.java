import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;

public class recorderThread extends Thread {
   // finalCameraUrls, finalCameraIp, finalCameraId)
    private String cameraUrl;
    private String cameraId;
    private String cameraIp;
    private String cameraName;
    private recorderThreadListener listener;
    private  static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");
    private  static DateFormat dateFormatForFolder = new SimpleDateFormat("yyyy-MM-dd");



    public recorderThread(recorderThreadListener listener, String cameraUrl, String cameraIp, String cameraId, String cameraName ) {
        this.cameraIp = cameraIp;
        this.cameraUrl = cameraUrl;
        this.cameraId = cameraId;
        this.listener = listener;
        this.cameraName = cameraName;
        start();
    }
        @Override
        public void run(){
            Runtime r = Runtime.getRuntime();
            Process p = null;
            listener.onStartRecorderThread(this,"поток запущен");

            try {
                while (!isInterrupted()) {
                    long newTime = System.currentTimeMillis();


                    String fileRoot = "/var/nvr/";
                    //создаем в корневой директории каталог с текущей датой для хранения каталогов с камер
                    String directoryToRecord = fileRoot.concat(dateFormatForFolder.format(newTime));
                    File directory = new File(directoryToRecord);
                        if (!directory.exists()){
                            directory.mkdir();
                            listener.onCreateFolderRecorderThread(this, "Создан новый каталог: " + directoryToRecord);
                         }
                    fileRoot = directoryToRecord.concat("/");
                        //создаем каталог для хранения записи с камеры
                    String directoryToRecord2 = fileRoot.concat(cameraName);
                    File directory2 = new File(directoryToRecord2);
                        if (!directory2.exists()){
                             directory2.mkdir();
                             listener.onCreateFolderRecorderThread(this, "Создан новый каталог: " + directoryToRecord2);
                         }
                         //создаем имя для файла, в котором будет храниться запись
                    String fileUrl = directoryToRecord2.concat("/").concat(cameraName)+dateFormat.format(newTime)+".avi";
                    String newExec = "ffmpeg -i "+ cameraUrl + " -acodec copy -vcodec copy  -t 00:05:00 "+fileUrl;

                    //listener.onStartRecordFromCamera(this, "Начинаем запись  потока в файл: " + fileUrl);
                    listener.onStartRecordFromCamera(this, "Начинаем запись потока "+this.getName()+" в файл: " + fileUrl);




                    //Запускаем захват потока с помощью ffmpeg и ожидаем окончания его выполнения
                    try {   p = r.exec(newExec);
                        InputStream stderr = p.getErrorStream();
                        InputStreamReader isr = new InputStreamReader(stderr);
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        System.out.println("<ERROR>");
                        while ( (line = br.readLine()) != null) System.out.println("Ошибка с камерой :"+ cameraName+" :"+line);


                        int exitVal = p.waitFor();
                        System.out.println("Process exitValue: " + exitVal);

                          //  if (p.waitFor(310, TimeUnit.SECONDS)) p.destroyForcibly();



                    } catch (Throwable e) {
                        e.printStackTrace();
                       // listener.onRecorderThreadException(this, e);
                        listener.onStopCameraRecorderThread(this, cameraUrl, cameraIp, cameraId, cameraName);
                        interrupt();
                    }

                    if (p.exitValue()==0){
                        listener.onSucsessRecordRecorderThread(this, fileUrl, Integer.parseInt(cameraId), newTime);
                    } else {
                        listener.onUnsuccessRecordRecorderThread(this, fileUrl, Integer.parseInt(cameraId), newTime);
                    }
                }
                Exception e = null;
                listener.onStopCameraRecorderThread(this, cameraUrl, cameraIp, cameraId, cameraName);

            } catch (Exception e) {
                listener.onRecorderThreadException(this, e);
                listener.onStopCameraRecorderThread(this, cameraUrl, cameraIp, cameraId, cameraName);
            }
        }


    }


