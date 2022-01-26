package com.cmc.server.faceAdminEmployee.controller;

import com.cmc.server.faceAdminEmployee.dto.request.RequestOfMessageDto;
import com.cmc.server.faceAdminEmployee.response.ResponseObject;
import com.cmc.server.faceAdminEmployee.service.SendMessageServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/")
@Validated
public class SendMessageController {

    SendMessageServer server = new SendMessageServer();

    /**
     *################################################################################################
     *Title:        Send (For test Firebase notify of Android)
     *Description:  Get data from api (/send) then to FireBase, FireBase will send notify contains this data
     *              to device by token of this device.
     *------------------------------------------------------------------------------------------------
     *Mô tả:        Nhận dữ liệu từ api (/send) sau đó gửi chúng tới FireBase, sau đó FireBase sẽ gửi thông
     *              báo có chứa nội dung là dữ liệu nhận từ api để hiển thị thông qua token của thiết
     *              bị đó.
     *################################################################################################
     */
    /* Test Postman with POST - body - raw - JSON, this is an example:
    {
    "token": ["eKf3ErtlTZmm6ONz7PQea1:APA91bELFdwszVEKEEBI4Esvoh6C0HYLz_no2gyy2iE94XGHlz8ddR62GtukfQPls3dHedvOmb2NKJY_fzL2G8LBV5c2mN9dYF6R9TU0JCT2yirfUzqmJm64tf38eewWlohvv4l_MkRJ"],
    "title": "Check In",
    "body": "Bạn vừa check in ",
    "ttl": 3600000,
    "icon" : "ic_baseline_notifications_active_24",
    "color" : "#f45342",
    "badge" : 42,
    "time" : "2021-01-07T00:00:00.000Z",
    "deviceName" : "Face Terminal CIST"
    }
     */
    @CrossOrigin //For accept to connect to this url (this case is allow all url access
    @PostMapping(value = "/send")
    public ResponseEntity<?> sendToFireBase(@RequestBody RequestOfMessageDto request)  {
        ResponseObject<?> res = server.sendMessageToFireBase(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }






}
