package cn.ywenrou.shortlink.console.dto.resp;

import lombok.Data;

import java.util.Date;

@Data
public class GroupInfoRespDTO {
    private String gid;
    private String name;
    private String description;
    private String tag;
    private Integer sortOrder;
    private Date createTime;
}
