import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Vector;

public class newNvr  implements recorderThreadListener{
    Vector<recorderThread> cameras = new Vector<>();
    recorderThread RecorderThread;
    private static DataSource ds;


    //Собственно самый главный метод
    public static void main (String[] args) {
        ds = new sqlConnectionPool().getDataSource();
        new newNvr().startRecord();

    }

    //Получаем из базы данные о имеющихся камерах и создаем потоки, записывающие видео
    public  void startRecord(){
        String[][] cameraUrls = sqlConnectionPool.getCamera(ds) ;
        for (int i = 0; i < cameraUrls.length; i++) {

            RecorderThread = new recorderThread(this, cameraUrls[i][0], cameraUrls[i][2], cameraUrls[i][1], cameraUrls[i][3]);
            System.out.println("Создан поток :"+RecorderThread.getName());
            //рассинхроним выполнение потоков при первом запуске
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


//методы recorderThreadListener
    //Наполняем вектор созданными потоками и сообщаем о запуске потока
    @Override
    public void onStartRecorderThread(recorderThread thread, String message) {
        cameras.add(thread);
        System.out.println(message);

    }
    //Обрабатываем завершение выполнения  потока
    @Override
    public void onStopCameraRecorderThread(recorderThread thread, String cameraUrl, String cameraIp, String cameraId, String cameraName) {
        cameras.remove(thread);
        RecorderThread = new recorderThread(this, cameraUrl, cameraIp, cameraId, cameraName);


    }
    //обрабатываем создание каталога
    @Override
    public void onCreateFolderRecorderThread(recorderThread thread, String message) {
        System.out.println(message);

    }
    //В том случае если процесс завершился успешно
    @Override
    public void onSucsessRecordRecorderThread(recorderThread thread, String fileUrl, int cameraId, long newTime) {
        sqlConnectionPool.addRecord(cameraId, fileUrl, newTime, 1, ds);
        sqlConnectionPool.AddStatus(cameraId, 1,ds);
        System.out.println("Запись в потоке "+thread.getName() +" произведена успешно");


    }
    //В том случае если процесс завершился неудачно, но не упал с ошибкой
    @Override
    public void onUnsuccessRecordRecorderThread(recorderThread thread, String fileUrl, int cameraId, long newTime) {
        sqlConnectionPool.addRecord(cameraId, fileUrl, newTime, 0, ds);
        sqlConnectionPool.AddStatus(cameraId, 0, ds);
        System.out.println("Запись в потоке "+thread.getName() +" сомнительна");


    }
    //выводим ошибку
    @Override
    public void onRecorderThreadException(recorderThread thread, Exception e) {
            System.out.println("Поток " + thread.getName() + " завершился с ошибкой:");
            e.printStackTrace();

    }

    //onStartRecordFromCamera
    @Override
    public void onStartRecordFromCamera(recorderThread thread, String message) {
        System.out.println(message);

    }
}
