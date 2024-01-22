package com.ssm.dto;

import com.ssm.entity.UserGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserGroupDTO {
    private long id;
    private String groupName;
    private String description;
    private UserGroup.GroupType groupType;
    private UserGroup.Subscription subscription;


    public UserGroupDTO(UserGroup userGroup) {
        this.id = userGroup.getId();
        this.groupName = userGroup.getGroupName();
        this.description = userGroup.getDescription();
        this.groupType = userGroup.getGroupType();
        this.subscription = userGroup.getSubscription();
    }
}