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

    /**
     *################################################################################################
     *Title:        Send message to FireBase
     *Description:  (I). Setup Token for server in the first time.
     *              (II). Create form message (contain information and data)
     *              (III). The message send to FireBase
     *              (IV). return result of api the number of the success messages
     *------------------------------------------------------------------------------------------------
     *Mô tả:        (I). Cài đặt token cho server trong lần đầu tiên.
     *              (II). Tạo cấu trúc tin nhắn (Gồm thông tin và dữ liệu)
     *              (III). Gửi tin nhắn tới FireBase
     *              (IV). trả về kết quả cho api số lượng tin nhắn được truyền thành công.
     *################################################################################################
     */
    public ResponseObject<String> sendMessageToFireBase(RequestOfMessageDto request) {
        ResponseObject<String> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
        try {
            //(I)
            if (this.firstTime) {
                this.setUpToken();
                this.firstTime = false;
            }
            //(II)
            //Tạo dữ liệu thông báo, tham khảo: https://firebase.google.com/docs/cloud-messaging/send-message#java
            List<String> registrationTokens = request.getToken();

//          ///change Datetime to UNIX timestamp (1), UNIX timestamp to Date (2), Date to String (3)
            //the following:
            //  (1) https://www.unixtimestamp.com/
            //  (2) https://stackoverflow.com/a/3371337/10621168
            //  (3) https://www.javatpoint.com/java-date-to-string
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
            //(III)
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            //(IV)
            res.setData(response.getSuccessCount() + " messages were sent successfully");
        } catch (Exception exception) {
            return new ResponseObject<>(false, ResponseStatus.UNHANDLED_ERROR, exception.getMessage());
        }
        return res;
    }

    // cài đặt file sdk tại : https://firebase.google.com/docs/admin/setup#windows
    public void setUpToken() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("..\\civams-face-d7e68-firebase-adminsdk-z1qwf-dd88b6509c.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

}
