package com.ssm.service;

import com.ssm.entity.GroupRequest;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import com.ssm.exception.*;
import com.ssm.repository.GroupRepository;
import com.ssm.repository.GroupRequestRepository;
import com.ssm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;


    @Autowired
    GroupRequestRepository groupRequestRepository;

    public GroupService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public void createGroup(String groupName, String adminUsername) throws UserNotFoundException, DataAccessException, UserAlreadyExistsException {
        Optional<User> admin = userRepository.findByUserName(adminUsername);
        if (!admin.isPresent()) {
            throw new UserNotFoundException("User Not Found when Creating the userGroup");
        }
        Optional<UserGroup> group = groupRepository.findByGroupName(groupName);

        if (group.isPresent()) {
            throw new UserAlreadyExistsException("Group Already Exists.Please try with another Name");
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(groupName);
        userGroup.setAdmin(admin.get());
        userGroup.getUsers().add(admin.get()); // Admin is automatically added to the userGroup
        groupRepository.save(userGroup);
    }

    public Boolean sendRequestToJoinGroup(String groupName, String userName) throws DataAccessException, UserAlreadyExistsException, UserNotFoundException, GroupRequestException {
        Optional<User> user = userRepository.findByUserName(userName);
        Optional<UserGroup> group = groupRepository.findByGroupName(groupName);
        if (user.isPresent() && group.isPresent()) { //check user and group present or not
            //check view user is already part of the group
            Set<User> checkUserPartOfGroup = group.get().getUsers();
            checkUserPartOfGroup.add(group.get().getAdmin());
            if (checkUserPartOfGroup.contains(user.get())) {
                throw new UserAlreadyExistsException("You Already Part of the Group you are trying to Join.");
            } else {
                Optional<GroupRequest> groupRequest = groupRequestRepository.findByUser(user.get());
                if (!groupRequest.isPresent()) { //when group is not there we send a new request
                    return sendRequest(group.get(), user.get());

                } else {
                    //if the request is rejected we will send a new request again
                    if (groupRequest.get().getStatus().equals(GroupRequest.RequestStatus.REJECTED)) {
                        int savedState = groupRequestRepository.changeStatusToPending(GroupRequest.RequestStatus.PENDING, groupRequest.get().getId());
                        return savedState != 0;
                    } else {
                        //TODO: HAVE TO DISCUSS ON THIS EXCEPTION
                        throw new GroupRequestException("Error while Processing your Group Request: Please try again");
                    }
                }
            }

        } else {
            throw new UserNotFoundException("There is No Such Group Exists.");
        }
    }

    private Boolean sendRequest(UserGroup group, User user) {
        GroupRequest newGroupRequest = new GroupRequest();
        newGroupRequest.setUserGroup(group);
        newGroupRequest.setUser(user);
        newGroupRequest.setRequestDate(new Date());
        newGroupRequest.setStatus(GroupRequest.RequestStatus.PENDING);
        GroupRequest savedGroupRequest = groupRequestRepository.save(newGroupRequest);
        return !ObjectUtils.isEmpty(savedGroupRequest);
    }


    @Transactional
    public void deleteGroup(String groupName, String userName) throws DataAccessException, UserNotFoundException {
        Optional<UserGroup> group = groupRepository.findByGroupName(groupName);
        if (group.isPresent()) {
            if (userName.equals(group.get().getAdmin().getUserName())) {
                groupRepository.deleteById(group.get().getId());
                groupRequestRepository.deleteByUserGroup(group.get());
            } else {
                throw new UserNotFoundException("Only Admin of the Group Can delete the group");
            }
        } else {
            throw new UserNotFoundException("Group did not Exist to delete");
        }
    }

    /**
     * In the phase just deleting the group if admin leaves the group
     * we will show a warning before leaving the group which means deleting the group
     **/
    public void leaveGroup(String groupName, String userName) throws LeaveGroupException, GroupNotFoundException {
        Optional<UserGroup> userGroup = groupRepository.findByGroupName(groupName);
        if (userGroup.isPresent()) {
            if (userName.equals(userGroup.get().getAdmin().getUserName())) {
                groupRepository.deleteById(userGroup.get().getId());
                groupRequestRepository.deleteByUserGroup(userGroup.get());
            } else {
                User member = userGroup.get().getUsers().stream()
                        .filter(user -> user.getUserName().equals(userName))
                        .findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(member)) {
                    userGroup.get().removeUser(member);
                    member.removeFromUserGroups(userGroup.get());
                    userRepository.save(member);
                    groupRepository.save(userGroup.get());
                    groupRequestRepository.deleteByUser(member);
                } else {
                    throw new LeaveGroupException("Unable to find the member in" + userGroup.get().getGroupName());
                }
            }
        } else {
            throw new GroupNotFoundException("Group did not Exist to delete");
        }

        //Check for admin

    }

    /**
     * REMOVE Member
     */

    public void removeMember(String groupName, String currentUser, String member) throws GroupNotFoundException, AccessException, UserNotFoundException {
        Optional<UserGroup> userGroup = groupRepository.findByGroupName(groupName);

        if (userGroup.isPresent()) {
            if (!currentUser.equals(userGroup.get().getAdmin().getUserName())) {
                throw new AccessException("Only Admin Can remove the Members of the Group");
            } else {
                User removeMember = userGroup.get().getUsers().stream()
                        .filter(user -> user.getUserName().equals(member))
                        .findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(removeMember)) {
                    userGroup.get().removeUser(removeMember);
                    removeMember.removeFromUserGroups(userGroup.get());
                    userRepository.save(removeMember);
                    groupRepository.save(userGroup.get());
                    groupRequestRepository.deleteByUser(removeMember);
                } else {
                    throw new UserNotFoundException("Unable to find the member in" + userGroup.get().getGroupName());
                }
            }
        } else {
            throw new GroupNotFoundException("Group did not Exist to delete");
        }
    }


    public List<UserGroup> getAllGroupsBasedOnUser(String adminUser) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUserName(adminUser);
        if (!user.isPresent()) throw new UserNotFoundException("System Error: No Such User Exists");
        return groupRepository.findByAdminOrUsers(user.get());
    }

    public List<GroupRequest> displayPendingRequests(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent()) throw new UserNotFoundException("System Error: No Such User Exists");
        List<UserGroup> groups = groupRepository.findByAdmin(user.get());
        List<GroupRequest> pendingRequests = new ArrayList<>();
        for (UserGroup userGroup : groups) {
            List<GroupRequest> groupRequests = groupRequestRepository.findByUserGroupAndStatus(userGroup, GroupRequest.RequestStatus.PENDING);
            pendingRequests.addAll(groupRequests);
        }
        return pendingRequests;
    }


    public Boolean requestStatusUpdate(Long groupRequestId, String status) {
        GroupRequest.RequestStatus requestStatus = "ACCEPTED".equals(status) ? GroupRequest.RequestStatus.ACCEPTED : GroupRequest.RequestStatus.REJECTED;
        int savedState = groupRequestRepository.changeStatusToPending(requestStatus, groupRequestId);
        //add the user to the group
        if (savedState != 0 && "ACCEPTED".equals(status)) {
            Optional<GroupRequest> groupRequest = groupRequestRepository.findById(groupRequestId);
            if (groupRequest.isPresent()) {
                User user = groupRequest.get().getUser();
                UserGroup userGroup = groupRequest.get().getUserGroup();
                //USER
                Set<UserGroup> userGroups = user.getUserGroups();
                userGroups.add(userGroup);
                user.setUserGroups(userGroups);
                //Group
                Set<User> groupUsers = userGroup.getUsers();
                groupUsers.add(user);
                userGroup.setUsers(groupUsers);
                //save both
                User savedUser = userRepository.save(user);
                UserGroup savedUserGroup = groupRepository.save(userGroup);
                return (!ObjectUtils.isEmpty(savedUserGroup)) && (!ObjectUtils.isEmpty(savedUser));
            } else {
                System.out.println("There is another error while finding user or UserGroup from GroupRequest");
                return false;
            }
        }
        return false;
    }


    public UserGroup getUserGroupBasedOnGroupId(Long groupId) throws GroupNotFoundException {
        return groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No Such Group Exists"));
    }

    public Set<String> getMemberOfTheGroup(UserGroup userGroup) throws GroupNotFoundException {
        UserGroup membersOfTheGroup = groupRepository.findByGroupName(userGroup.getGroupName()).orElseThrow(() -> new GroupNotFoundException("No Such Group Exists Or Session Expired"));
        Set<String> members = membersOfTheGroup.getUsers().stream().map(x -> x.getUserName()).collect(Collectors.toSet());
        members.add(membersOfTheGroup.getAdmin().getUserName());
        return members;
    }


}
