package cn.ywenrou.shortlink.console.dto.req;

import lombok.Data;

@Data
public class GroupUpdateReqDTO {
    private String gid;
    private String name;
    private String description;
    private String tag;
    private Integer sortOrder;
}
