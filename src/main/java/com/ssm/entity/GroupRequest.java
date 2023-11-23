package com.ssm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "group_request")
@Getter
@Setter
@NoArgsConstructor
public class GroupRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // User who is sending the request

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup userGroup; // Group to which the user wants to join

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date")
    private Date requestDate;

    @Column(name = "status", nullable = false)
    private RequestStatus status; // Status of the request (e.g., Pending, Accepted, Rejected)

    public enum RequestStatus {
        PENDING, // Request is pending approval
        ACCEPTED, // Request has been accepted
        REJECTED // Request has been rejected
    }

    public GroupRequest(Long id, User user, UserGroup userGroup, Date
            requestDate, RequestStatus status) {
        this.id = id;
        this.user = user;
        this.userGroup = userGroup;
        this.requestDate = requestDate;
        this.status = status;
    }
}