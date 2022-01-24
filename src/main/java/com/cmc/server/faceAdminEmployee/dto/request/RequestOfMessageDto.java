package com.cmc.server.faceAdminEmployee.dto.request;

import com.google.api.client.util.DateTime;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestOfMessageDto {
    private List<String> token;
    private String title;
    private String body;
    private long ttl;
    private String icon;
    private String color;
    private int badge;
    private DateTime time;
    private String deviceName;


}
