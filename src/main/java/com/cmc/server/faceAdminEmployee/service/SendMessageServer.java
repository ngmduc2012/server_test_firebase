package com.cmc.server.faceAdminEmployee.service;

import com.cmc.server.faceAdminEmployee.dto.request.RequestOfMessageDto;
import com.cmc.server.faceAdminEmployee.response.ResponseObject;
import com.cmc.server.faceAdminEmployee.response.ResponseStatus;
import com.google.api.client.util.DateTime;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SendMessageServer {

    boolean firstTime = true;

    public ResponseObject<String> sendMessage(RequestOfMessageDto request) {

        ResponseObject<String> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);

        try {
            if (this.firstTime) {
                this.setUpToken();
                this.firstTime = false;
            }
            //Tạo dữ liệu thông báo, tham khảo: https://firebase.google.com/docs/cloud-messaging/send-message#java
            List<String> registrationTokens = request.getToken();
//                    Arrays.asList(
//                    "duJ3XIcGSsKJg8mI-6IDYm:APA91bGfeIAqtulnGKU7Jn3FOxKJdI-Ws50THRJNT4ZR2jkqiDRYgDZbFdDuShy9yOcokZ3HFkraf4bJ1estfv1jfC2npT9CqdNVleJbru9KTK9T4_EC2iaFDnZ8dktlPvHAB_gT4akH"
//                    ,
//                    "cPBf3775SVadRFTWxbkqPE:APA91bHDACP37lg1t7z9tAzV15Vmmhf_mzTuB99Z8EMIAkxvJDBjXn7e1MiQYDMELLVkokPj7MrdidQnQU9XDJNmTAnDBk3kLGZI2W4e0GAHuS0f-IZEaj2ocOm1kFB9gpqe7F-DcrMc", "cPBf3775SVadRFTWxbkqPE:APA91bHDACP37lg1t7z9tAzV15Vmmhf_mzTuB99Z8EMIAkxvJDBjXn7e1MiQYDMELLVkokPj7MrdidQnQU9XDJNmTAnDBk3kLGZI2W4e0GAHuS0f-IZEaj2ocOm1kFB9gpqe7F-DcrMc",
//                    "eEU9MTzbRvupBrlfJVu6LX:APA91bFHTSsdg3i97biaqJxXi7xK8d2UjnV4hplxQTr-2pg3ugtXojjyLr8SS-cpUf88GG6VEGe-P1PmNrj_hmypB18RewQ4nEKd-l_UfcOZk_GIYfm5Ha2lMwHpUr_NnypP3uqYpDph"
//            );// See documentation on defining a message payload.

//          ///change Datetime to UNIX timestamp (1), UNIX timestamp to Date (2), Date to String (3)
            //the following:
            //(1) https://www.unixtimestamp.com/
            //(2) https://stackoverflow.com/a/3371337/10621168
            //(3) https://www.javatpoint.com/java-date-to-string
            java.util.Date time = new java.util.Date(request.getTime().getValue()); //(1), (2)
            String timeString = new SimpleDateFormat("hh:mm:ss - dd/MM/yyyy").format(time); //(3)
            //creat map follow: https://viettuts.vn/java-collection/map-trong-java
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", request.getTitle());
            map.put("body", request.getBody());
            map.put("time", timeString);
            map.put("device", request.getDeviceName());

            //The following: https://firebase.google.com/docs/cloud-messaging/send-message#customize-messages-across-platforms
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody() + " at " + timeString + " in " + request.getDeviceName())
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(request.getTtl())
                            .setNotification(AndroidNotification.builder()
                                    .setIcon(request.getIcon())
                                    .setColor(request.getColor())
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(request.getBadge())
                                    .build())
                            .build())
                    .putAllData(map)
                    .addAllTokens(registrationTokens)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            res.setData(response.getSuccessCount() + " messages were sent successfully");
        } catch (Exception exception) {
            return new ResponseObject<>(false, ResponseStatus.UNHANDLED_ERROR, exception.getMessage());
        }
        return res;
    }

    // cài đặt file sdk tại : https://firebase.google.com/docs/admin/setup#windows
    public void setUpToken() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("D:\\Dev Android Team\\civams-face-firebase-adminsdk-8k335-c86ed9fe62.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

}
