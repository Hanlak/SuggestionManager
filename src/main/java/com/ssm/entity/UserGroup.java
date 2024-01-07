package com.ssm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UserGroup")
@Getter
@Setter
@NoArgsConstructor
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "GroupName", nullable = false, length = 255)
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @ManyToMany(mappedBy = "userGroups")
    private Set<User> users = new HashSet<>();

    // Method to remove a user from the user group
    public void removeUser(User user) {
        this.getUsers().remove(user);
        user.getUserGroups().remove(this);
    }

}
