package com.utt.wherearemyfriends;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private String id;
    private List<Member> members;

    public Group(String name) {
        this(name, null, new ArrayList<>());
    }

    public Group(String name, List<Member> members) {
        this(name, null, members);
    }

    public Group(String name, String id, List<Member> members) {
        this.name = name;
        this.id = id;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) { this.members = members; }
}
