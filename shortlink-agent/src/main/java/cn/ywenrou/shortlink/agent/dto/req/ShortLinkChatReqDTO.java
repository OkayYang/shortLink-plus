package cn.ywenrou.shortlink.agent.dto.req;

import lombok.Data;

@Data
public class ShortLinkChatReqDTO {
    private String message;
    private String model;
}
