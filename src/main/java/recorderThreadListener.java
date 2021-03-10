public  interface recorderThreadListener {
    void onStartRecorderThread(recorderThread thread, String message);
    void onCreateFolderRecorderThread(recorderThread thread, String message);


    void onSucsessRecordRecorderThread(recorderThread thread, String fileUrl, int cameraId, long newTime);
    void onUnsuccessRecordRecorderThread(recorderThread thread, String fileUrl, int cameraId, long newTime);


    void onRecorderThreadException(recorderThread thread, Exception e);

    void onStopCameraRecorderThread(recorderThread thread, String cameraUrl, String cameraIp, String cameraId, String cameraName);

    void onStartRecordFromCamera(recorderThread thread, String message);

}
